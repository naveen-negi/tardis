(defproject tardis "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [io.pedestal/pedestal.route "0.5.8"]

                 [juxt/crux-core "20.09-1.12.1-beta"]
                 [juxt/crux-rocksdb "20.09-1.12.1-beta"]

                 [lambdaisland/deep-diff2 "2.0.108"]
                 [integrant "0.8.0"]
                 [integrant/repl "0.3.2"]

                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :tardis-rocksdb {:crux/module 'crux.rocksdb/->kv-store
               :db-dir (io/file "/tmp/rocksdb")}
  :crux/tx-log {:kv-store :my-rocksdb}
  :crux/document-store {:kv-store :my-rocksdb}
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "tardis.server/run-dev"]}
                   :resource-paths ["src/test/resources"]
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.8"]]}
             :uberjar {:aot [tardis.server]}}
  :main ^{:skip-aot true} tardis.server)
