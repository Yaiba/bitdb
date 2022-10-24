(ns bitdb.graphql-schema
  (:require [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.stuartsierra.component :as component]
            [clojure.edn :as edn]))

;; account-id = blake2b(b'ACCOUNT.bit', person="ckb-default-hash", digest-size=32)

(defn resolve-records-by-account
  [bit-map context args value]
  (let [{:keys [account]} args]
    (get bit-map account)))

(defn resolver-map
  [component]
  (let [bit-data (-> (io/resource "data/bit-data.edn")
                     slurp
                     edn/read-string)
        bit-map (->> bit-data
                     :records
                     (reduce #(assoc %1 (:account %2) (conj (get %1 (:account %2) []) %2)) {}))]
    {:query/general-query (fn [context args value] nil)
     :query/records-by-account (partial resolve-records-by-account bit-map)}))


(defn load-schema
  "Load schema from resource."
  [component]
  (-> (io/resource "bit-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map component))
      schema/compile))

(defrecord SchemaProvider [schema]
  component/Lifecycle
  (start [this]
    (assoc this :schema (load-schema this)))
  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (map->SchemaProvider {})})
