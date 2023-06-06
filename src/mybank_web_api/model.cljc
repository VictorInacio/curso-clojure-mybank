(ns mybank-web-api.model
  (:require [schema.core :as s
             :include-macros true]))

(s/defschema Data
             "A schema for a nested data type"
             {:a {:b s/Str
                  :c s/Int}
              :d [{:e s/Keyword
                   :f [s/Num]}]})


(defn str->int [s]
  #?(:clj  (java.lang.Integer/parseInt s)
     :cljs (js/parseInt s)))

;(print (eval (read-string "(+ 1 1)")))

;(slurp (java.io/ "f.edn"))
