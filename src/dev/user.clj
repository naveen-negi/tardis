(ns user
  (:require [crux.api :as crux]
            [integrant.repl :refer [clear go halt prep init reset reset-all]])
  (:import (java.util UUID)))

(comment
  (def node (crux/start-node {}))

  (def manifest
    {:crux.db/id  :manifest
     :pilot-name  "Johanna"
     :id/rocket   "SB002-sol"
     :id/employee "22910x2"
     :badges      "SETUP"
     :cargo       ["stereo" "gold fish" "slippers" "secret note"]})

  (defn easy-ingest [node docs]
    (crux/submit-tx node (mapv (fn [doc] {:crux.tx/put doc}) docs)))

  (comment

    (crux/entity (crux/db node) ::tombaugh-resources)

    (crux/q (crux/db node) '{:find  [e]
                             :where [[e :pilot-name "Johanna"]]})

    (crux/submit-tx node
                    [[:crux.tx/put {:crux.db/id   :gold-harmony
                                    :company-name "Gold Harmony"
                                    :seller?      true
                                    :buyer?       false
                                    :units/Au     10211
                                    :credits      51}]

                     [:crux.tx/put {:crux.db/id   :tombaugh-resources
                                    :company-name "Tombaugh Resources Ltd."
                                    :seller?      true
                                    :buyer?       false
                                    :units/Pu     50
                                    :units/N      3
                                    :units/CH4    92
                                    :credits      51}]

                     [:crux.tx/put {:crux.db/id   :encompass-trade
                                    :company-name "Encompass Trade"
                                    :seller?      true
                                    :buyer?       true
                                    :units/Au     10
                                    :units/Pu     5
                                    :units/CH4    211
                                    :credits      1002}]

                     [:crux.tx/put {:crux.db/id   :blue-energy
                                    :seller?      false
                                    :buyer?       true
                                    :company-name "Blue Energy"
                                    :credits      1000}]
                     ])

    (crux/submit-tx
      node
      [[:crux.tx/match
        :blue-energy
        {:crux.db/id   :blue-energy
         :seller?      false
         :buyer?       true
         :company-name "Blue Energy"
         :credits      1000}]
       [:crux.tx/put
        {:crux.db/id   :blue-energy
         :seller?      false
         :buyer?       true
         :company-name "Blue Energy"
         :credits      900
         :units/CH4    10}]

       [:crux.tx/match
        :tombaugh-resources
        {:crux.db/id   :tombaugh-resources
         :company-name "Tombaugh Resources Ltd."
         :seller?      true
         :buyer?       false
         :units/Pu     50
         :units/N      3
         :units/CH4    92
         :credits      51}]
       [:crux.tx/put
        {:crux.db/id   :tombaugh-resources
         :company-name "Tombaugh Resources Ltd."
         :seller?      true
         :buyer?       false
         :units/Pu     50
         :units/N      3
         :units/CH4    82
         :credits      151}]])
    )





  (comment
    (crux/submit-tx
      node
      [
       [:crux.tx/put
        {:crux.db/id  :consumer/RJ29sUU
         :consumer-id :RJ29sUU
         :first-name  "Jay"
         :last-name   "Rose"
         :cover?      true
         :cover-type  :with-no-end-date}
        #inst "2115-12-03"]

       [:crux.tx/put
        {:crux.db/id  :consumer/RJ29sUU
         :consumer-id :RJ29sUU
         :first-name  "Jay"
         :last-name   "Rose"
         :cover?      true
         :cover-type  :Full}
        #inst "2113-12-03"                                  ;; Valid time start
        #inst "2114-12-03"]                                 ;; Valid time end

       [:crux.tx/put
        {:crux.db/id  :consumer/RJ29sUU
         :consumer-id :RJ29sUU
         :first-name  "Jay"
         :last-name   "Rose"
         :cover?      true
         :cover-type  :Full}
        #inst "2112-12-03"
        #inst "2113-12-03"]

       [:crux.tx/put
        {:crux.db/id  :consumer/RJ29sUU
         :consumer-id :RJ29sUU
         :first-name  "Jay"
         :last-name   "Rose"
         :cover?      false}
        #inst "2112-06-03"
        #inst "2112-12-02"]

       [:crux.tx/put
        {:crux.db/id  :consumer/RJ29sUU
         :consumer-id :RJ29sUU
         :first-name  "Jay"
         :last-name   "Rose"
         :cover?      true
         :cover-type  :Promotional}
        #inst "2111-06-03"
        #inst "2112-06-03"]])
    ;query data

    (crux/q (crux/db node)
            '{:find  [name]
              :where [[e :cover? true]
                      [e :cover-type name]]}))


  (comment

    (crux/q (crux/db node)
            '{:find  [element]
              :where [[element :type :element/metal]]})

    (crux/q (crux/db node)
            '{:find  [name]
              :where [[element :type :element/metal]
                      [element :common-name name]
                      ]})

    (crux/q (crux/db node)
            '{:find  [name rho element]
              :where [[element :density rho]
                      [element :common-name name]
                      ]})

    (crux/q (crux/db node)
            {:find  '[name]
             :where '[[element :type t]
                      [element :common-name name]]
             :args  [{'t :element/metal}]})




    (crux/submit-tx node [[:crux.tx/put (assoc manifest :badges ["SETUP" "PUT"])]])
    (crux/entity (crux/db node) :manifest)

    (def doc-id ((comp keyword str) (UUID/randomUUID)))

    (crux/submit-tx node [[:crux.tx/put {:crux.db/id doc-id :content ""}]])

    (crux/entity (crux/db node) doc-id)

    (crux/submit-tx node [[:crux.tx/put manifest]])

    (crux/entity-history (crux/db node) :manifest :asc)

    (crux/submit-tx node [
                          [:crux.tx/put
                           {:crux.db/id  :commodity/Pu
                            :common-name "Plutonium"
                            :type        :element/metal
                            :density     19.851
                            :radioactive true}]
                          [:crux.tx/put
                           {:crux.db/id  :commodity/N
                            :common-name "Nitrogen"
                            :type        :element/gas
                            :density     1.2506
                            :radioactive false}]

                          [:crux.tx/put
                           {:crux.db/id  :commodity/CH4
                            :common-name "Methane"
                            :type        :molecule/gas
                            :density     0.717
                            :radioactive false}]

                          ])

    (crux/submit-tx node [
                          [:crux.tx/put {:crux.db/id :stock/Pu
                                         :commod     :commodity/Pu
                                         :weight-ton 21}
                           #inst "2115-02-13T18"]

                          [:crux.tx/put
                           {:crux.db/id :stock/Pu
                            :commod     :commodity/Pu
                            :weight-ton 23}
                           #inst "2115-02-14T18"]

                          [:crux.tx/put
                           {:crux.db/id :stock/Pu
                            :commod     :commodity/Pu
                            :weight-ton 22.2}
                           #inst "2115-02-15T18"]

                          [:crux.tx/put
                           {:crux.db/id :stock/Pu
                            :commod     :commodity/Pu
                            :weight-ton 24}
                           #inst "2115-02-18T18"]

                          [:crux.tx/put
                           {:crux.db/id :stock/Pu
                            :commod     :commodity/Pu
                            :weight-ton 24.9}
                           #inst "2115-02-19T18"]])


    (crux/submit-tx node
                    [[:crux.tx/put
                      {:crux.db/id :stock/N
                       :commod     :commodity/N
                       :weight-ton 3}
                      #inst "2115-02-13T18"                 ;; start valid-time
                      #inst "2115-02-19T18"]                ;; end valid-time

                     [:crux.tx/put
                      {:crux.db/id :stock/CH4
                       :commod     :commodity/CH4
                       :weight-ton 92}
                      #inst "2115-02-15T18"
                      #inst "2115-02-19T18"]])

    (crux/entity (crux/db node #inst "2115-03-14") :stock/Pu)))

