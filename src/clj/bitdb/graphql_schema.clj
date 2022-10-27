(ns bitdb.graphql-schema
  (:require [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.stuartsierra.component :as component]
            [bitdb.database :as database]
            [clojure.edn :as edn]))

;; account-id = blake2b(b'ACCOUNT.bit', person="ckb-default-hash", digest-size=32)

(defn record-info-by-account
  [component]
  (fn [_ args _]
    (database/list-record-by-account (:database component) (:account args))))

(defn resolver-map
  [component]
  {:query/record-info-by-account (record-info-by-account component)})

(defn load-schema
  "Load schema from resource."
  [component]
  (-> (io/resource "bit-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map component))
      schema/compile))

(defrecord SchemaProvider [schema mockdb]
  component/Lifecycle
  (start [this]
    (assoc this :schema (load-schema this)))
  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (component/using (map->SchemaProvider {})
                                     [:database])})
