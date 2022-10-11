(ns bitdb.database
  (:require [com.stuartsierra.component :as component]))

(defrecord Database [host port user password dbname connection]
  component/Lifecycle
  (start [this]
    (println ";; Starting database")
    (let [conn (str host port)]
      (assoc this :connection conn)))
  (stop [this]
    (println ";; Stopping database")
    ;(.close (:connection this))
    (assoc this :connection nil)))

(defn new-database [config]
  (map->Database config))
