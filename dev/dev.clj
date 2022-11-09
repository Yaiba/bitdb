(ns dev
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application.

  Call `(reset)` to reload modified code and (re)start the system.

  The system under development is `system`, referred from
  `com.stuartsierra.component.repl/system`.

  See also https://github.com/stuartsierra/component.repl"
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
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
   [com.walmartlabs.lacinia :as l]
   ))

;; Do not try to load source code from 'resources' directory
(clojure.tools.namespace.repl/set-refresh-dirs "dev" "src" "test")

(defn load-schema-file
  "Load schema from resource."
  []
  (-> (io/resource "bit-schema.edn")
      slurp
      edn/read-string))

(defn q
  [query-string]
  (-> system
      :schema-provider
      :schema
      (l/execute query-string nil nil)))

(comment (q "{ account_info_by_account(account: \"thefirst💯registeredbydevteamtoensuredassuccessfullylaunched20.bit\") {status manager records_info {key value} }}")
         (q "{ rebate_info_by_account(invitee_account: \"thefirst💯registeredbydevteamtoensuredassuccessfullylaunched20.bit\") {inviter_account}}")
         (q "{ trade_deal_info_by_account(account: \"bonjour.bit\") {account deal_type price_usd}}")
         )

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



