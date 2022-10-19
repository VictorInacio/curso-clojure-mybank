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


```clojure
;; maybe (nilable)
(s/validate (s/maybe s/Keyword) :a)
(s/validate (s/maybe s/Keyword) nil)

;; eq and enum
(s/validate (s/eq :a) :a)
(s/validate (s/enum :a :b :c) :a)

;; pred
(s/validate (s/pred odd?) 1)

```

Adicionar nas funções bank:

```clojure

(s/defschema Contas {s/Keyword {:saldo Number}})

(s/explain Contas)

(s/check Contas {:1 {:saldo 1}})
(s/validate Contas {:1 {:saldo 1}})

(s/check Contas {1 {:saldo "a"}})
(s/validate Contas {1 {:saldo "a"}})

(s/defn ^:always-validate get-saldo [id-conta :- s/Keyword contas :- Contas]
  (get contas id-conta "conta inválida!"))

(get-saldo 1 {})

(s/with-fn-validation
  (get-saldo 1 {}))

(s/set-fn-validation! true)
(s/set-fn-validation! false)

(defn get-saldo-interceptor [context]
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (-> context :contas)
        saldo (get-saldo id-conta @contas)]
    (assoc context :response {:status  200
                              :headers {"Content-Type" "text/plain"}
                              :body    saldo})))
```

```clojure
(s/defschema Context {s/Any s/Any})

(s/defschema Response {s/Any s/Any
                       :response {:body s/Any
                                  :status s/Int
                                  s/Any s/Any}})
(defn get-saldo [context]
  (s/validate Context context)
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (-> context :contas)
        ret (assoc context :response {:status  200
                                      :headers {"Content-Type" "text/plain"}
                                      :body    (id-conta @contas "conta inválida!")})]
    (s/validate Response ret)))

(get-saldo {:request {:path-params {:id "1"}}
            :contas (atom {})})


(get-saldo {:request {:path-params {:id "1"}}
            :contas (atom {})})
```

```clojure

(get-saldo "a" {})

(def dbg (atom false))
(reset! dbg true)

(s/with-fn-validation
  (get-saldo :2 {:1 {:saldo 1}}))

(if @dbg
  (s/with-fn-validation
    (get-saldo :1 {}))
  (get-saldo :1 {}))

(get-saldo :2 {:1 {:saldo 1}})
(get-saldo 2 {:1 {:saldo 1}})

(s/set-fn-validation! false)
```
