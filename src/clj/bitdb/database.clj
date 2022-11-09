(ns bitdb.database
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as jsql]
            [next.jdbc.specs :as jspec]
            [next.jdbc.connection :as connection]
            [next.jdbc.result-set :as rs]
            [io.pedestal.log :as log]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import [com.zaxxer.hikari HikariDataSource]))

(def db-type "mysql")

(defn ^:private query
  [ds statement]
  (let [[sql & params] statement]
    (log/debug :sql (str/replace sql #"\s+" " ")
               :params params))
  (jsql/query (ds) statement
              ;; result set need to be unqualified
              {:builder-fn rs/as-unqualified-lower-maps}))

(defn new-datasource
  [config]
  (let [db-spec {:jdbcUrl (:bb-db-jdbcurl config)
                 :username (:bb-db-user config)
                 :password (:bb-db-pass config)
                 :dbtype db-type
                 :dbname (:bb-db-name config)
                 :port (:bb-db-port config)
                 :host (:bb-db-host config)}]
    {:datasource (connection/component HikariDataSource db-spec)}))


;; instrument specifications
;; Require the `next.jdbc.specs`
(comment ;;
  (jspec/instrument)
  (jspec/unstrument))

