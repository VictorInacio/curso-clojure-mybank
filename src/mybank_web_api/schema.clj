(ns mybank-web-api.schema
  (:require [schema.core :as s :include-macros true])
  (:import (clojure.lang BigInt)))

(comment
  (def FooBar {:foo s/Keyword :bar [Number]})               ;; a schema
  ;(def FooBar {:foo s/Keyword :bar [s/Any]}) ;; a schema

  (type FooBar)

  (s/check FooBar {:foo :k :bar [1.0 2.0 3.0]})
  (s/check FooBar {:foo :k :bar [1.0 2.0 3.0]})
  ;=> {:foo (not (keyword? 0))}

  (s/check FooBar {:foo :k :bar [1.0 2.0 "a" 3.0 4 5 "a"]})
  (s/check FooBar {:foo :k :bar [1.0 2.0 "a" 3.0 4 5 "a"]})


  (s/check FooBar {:foo 0 :bar [1.0 2.0 3.0]})
  (s/check FooBar {:foo :k :bar [1.0 2.0 3.0] :buzz 0})

  (def FooBarAny {:foo      s/Keyword
                  :bar      [s/Any]
                  s/Keyword s/Any
                  })                                        ;; a schema

  (s/check FooBarAny {:foo   :a
                      :bar   [1.0 2.0 3.0]
                      "buzz" 0})
  (s/validate FooBar {:foo :k :bar [1.0 2.0 3.0]})
  (s/validate FooBar {:foo :k :bar [1.0 2.0 "a" 3.0 4 5 "a"]})

  (def FooBarAny {:foo      s/Keyword
                  :bar      [s/Any]
                  s/Keyword s/Any
                  })                                        ;; a schema

  (s/validate FooBarAny {:foo  :a
                         :bar  [1.0 2.0 3.0]
                         :buzz 0})

  (def FooBarOpt {(s/optional-key :foo) s/Keyword
                  :bar                  [s/Any]
                  s/Keyword             s/Any
                  })

  (s/validate FooBarOpt {;:foo  :a
                         :bar  [1.0 2.0 3.0]
                         :buzz 0})


  (def FooBarJava {:foo Double :bar Long})                  ;; a java based type schema
  (s/check FooBarJava {:foo 1.0 :bar 1})
  (s/validate FooBarJava {:foo 1.0 :bar 1})
  (s/check FooBarJava {:foo 1 :bar 1.0})


  (def FooBarJavaBig {:foo BigDecimal :bar BigInt})
  (s/check FooBarJavaBig {:foo 1.0M :bar 1N})
  (s/validate FooBarJavaBig {:foo 1.0M :bar 1N})


  (def Literals #{[{s/Any s/Any}]})
  (def Literals [#{} {}])
  (def Literals #{[{Double Double}]})

  (s/check Literals #{[{:a  :b
                        1   0
                        "a" "c"}]})

  (def FooBarMapVec {(s/optional-key :foo) s/Keyword
                     :bar                  [s/Any]
                     :map-vec              [{s/Str Number}]
                     })

  (s/check FooBarMapVec {:foo     :a
                         :bar     [1.0 2.0 3.0]
                         :map-vec [{"str" 0}]})

  (s/defschema FooBarMapVec {(s/optional-key :foo) s/Keyword
                             :bar                  [s/Any]
                             :map-vec              [{s/Str Number}]
                             })

  (s/defschema MapFooBar {s/Keyword FooBarMapVec})

  (s/checker MapFooBar)
  (s/explain FooBarMapVec)
  (s/explain MapFooBar)


  ;; eq and enum
  (s/validate (s/eq :a) :a)
  (s/validate (s/enum :a :b :c 1 2) 2)

  ;; pred
  (s/validate (s/pred odd?) 1)

  (s/defschema impar (s/pred odd?))

  (defn small? [x] (> 3 x))

  (s/validate (s/pred small?) 3))