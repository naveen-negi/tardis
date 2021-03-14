(ns tardis.config
  (:require [integrant.core :as ig]
            [io.pedestal.http :as http]
            [tardis.routes :as routes]
            [crux.api :as crux]
            [selmer.parser :as parser]))

(defn test?
  [service-map]
  (= (:env service-map) :test))

(defmethod ig/init-key :default
  [_ x]
  x)

(defmethod ig/init-key :tardis/pedestal
  [_ {:keys [service-map] :as pedestal}]
  (parser/cache-off!)
  (cond-> service-map
          true http/create-server
          (not (test? service-map)) http/start
          true ((partial assoc pedestal :service))))

(defmethod ig/halt-key! :tardis/pedestal
  [_ {:keys [service] :as pedestal}]
  (http/stop service)
  (assoc pedestal :service nil))


(defmethod ig/init-key :tardis/crux [_ crux-config]
  (let [node (crux/start-node crux-config)
        db (crux/db node)]
    (-> crux-config
        (assoc :node node)
        (assoc :db db)) ))

(defmethod ig/init-key :tardis/routes [_ {:keys [db-connection]}]
  (routes/get-all-routes db-connection))

