(ns tardis.system
  (:require [integrant.core :as ig]
            [integrant.repl :refer [go halt init prep reset]]
            [io.pedestal.http :as http]
            [tardis.config :as config]
            [tardis.routes :refer [get-all-routes]]
            [crux.api :as crux]
            [clojure.java.io :as io]))

(defn system
  [env]
  {:tardis/service-map {:env env
                        ::http/routes (ig/ref :tardis/routes)
                        ::http/type :jetty
                        ::http/allowed-origins {:creds true :allowed-origins (constantly true)}
                        ::http/port 8890
                        ::http/join? false}

   :tardis/routes {:db-connection (ig/ref :tardis/crux)}

   :tardis/pedestal {:service-map (ig/ref :tardis/service-map)}

   :tardis/crux (case env
                  :test
                  {}
                  :dev
                  {:crux/index-store {:kv-store {:crux/module 'crux.rocksdb/->kv-store
                                                  :db-dir (io/file "data/crux-db/index")}}
                    :rocksdb-golden {:crux/module 'crux.rocksdb/->kv-store
                                     :db-dir (io/file "data/crux-db/tx-log-doc-store")}
                    :crux/document-store {:kv-store :rocksdb-golden}
                    :crux/tx-log {:kv-store :rocksdb-golden}} )})

