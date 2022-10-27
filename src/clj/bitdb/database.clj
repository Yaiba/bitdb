(ns bitdb.database
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as jsql]
            [next.jdbc.specs :as jspec]
            [next.jdbc.connection :as connection]
            [next.jdbc.result-set :as rs]
            [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import [com.zaxxer.hikari HikariDataSource]))

(def db-type "mysql")

(defn list-record-by-account
  "Return account's records from database."
  [ds account]
  (jsql/query
   (ds)
   ["select `account_id`, `account`, `key`, `type`, `label`, `value`, `created_at`, `updated_at` from `t_records_info` where account = ?" account]
   ;; result set need to be unqualified
   {:builder-fn rs/as-unqualified-lower-maps}))

(defn find-account-by-account
  [component account])

(defn new-database
  [config]
  (let [db-spec {:jdbcUrl (:bb-db-jdbcurl config)
                 :username (:bb-db-user config)
                 :password (:bb-db-pass config)
                 :dbtype db-type
                 :dbname (:bb-db-name config)
                 :port (:bb-db-port config)
                 :host (:bb-db-host config)}]
    {:database (connection/component HikariDataSource db-spec)}))


;; instrument specifications
;; Require the `next.jdbc.specs`
(comment ;; 
  (jspec/instrument)
  (jspec/unstrument))

(defn query [q]
  ())
