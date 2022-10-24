(ns bitdb.config
  (:require [com.stuartsierra.component :as component]
            [next.jdbc.connection :refer [jdbc-url]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [expound.alpha :as expound]
            [environ.core :as env])
  (:import clojure.lang.Keyword))

(defmulti ^:private app-defaults
  "Global application defaults"
  {:bb-run-mode   "dev"
   :bb-db-type    "mysql"
   :bb-jetty-port "3000"
   :bb-jetty-join "false"})

(defn config-str
  [k]
  (let [k (keyword k)
        v (k env/env)]
    (or (when-not (str/blank? v) v)
        (k app-defaults))))

(defn config-kw
  ^Keyword [k]
  (some-> k config-str keyword))

(defn is-dev?
  []
  (= :dev (config-kw :bb-run-mode)))

(defn is-prod?
  []
  (= :prod (config-kw :bb-run-mode)))

(defn is-test?
  []
  (= :test (config-kw :bb-run-mode)))

;; specs
(def ip-regex #"^([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])$")

(s/def ::significant-string (s/and string? #(not= % "")))
(s/def ::->int
  (s/conformer
   (fn [value]
     (cond
       (int? value) value
       (string? value)
       (try (Integer/parseInt value)
            (catch Exception e
              ::s/invalid))
       :else ::s/invalid))))
(s/def ::->bool
  (s/conformer
   (fn [value]
     (cond
       (boolean? value) value
       (string? value)
       (try (Boolean/parseBoolean value)
            (catch Exception e
              ::s/valid))
       :else ::s/invalid))))
(s/def ::->keyword
  (s/conformer
   (fn [value]
     (cond
       (and (string? value) (seq value))
       (keyword value)
       :else ::s/invalid))))
(s/def ::ip-addr (s/and ::significant-string
                        #(or (= % "localhost") (re-matches ip-regex %))))
(s/def ::->env ;; use filed value as env key
  (s/conformer
   (fn [varname]
     (or (System/getenv varname) ::s/invalid))))

(s/def ::bb-run-mode ::->keyword)

(s/def ::bb-jetty-port (s/and ::->int #(<= 1024 % 65535)))
(s/def ::bb-jetty-host ::ip-addr)
(s/def ::bb-jetty-join ::->bool)
(s/def ::bb-server
  (s/keys :req-un [::bb-jetty-port
                   ::bb-jetty-host]
          :opt-un [::bb-jetty-join]))

(s/def ::bb-db-user ::significant-string)
(s/def ::bb-db-pass ::significant-string)
(s/def ::bb-db-name ::significant-string)
(s/def ::bb-db-host ::ip-addr)
(s/def ::bb-db-port ::->int)
(s/def ::bb-db-jdbcurl ::significant-string)
(s/def ::bb-db
  (s/keys :req-un [::bb-db-user
                   ::bb-db-pass
                   ::bb-db-host
                   ::bb-db-port
                   ::bb-db-name
                   ::bb-db-jdbcurl]))
(s/def ::config
  (s/keys :req-un [::bb-db
                   ::bb-server
                   ::bb-run-mode]))

(defn spec->keys
  "Convert spec coll to vector of keys"
  [spec-keys]
  (let [form (s/form spec-keys)
        params (apply hash-map (rest form))
        {:keys [req opt req-un opt-un]} params
        ->unqualify (comp keyword name)]
    (concat req
            opt
            (map ->unqualify opt-un)
            (map ->unqualify req-un))))

(defn repl-die
  [code template & args]
  (let [out (if (zero? code) *out* *err*)]
    (binding [*out* out]
      (println (apply format template args)))))

(defn die!
  [code template & args]
  (let [out (if (zero? code) *out* *err*)]
    (binding [*out* out]
      (println (apply format template args))))
  (System/exit code))

(def ^:dynamic *die-fn* repl-die)

(when (and (not (is-prod?))
           (.exists (io/file ".lein-env")))
  (println "Using env vars from .lein-env"))

(defn- coerce-config
  [config ^Keyword spec]
  (let [result (s/conform spec config)]
    (if (s/invalid? result)
      (let [report (expound/expound-str spec config)]
        (*die-fn* 1 "Invalid config values: %s %s"
                  \newline report))
      result)))

(defn load-sub-config-map
  [env ^Keyword spec]
  (let [cfg-keys (spec->keys spec)]
    (select-keys env cfg-keys)))

(defn env-sub-config
  "Load sub config from env. 
   Return map if it is a key coll, single value if it is a single key"
  [env k]
  (let [qk (keyword "bitdb.config" (name k))
        key-coll? (#{:req :req-un :opt :opt-un :gen} (second (s/form qk)))]
    (if key-coll?
      (select-keys env (spec->keys qk))
      (env k))))

(defn load-sub-config
  "Load sub config by k, return a map."
  [env k]
  (let [qk (keyword "bitdb.config" (name k))
        config (env-sub-config env k)]
    {k (coerce-config config qk)}))

(defn load-config
  "Load config from env by spec keys"
  [env]
  (binding [*die-fn* (if (is-prod?) die! repl-die)]
    (let [cfg-keys (spec->keys ::config)
          load-fn (partial load-sub-config env)]
      (into {} (map load-fn cfg-keys)))))

(defrecord Config [config]
  component/Lifecycle
  (start [this]
    (assoc this :config (load-config env/env)))
  (stop [this]
    (assoc this :config nil)))

(defn new-config
  []
  {:config (map->Config {})})
