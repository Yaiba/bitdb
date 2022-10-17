(ns bitdb.database
  (:require [com.stuartsierra.component :as component]))

(defprotocol DBQuery
  (select [db query]))

(defrecord Database [config conn]
  component/Lifecycle
  (start [this]
    (println ";; Starting database")
    (let [_conn (str config)]
      (assoc this :conn conn)))
  (stop [this]
    (println ";; Stopping database")
    ;(.close (:conn this))
    (when conn
      (try
        ;;(.close conn)
        (println "close databse")
        (catch Exception e
          (println e "Error while stopping database"))))
    (assoc this :conne nil))

  DBQuery
  (select [_ query]
    ))

(defn new-database
  "Returns database component from config"
  [config]
  (map->Database {:config config}))


(defn query [q]
  ())
