(ns tardis.template-generator
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [selmer.parser :as parser])
  (:import (java.util UUID)))

(defn populated-template [json-data]
  (parser/render json-data {:id-0 (str (UUID/randomUUID))
                            :id-1 (str (UUID/randomUUID))
                            :id-2 (str (UUID/randomUUID))
                            :id-3 (str (UUID/randomUUID))
                            :id-4 (str (UUID/randomUUID))
                            :id-5 (str (UUID/randomUUID)) }))

(def colors {"loa" "teal" "fpt" "lightBlue" "te" "green" "aylo" "yellow" "sot" "violet" "ip" "purple"})

(defn with-group [template group]
  (let [nodes-with-group (map #(merge % {:group group}) (:nodes template) ) ]
    (assoc template :nodes nodes-with-group)))

(defn generate-template [template-name]
  (case template-name
    "ladder-of-abstraction" (with-group (json/decode (populated-template (-> "loa.json" io/resource io/file slurp)) true) "loa")
    "first-principle-thinking" (with-group (json/decode (populated-template (-> "first_principle_thinking.json" io/resource io/file slurp)) true) "fpt")
    "thought-experiment" (with-group (json/decode (populated-template (-> "thought_experiment.json" io/resource io/file slurp)) true) "te")
    "aylo" (with-group (json/decode (populated-template (-> "aylo.json" io/resource io/file slurp)) true) "aylo")
    "second-order-thinking" (with-group (json/decode (populated-template (-> "second_order_thinking.json" io/resource io/file slurp)) true) "sot")
    "inversion" (with-group (json/decode (populated-template (-> "inversion.json" io/resource io/file slurp)) true) "ip")
    :default {}))

