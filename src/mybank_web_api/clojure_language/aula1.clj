(ns mybank-web-api.clojure-language.aula1
  (:require [clojure.core :as core]
            [mybank-web-api.utils :as utils]))

;; NAMESPACES
(comment
  (clojure.core/refer-clojure)

  ;; Namespace atual
  *ns*

  ;; Novo namespace
  '(ns myapp.foo.bar)

  (in-ns 'mybank-web-api.utils)
  ;(defn say-hello [x] (println "Hello, " x "!"))

  (ns-map (find-ns 'user))

  ;; Trocar escopo namespace
  (in-ns 'mybank-web-api.repl)

  (require '[mybank-web-api.core])
  (require '[mybank-web-api.core])
  (require '[mybank-web-api.utils :as util])
  (util/fn-util)

  (->
    (ns-map (find-ns 'mybank-web-api.utils))
    ;keys
    )

  (->
    (ns-publics (find-ns 'mybank-web-api.utils))
    ;keys
    )

  (+)
  (defn +
    "Returns the sum of nums. (+) returns 0. Does not auto-promote
    longs, will throw on overflow. See also: +'"
    #_{:inline         (nary-inline 'add 'unchecked_add)
       :inline-arities >1?
       :added          "1.2"}
    ([] 99999)
    ([x] (cast Number x))
    ([x y] (. clojure.lang.Numbers (add x y)))
    #_([x y & more]
       (reduce1 + (+ x y) more)))

  (def + clojure.core/+)
  (+)

  )

(comment
  ;; JAVA INTEROP
  (type "By Bluebeard's bananas!")
  (.toUpperCase "By Bluebeard's bananas!")
  ; => "BY BLUEBEARD'S BANANAS!"

  (.indexOf "Let's synergize our bleeding edges"
            "y")
  ; => 7


  (macroexpand-1 '(.toUpperCase "By Bluebeard's bananas!"))
  ; => (. "By Bluebeard's bananas!" toUpperCase)

  (macroexpand-1 '(.indexOf "Let's synergize our bleeding edges" "y"))
  ; => (. "Let's synergize our bleeding edges" indexOf "y")

  (macroexpand-1 '(Math/abs -3))
  ; => (. Math abs -3)

  (new String)
  ; => ""

  (String.)
  ; => ""

  (String. "To Davey Jones's Locker with ye hardies")
  ; => "To Davey Jones's Locker with ye hardies"

  (java.util.Stack.)
  ; => []


  ;Stack fila1 = new java.util.Stack ()
  ;fila1.push ("a")
  (let [stack (java.util.Stack.)]
    (.push stack "Latest episode of Game of Thrones, ho!")
    (.push stack 1)
    (.push stack :a)
    (.pop stack)
    stack)
  ; => ["Latest episode of Game of Thrones, ho!"]

  (doto (java.util.Stack.)
    (.push "Latest episode of Game of Thrones, ho!")
    (.push "Whoops, I meant 'Land, ho!'"))
  ; => ["Latest episode of Game of Thrones, ho!" "Whoops, I meant 'Land, ho!'"]

  (macroexpand-1
    '(doto (java.util.Stack.)
       (.push "Latest episode of Game of Thrones, ho!")
       (.push "Whoops, I meant 'Land, ho!'")))
  ; => (clojure.core/let
  ;[G__2876 (java.util.Stack.)]
  ;(.push G__2876 "Latest episode of Game of Thrones, ho!")
  ;(.push G__2876 "Whoops, I meant 'Land, ho!'")
  ;G__2876)

  )

(comment
  ;; Records and Polimorphism
  (defmulti full-moon-behavior
            (fn [were-creature] (:were-type were-creature)))



  (defmethod full-moon-behavior :wolf
    [were-creature]
    (str (:name were-creature) " will howl and murder"))

  (defmethod full-moon-behavior :simmons
    [were-creature]
    (str (:name were-creature) " will encourage people and sweat to the oldies"))


  (full-moon-behavior {:were-type :wolf
                       :name      "Rachel from next door"})


  (full-moon-behavior {:were-type :simmons
                       :name      "Person name"})

  (full-moon-behavior {:were-type :people
                       :name      "Person name"})

  (defmethod full-moon-behavior {:wolf 1
                                 :day :yes
                                 :full 1}
    [were-creature]
    (str (:name were-creature) " will howl and murder"))

  (full-moon-behavior {:were-type :wolf
                       :name      "Rachel from next door"})

  (full-moon-behavior {:name "VIctor"})
  ; => "Rachel from next door will howl and murder"

  (full-moon-behavior {:name      "Andy the baker"
                       :were-type :simmons})
  ; => "Andy the baker will encourage people and sweat to the oldies"





  (defrecord WereWolf [name title])

  (WereWolf. "David" "London Tourist")
  ; => #were_records.WereWolf{:name "David", :title "London Tourist"}

  (->WereWolf "Jacob" "Lead Shirt Discarder")
  ; => #were_records.WereWolf{:name "Jacob", :title "Lead Shirt Discarder"}

  (map->WereWolf {:name "Lucian" :title "CEO of Melodrama" :age 100})
  ; => #were_records.WereWolf{:name "Lucian", :title "CEO of Melodrama"}

  (def jacob (->WereWolf "Jacob" "Lead Shirt Discarder"))
  (:name jacob )
  (.name jacob)
  (get jacob :name)
  ; => "Jacob"

  (:name jacob)
  ; => "Jacob"

  (get jacob :name)
  ; => "Jacob"
  jacob
  (assoc jacob :age 11)
  ; => #were_records.WereWolf{:name "Jacob", :title "Lead Third Wheel"}

  (defprotocol WereCreature
    (full-moon-behavior [x])
    (create [x])
    (destroy [x])
    )

  (extend-type WereCreature

    (full-moon-behavior [x] "Maybe the Internet is just a vector for toxoplasmosis")
    )

  (defrecord WereWolf [name title]
    WereCreature
    (full-moon-behavior [x]
      (str name " will howl and murder")))

  (defmulti types (fn [x y] [(class x) (class y)]))
  (defmethod types [java.lang.String java.lang.String]
    [x y]
    "Two strings!")

  (types "String 1" "String 2")
  ; => "Two strings!"

  ;; KEYWORDS
  ;https://github.com/clojure/tools.reader/blob/v1.3.6/src/main/clojure/clojure/tools/reader.clj#L344

  ;https://blog.jeaye.com/2017/10/31/clojure-keywords/

  (name :a)
  (namespace :name/a)
  (keyword? :name/a)
  (keyword "a")
  (keyword "a")

  ;; Plain old keywords
  :foo

  :utils/asdasd

  ;;=> :foo

  :mybank-repl/foo
  ;;=> :a/foo

  :a
  (def m {:a 1})

  (:a m)

  ;Namespaced keywords

  :users.profile/name

  ;Grouped keywords

  (def order-schema
    [{:db/ident       :order/items
      :db/valueType   :db.type/ref
      :db/cardinality :db.cardinality/many
      :db/isComponent true}
     {:db/ident       :item/id
      :db/valueType   :db.type/ref
      :db/cardinality :db.cardinality/one}
     {:db/ident       :item/count
      :db/valueType   :db.type/long
      :db/cardinality :db.cardinality/one}])

  ;Dotted keywords
  (-> {:select [:a :b :c]
       :from   [:foo]
       :where  [:= :f.a "baz"]}
      sql/format)
  )
