(ns tardis.integration-tests.service-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [io.pedestal.test :refer [response-for]]
            [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [tardis.system :refer [system]]
            [test-utils :as utils]
            [cheshire.core :as json]
            [clojure.java.io :as io]))

(declare sut)
(integrant.repl/set-prep! (constantly (system :test)))

(defn test-fixture [f]
  (reset-all)
  (go)
  (f))

(def data (-> "resources/data.json" io/resource io/file slurp))

(def data-as-map (-> "resources/data.json" io/resource io/file slurp json/decode))

(use-fixtures :once test-fixture)

(deftest save-notes-test
  (let [service (utils/service-fn sut)]
    (testing "should save and retrieve notes to db"
      (let [create-response ((comp #(json/decode % true) :body) (response-for service
                                                                    :post "/notes"
                                                                    :headers {"Content-Type" "application/json"}
                                                                    :body (json/encode {:template :aylo})))
            get-response ((comp #(json/decode % true) :body) (response-for service
                                                                 :get (str "/notes/" (:crux.db/id create-response))
                                                                 :headers {"Accept" "application/json"}
                                                                 ))]
        (is ((comp not nil?) create-response))
        (is (= (json/decode data true) (dissoc get-response :id )))))))

(deftest update-notes-test
  (let [service (utils/service-fn sut)]
    (testing "should update an existing note"
      (let [create-response (response-for service
                                          :post "/notes"
                                          :headers {"Content-Type" "application/json"}
                                          :body data)
            _ (response-for service
                            :put (str "/notes/" (:body create-response))
                            :headers {"Content-Type" "application/json"}
                            :body (json/encode (assoc data-as-map :additional-field "additional-field")))
            _ (prn (assoc data-as-map :additional-field "additional-field"))
            get-response (response-for service
                                       :get (str "/notes/" (:body create-response))
                                       :headers {"Accept" "application/json"})
            ]
        (is ((comp not nil?) (:body create-response)))
        (is ((comp not nil?) (:additional-field ((comp #(dissoc % :crux.db/id) json/decode) (:body get-response) true))))))))
