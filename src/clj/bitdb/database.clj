(ns bitdb.database
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as jsql]
            [next.jdbc.specs :as jspec]
            [next.jdbc.connection :as connection])
  (:import [com.zaxxer.hikari HikariDataSource]))

(def db-type "mysql")

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
