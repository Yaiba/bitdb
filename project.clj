(defproject bitdb "BitDB"
  :description "Full stack Clojure exploring .bit database."
  :url "https://github.com/Yaiba/bitdb"
  :min-lein-version "2.0.0"
  :dependencies [; Clojure
                 [org.clojure/clojure "1.10.0"]

                 ; Http server
                 [compojure "1.6.1"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [ring/ring-defaults "0.3.4"]

                 ; DB
                 [com.github.seancorfield/next.jdbc "1.3.834"]
                 [com.github.seancorfield/honeysql "2.3.928"]
                 [mysql/mysql-connector-java "8.0.19"]

                 ; build tools
                 [environ "1.2.0"]

                 ; Utility
                 [com.stuartsierra/component "1.1.0"]
                 [expound "0.9.0"]
                 ]
  :source-paths ["src/clj" "test/clj"]
  :plugins [[lein-ring "0.12.5"]
            [com.github.clj-kondo/lein-clj-kondo "0.2.1"]
            [lein-environ "1.2.0"]]
  :ring {:handler bitdb.handler/app}
  :main ^:skip-aot bitdb.core
  :profiles {:dev [:project/dev :profiles/dev]
             :profiles/dev {}
             :project/dev {:main user
                           :source-paths ["dev"]
                           :dependencies [[javax.servlet/servlet-api "2.5"]
                                          [ring/ring-mock "0.3.2"]
                                          [org.clojure/tools.namespace "1.3.0"]
                                          [com.stuartsierra/component.repl "1.0.0"]]}})

