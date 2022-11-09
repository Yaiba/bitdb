(ns bitdb.graphql-schema
  (:require [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.stuartsierra.component :as component]
            [bitdb.queries :refer [query]]
            [clojure.string :as str]
            [clojure.edn :as edn]))

;; account-id = blake2b(b'ACCOUNT.bit', person="ckb-default-hash", digest-size=32)

(defn records-info-by-account
  [queries]
  (fn [_ args _]
    (let [{:keys [account]} args
          records (query queries :records-by-account {:account account})]
      records)))

(defn account-info-by-account
  [queries]
  (fn [_ args _]
    (let [{:keys [account]} args
          account-info (query queries :account-info-by-account {:account account})]
      account-info)))

(defn account-records
  [queries]
  (fn [_ _ account-info]
    (let [account (:account account-info)]
      (query queries :records-by-account {:account account}))))

(defn account-trade-deals
  [queries]
  (fn [_ _ account-info]
    (let [account (:account account-info)]
      (query queries :trade-deal-info-by-account {:account account}))))

(defn account-inviters
  [queries]
  (fn [_ _ account-info]
    (let [account (:account account-info)]
      (query queries :inviter-info-by-invitee-account {:invitee_account account}))))

(defn inviter-info-by-account
  [queries]
  (fn [_ _ {:keys [invitee_account] :as params}]
    (query queries :inviter-info-by-invitee-account {:invitee_account invitee_account})))

(defn trade-deal-info-by-account
  [queries]
    (fn [_ args _]
    (let [{:keys [account]} args
          deal-info (query queries :trade-deal-info-by-account {:account account})]
      deal-info)))

(defn resolver-map
  [component]
  (let [queries (:queries component)]
    {:query/records-info-by-account (records-info-by-account queries)
     :query/account-info-by-account (account-info-by-account queries)
     :query/inviter-info-by-account (inviter-info-by-account queries)
     :query/trade-deal-info-by-account (trade-deal-info-by-account queries)
     :Account/trade-deals (account-trade-deals queries)
     :Account/records (account-records queries)
     :Account/inviters (account-inviters queries)
     }))

(defn load-schema-file
  "Load schema file from resource."
  []
  (-> (io/resource "bit-schema.edn")
      slurp
      edn/read-string))

(defn load-schema
  "Load schema from resource."
  [component]
  (-> (load-schema-file)
      (util/attach-resolvers (resolver-map component))
      schema/compile))

(defrecord SchemaProvider [schema datasource queries]
  component/Lifecycle
  (start [this]
    (assoc this :schema (load-schema this)))
  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (component/using (map->SchemaProvider {})
                                     [:datasource :queries])})
