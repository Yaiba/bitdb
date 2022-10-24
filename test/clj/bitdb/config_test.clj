(ns bitdb.config-test
  (:require [clojure.test :refer :all]
            [bitdb.config :as config]
            [clojure.spec.alpha :as s]))

(s/def ::test-a int?)
(s/def ::test-b int?)
(s/def ::test-c int?)
(s/def ::test-d int?)
(s/def ::test-spec
  (s/keys :req [::test-a]
          :req-un [::test-b]
          :opt [::test-c]
          :opt-un [::test-d]))

(deftest test-spec->keys
  (testing "expected case"
    (let [keys (config/spec->keys ::test-spec)
          sorted-keys (sort keys)]
      (is (= [:test-b :test-d :bitdb.config-test/test-a :bitdb.config-test/test-c]
             sorted-keys)))))

(def test-env
  {:bb-db-user "user"
   :bb-run-mode "dev"
   :bb-jetty-join "false"
   :bb-jetty-port "3000"
   :bb-jetty-host "localhost"})

(deftest config-parsing
  (testing "load sub config type int"
    (is (= {:bb-jetty-port 3000}
           (config/load-sub-config test-env :bb-jetty-port))))
  (testing "load sub config type keyword"
    (is (= {:bb-run-mode :dev}
           (config/load-sub-config test-env :bb-run-mode))))
  (testing "load sub config type bool"
    (is (= {:bb-jetty-join false}
           (config/load-sub-config test-env :bb-jetty-join))))
  (testing "load sub config type ip localhost"
    (is (= {:bb-jetty-host "localhost"}
           (config/load-sub-config test-env :bb-jetty-host))))
  (testing "load sub config type ip addr"
    (let [test-env (assoc test-env :bb-jetty-host "2.2.2.2")]
      (is (= {:bb-jetty-host "2.2.2.2"}
             (config/load-sub-config test-env :bb-jetty-host)))))
  (testing "load sub config map with missing field"
    (let [test-env (dissoc test-env :bb-jetty-port)]
      (is (= {:bb-server nil}
             (config/load-sub-config test-env :bb-server)))))
  (testing "load sub config map"
    (is (= {:bb-server {:bb-jetty-port 3000
                        :bb-jetty-host "localhost"
                        :bb-jetty-join false}}
           (config/load-sub-config test-env :bb-server)))))
