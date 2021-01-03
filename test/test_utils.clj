(ns test-utils
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]))

(defn service-fn
  [system]
  (get-in integrant.repl.state/system [:tardis/pedestal :service :io.pedestal.http/service-fn]))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [(integrant.repl/set-prep! ~binding-expr)
         ~bound-var (ig/init ~binding-expr)]
     (try
       ~@body
       (finally
         (ig/halt! ~bound-var)))))

