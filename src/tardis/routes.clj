(ns tardis.routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [crux.api :as crux]
            [cheshire.core :as json]
            [clojure.set :as set]
            [tardis.template-generator :as gen]
            )
  (:import (java.util UUID)))


(defn new-note []
  {:nodes [{:id (UUID/randomUUID)
            :name        "Initial Node",
           :description "",
            ;:val 10
           :title       "New Notes",
           :content     "",
           :neighbors   [],
           :links       []
            :group "root-node"
            ;:color "lightBlue"
           :type :root}]
  :links []})

(defn create-note
  [db-connection _]
  (let [document-id ((comp keyword str) (UUID/randomUUID))
        document (new-note)
        document-with-uuid (assoc document :crux.db/id document-id)
        trx (crux/submit-tx (:node db-connection) [[:crux.tx/put document-with-uuid]])]
    (crux/await-tx (:node db-connection) trx)
    (ring-resp/response (set/rename-keys document-with-uuid {:crux.db/id :id}))))

(defn generate-template
  [request]
  (ring-resp/response (json/encode (gen/generate-template (get-in request [:path-params :id])) )))

(defn update-notes
  [db-connection {:keys [json-params path-params]}]
  (let [document-id (keyword (:id path-params))
        document (-> json-params (assoc :crux.db/id document-id) (dissoc :id))
        trx (crux/submit-tx (:node db-connection) [[:crux.tx/put document]])]
    (crux/await-tx (:node db-connection) trx)
    (ring-resp/response (name document-id))))

(defn get-notes [db-connection request]
  (let [note-id (keyword (get-in request [:path-params :id]))
        note (crux/entity (crux/db (:node db-connection)) note-id)]
    (ring-resp/response (json/encode (set/rename-keys note {:crux.db/id :id})))))

(defn db-notes [db-connection]
  (crux/q (crux/db (:node db-connection))
        '{:find [notes]}) )

(defn get-all-notes [db-connection _]
  (let [notes (db-notes db-connection)]
    (ring-resp/response (json/encode notes))))

(defn get-all-routes [db-connection]
  (route/expand-routes
    #{["/notes" :post [(body-params/body-params) http/json-body (partial create-note db-connection)] :route-name :save-notes]
      ["/notes" :get [(partial get-all-notes db-connection)] :route-name :get-all-notes]
      ["/notes/:id" :get [(partial get-notes db-connection)] :route-name :get-notes]
      ["/templates/:id" :get [generate-template] :route-name :get-template]
      ["/notes/:id" :put [(body-params/body-params) http/json-body (partial update-notes db-connection)] :route-name :update-notes]
      }))


