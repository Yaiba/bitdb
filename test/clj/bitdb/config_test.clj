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
  {:bb-db-port "4444"
   :bb-db-host "localhost"
   :bb-db-user "user"
   :bb-run-mode "dev"
   :bb-jetty-join "false"})

(deftest config-parsing
  (testing "load sub config type int"
    (is (= {:bb-db-port 4444}
           (config/load-sub-config test-env :bb-db-port))))
  (testing "load sub config type keyword"
    (is (= {:bb-run-mode :dev}
           (config/load-sub-config test-env :bb-run-mode))))
  (testing "load sub config type bool"
    (is (= {:bb-jetty-join false}
           (config/load-sub-config test-env :bb-jetty-join))))
  (testing "load sub config type ip localhost"
    (is (= {:bb-db-host "localhost"}
           (config/load-sub-config test-env :bb-db-host))))
  (testing "load sub config type ip addr"
    (let [test-env (assoc test-env :bb-db-host "2.2.2.2")]
      (is (= {:bb-db-host "2.2.2.2"}
             (config/load-sub-config test-env :bb-db-host)))))
  (testing "load sub config map with missing field"
    (is (= {:bb-db nil}
           (config/load-sub-config test-env :bb-db))))
  (testing "load sub config map"
    (let [test-env (assoc test-env :bb-db-pass "pass")]
      (is (= {:bb-db {:bb-db-port 4444
                      :bb-db-host "localhost"
                      :bb-db-user "user"
                      :bb-db-pass "pass"}}
             (config/load-sub-config test-env :bb-db))))))
