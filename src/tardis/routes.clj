(ns tardis.routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [crux.api :as crux]
            [cheshire.core :as json]
            [clojure.set :as set]
            [clojure.java.io :as io])
  (:import (java.util UUID)))


(def aylo-template (json/decode (-> "aylo.json" io/resource io/file slurp) true ))
(def loa-template (json/decode (-> "loa.json" io/resource io/file slurp) true ))

(defn template [template-name]
  (case template-name
    "ladder-of-abstraction"  loa-template
    "aylo"  aylo-template
    :default  {}))

(defn create-note
  [db-connection {:keys [json-params]}]
  (let [document-id ((comp keyword str) (UUID/randomUUID) )
        document (template (:template json-params))
        document-with-uuid (assoc document :crux.db/id document-id)
        trx (crux/submit-tx (:node db-connection) [[:crux.tx/put document-with-uuid]])]
    (crux/await-tx (:node db-connection)  trx )
   (ring-resp/response document-with-uuid)))

(defn update-notes
  [db-connection {:keys [json-params path-params]}]
  (let [document-id (keyword (:id path-params))
        document (-> json-params (assoc  :crux.db/id document-id) (dissoc :id))
        trx (crux/submit-tx (:node db-connection) [[:crux.tx/put document]])]
    (crux/await-tx (:node db-connection)  trx )
    (ring-resp/response (name document-id))))

(defn get-notes [db-connection request]
  (let [note-id (keyword (get-in request [:path-params :id]) )
        note (crux/entity (crux/db (:node db-connection)) note-id)]
    (ring-resp/response (json/encode (set/rename-keys note {:crux.db/id :id})))))

(defn get-all-routes [db-connection]
  (route/expand-routes
   #{["/notes" :post [(body-params/body-params) http/json-body  (partial create-note db-connection)] :route-name :save-notes]
     ["/notes/:id" :get [(partial get-notes db-connection)] :route-name :get-notes]
     ["/notes/:id" :put [(body-params/body-params) http/json-body (partial update-notes db-connection)] :route-name :update-notes]
     }))


