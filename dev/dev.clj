(ns dev
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application.

  Call `(reset)` to reload modified code and (re)start the system.

  The system under development is `system`, referred from
  `com.stuartsierra.component.repl/system`.

  See also https://github.com/stuartsierra/component.repl"
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as string]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh refresh-all clear]]
   [com.stuartsierra.component :as component]
   [com.stuartsierra.component.repl :refer [reset set-init start stop system]]
   [bitdb.system :refer [new-system]]
   [bitdb.config :refer [load-config]]
   [environ.core :refer [env]]
   [bitdb.graphql-schema :as qs]
   [com.walmartlabs.lacinia :as l]
   ))

;; Do not try to load source code from 'resources' directory
(clojure.tools.namespace.repl/set-refresh-dirs "dev" "src" "test")


(def schema (qs/load-schema nil))

(defn q
  [query-string]
  (l/execute schema query-string nil nil))

(defn dev-system
  []
  (new-system (load-config env)))

(defn get-db-spec
  [config]
  {;:jdbcUrl {:bb-db-jdbcurl config}
     :user (:bb-db-user config)
     :password (:bb-db-pass config)
     :dbtype "mysql"
     :dbname (:bb-db-name config)
     :port (:bb-db-port config)
     :host (:bb-db-host config)})



(set-init (fn [_] (dev-system)))



