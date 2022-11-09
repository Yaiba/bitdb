(ns bitdb.system-test
  (:require [clojure.test :refer [deftest is]]
            [bitdb.system :as system]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia :as lacinia]
            [bitdb.test-utils :refer [simplify]]
            [environ.core :refer [env]]
            [bitdb.config :refer [load-config]]))

(defn ^:private test-system
  []
  (-> env
      (assoc :bb-jetty-port "3333")
      load-config
      (system/new-system)))

(defn ^:private q
  [system query variables]
  (-> system
      (get-in [:schema-provider :schema])
      (lacinia/execute query variables nil)
      simplify))

(deftest can-read-account-info
     (let [system (component/start-system (test-system))
           result (q system
                     "{ account_info_by_account(account: \"thefirst💯registeredbydevteamtoensuredassuccessfullylaunched20.bit\") {status manager}}"
                     nil)]
       (is (= {:data {:account_info_by_account {:manager "0x2b2b0d8eb7e6b7408608fd9fbf595096ff809ce6"
                                                :status 0}}}
              result))
       (component/stop-system system)))

(deftest can-read-account-info-with-related-records
     (let [system (component/start-system (test-system))
           result (q system
                     "{ account_info_by_account(account: \"thefirst💯registeredbydevteamtoensuredassuccessfullylaunched20.bit\") {status manager records_info {key value}}}"
                     nil)]
       (is (= {:data {:account_info_by_account {:manager "0x2b2b0d8eb7e6b7408608fd9fbf595096ff809ce6"
                                                :status 0
                                                :records_info [{:key "ghost"
                                                                :value "林冠宏"}]}}}
              result))
       (component/stop-system system)))
