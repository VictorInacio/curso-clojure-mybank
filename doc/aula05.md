# Data schema definition

## Add dependency [prismatic/schema "0.4.3"]

Main schema.core functions:

```clojure

(def FooBar {:foo s/Keyword :bar [Number]}) ;; a schema

(s/check FooBar {:foo :k :bar [1.0 2.0 3.0]})
;=> {:foo (not (keyword? 0))}

(s/check FooBar {:foo :k :bar ["a" 1.0 2.0 3.0]})
;=> {:bar [(not (instance? java.lang.Number "a")) nil nil nil]}

(s/check FooBar {:foo 0 :bar [1.0 2.0 3.0]})
;=> nil

(s/validate FooBar {:foo :k :bar [1.0 2.0 3.0]})
;=> {:foo :k, :bar [1.0 2.0 3.0]}


(s/validate FooBar {:foo :k :bar ["a" 1.0 2.0 3.0]})
;=> {:foo :k, :bar [1.0 2.0 3.0]}


(def FooBarJava {:foo Double :bar Long}) ;; a java based type schema
(s/check FooBarJava {:foo 1.0 :bar 1})
(s/check FooBarJava {:foo 1 :bar 1.0})

(def Literals #{[{}]})

(s/check Literals #{[{}]})
```
## defrecord schemeless

```clojure
(defrecord Recipe
  [name ;; string
   description ;; string
   ingredients ;; sequence of Ingredient steps ;; sequence of string servings ;; number of servings
   ])


(defrecord Ingredient [name ;; string 
                       quantity ;; amount
                       unit ;; keyword
                       ])
```

```clojure
(def spaghetti-tacos (map->Recipe
                       {:name        "Spaghetti tacos"
                        :description "It's spaghetti... in a taco."
                        :ingredients [(->Ingredient "Spaghetti" 1 :lb)
                                      (->Ingredient "Spaghetti sauce" 16 :oz)
                                      (->Ingredient "Taco shell" 12 :shell)]
                        :steps       ["Cook spaghetti according to box."
                                      "Heat spaghetti sauce until warm."
                                      "Mix spaghetti and sauce."
                                      "Put spaghetti in taco shells and serve."]
                        :servings    4}))

```

```clojure
(s/defschema Data
             "A schema for a nested data type"
             {:a {:b s/Str
                  :c s/Int}
              :d [{:e s/Keyword
                   :f [s/Num]}]})
```

```clojure
(:require [schema.core :as s])

(s/defrecord FooBar
             [foo :- Int
              bar :- String])

(s/defrecord Ingredient
             [name :- s/Str
              quantity :- s/Int
              unit :- s/Keyword])
(s/defrecord Recipe
             [name :- s/Str
              description :- s/Str
              ingredients :- [Ingredient]
              steps :- [s/Str]
              servings :- s/Int])
```


