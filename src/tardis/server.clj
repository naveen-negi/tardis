(ns tardis.server
  (:gen-class) ; for -main method in uberjar
  (:require [tardis.system :refer [system]]
            [integrant.core :as ig]
            [io.pedestal.http :as http]))


(defn -main
  [& args]
  (http/start (get-in (ig/init (system :dev)) [:tardis/pedestal :service])))


