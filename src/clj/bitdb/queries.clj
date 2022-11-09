(ns bitdb.queries
  (:require [hugsql.core :as hugsql]
            [hugsql.adapter.next-jdbc :as next-adapter]
            [io.pedestal.log :as log]
            [com.stuartsierra.component :as component]))

(def ^:private fn-suffix "-sqlvec")

(defn get-fn
  [fns datasource]
  (fn call-fn
    ([fn-name]
     (call-fn fn-name {}))
    ([fn-name opts]
     (let [_fn (get-in fns [fn-name :fn])
           sqlvec? (clojure.string/ends-with? fn-name fn-suffix)]
       (if sqlvec?
         (_fn opts)
         (_fn (datasource) opts))))))

(defn query
  [queries fn-name opts]
  (let [call-db-fn (:call-db-fn queries)
        call-sqlvec-fn (:call-sqlvec-fn queries)
        sqlvec-fn-name (keyword (str (name fn-name) fn-suffix))
        sqlvec (call-sqlvec-fn sqlvec-fn-name opts)]
    (log/debug :sql sqlvec)
    (call-db-fn fn-name opts)))

(defrecord Queries [queries-file-path datasource]
  component/Lifecycle
  (start [this]
    (let [db-fns (hugsql/map-of-db-fns queries-file-path
                   {:adapter (next-adapter/hugsql-adapter-next-jdbc)
                    :quoting :mysql
                    :fn-suffix fn-suffix})
          sqlvec-fns (hugsql/map-of-sqlvec-fns queries-file-path
                       {:adapter (next-adapter/hugsql-adapter-next-jdbc)
                        :quoting :mysql
                        :fn-suffix fn-suffix})]
      (assoc this
             :db-fns db-fns
             :sqlvec-fns sqlvec-fns
             :call-db-fn (get-fn db-fns datasource)
             :call-sqlvec-fn (get-fn sqlvec-fns datasource))))
  (stop [this]
    (-> this
        (dissoc :db-fns)
        (dissoc :sqlvec-fns)
        (dissoc :call-db-fn)
        (dissoc :call-sqlvec-fn))))

(defn new-queries []
  {:queries (component/using (map->Queries {:queries-file-path "sql/queries.sql"})
                             [:datasource])})


