(ns tardis.template-generator
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [selmer.parser :as parser])
  (:import (java.util UUID)))

(def aylo-template (json/decode (-> "aylo.json" io/resource io/file slurp) true))
(def loa-template (parser/render (-> "loa.json" io/resource io/file slurp) {:id-1 (str (UUID/randomUUID) )
                                                                            :id-2 (str (UUID/randomUUID) )
                                                                            :id-3 (str (UUID/randomUUID) )
                                                                            :id-4 (str (UUID/randomUUID) )}))


(defn template [template-name]
  (case template-name
    "ladder-of-abstraction" loa-template
    "aylo" aylo-template
    :default {}))

(defn create-link [first-node second-node]
  (let [first-id (:id first-node)
        second-id (:id second-node)]
    {:source first-id
     :target second-id}))

(defn build-links [nodes links]
  (if (> (count nodes) 1)
    (let [updated-links (conj links  (create-link (first nodes) (second nodes)) ) ]
      (build-links (rest nodes) updated-links))
    links))

(defn generate-graph [template-name]
  (let [template (template template-name)
        new-nodes  (map #(assoc % :id (UUID/randomUUID)) (:nodes template))
        new-links (build-links new-nodes [])]
    {:nodes new-nodes :links new-links} ))
