(ns mybank-web-api.clojure-language.aula4
  (:require [schema.core :as s]))

(int? 0)
(int? "0")

(def numero? (s/pred int?))

(s/validate numero? 0)
(s/validate numero? "0")
(s/validate numero? [])
(s/validate numero? {"" []})


(s/check numero? 0)
(s/check numero? "0")
(s/check numero? [])


(def texto? (s/pred string?))

(s/validate texto? "oi mundo dos schemas")

(#(= % "victor") "victor")
(#(= % "victor") "not victor")

(def victor? (s/pred #(= % "victor")))
(s/def victor? (s/pred #(= % "victor")))

(s/check victor? "not")

(hash-map :a 'b
          :b "c"
          [0 0 0] "123x")
(hash-map :a 'b)

(def alunos-turma-968?
  (s/pred
    #(contains? #{"Atila"
                  "Beatriz"
                  "Bruna"
                  "Gabriele"
                  "Jade"
                  "Larissa"
                  "Lucas"
                  "Nicole"} %)
    'alunos-968))

(s/validate alunos-turma-968? "Gabriele")

(s/check victor? "victor")
(s/check victor? "not victor")

(s/def m-victor? {:nome victor?})


(s/validate m-victor? {:nome "victor"})
(s/validate m-victor? {:nome "not victor"})
(s/validate m-victor? {:nomes "victor"})
(s/check m-victor? {:nomes "victor"})
(s/check {:nome victor?} {:nomes "victor"})
(s/check {:nome victor?} {:nome "victor"})

(def bool? (s/pred bool?))
(s/check s/Bool true)
(s/check s/Bool false)
(s/check s/Bool "false")
(s/check s/Bool nil)

(s/check s/Num 0)
(s/check s/Num 0.1)
(s/check s/Num 0.1M)
(s/check s/Num 1000000000000000000000N)

(s/check s/Keyword :a)
(s/check s/Keyword :na/a)
(s/check s/Symbol 'bool?)
(s/check s/Symbol (quote bool?))
;; s/Any, s/Bool, s/Num, s/Keyword, s/Symbol, s/Int, and s/Str are cross-platform schemas.


(s/validate s/Str "oi mundo dos schemas")



(def texto? s/Str)




(s/check texto? "oi mundo dos schemas")





(s/validate texto? "oi mundo dos schemas")



(s/defschema Data
  "A schema for a nested data type"
  {:a {:b s/Str
       :c s/Int}
   :d [{:e s/Keyword
        :f [s/Num]}]})


(s/defschema Data
  "A schema for a nested data type"
  {:a {:b s/Str
       :c s/Int}
   :d [{:e s/Keyword
        :f [alunos-turma-968?]}]})


(s/check
  Data
  {:a {:b "abc"
       :c 123}
   :d [{:e :bc
        :f [12.2 13 100]}
       {:e :bc
        :f ["Lucas" "Nicole" "Victor"]}]})


(s/check
  Data
  {:a {:b "abc"
       :c 123}
   :d [{:e :bc
        :f [12.2 13 100 "100"]}
       {:e :bc
        :f ["-1"]}]})

(s/validate
  {:a {:b s/Str
       :c s/Int}
   :d [{:e s/Keyword
        :f [s/Num]}]}
  {:a {:b "abc"
       :c 123}
   :d [{:e :bc
        :f [12.2 13 100]}
       {:e :bc
        :f [-1]}]})


(s/validate
  Data
  {:a {:b ["abc"]
       :c 123}
   :d [{:e :bc
        :f [12.2 13 100]}
       {:e :bc
        :f [-1]}]})


(s/check
  Data
  {:a {:b ["abc"]
       :c 123}
   :d [{:e :bc
        :f [12.2 13 100]}
       {:e :bc
        :f [-1]}]})

(s/check
  Data
  {:a {:b 123
       :c "ABC"}})

(s/check
  Data
  {:a {:b 123
       :c "ABC"}})

(s/defschema EmailIdMappings {s/Str s/Int})


(s/validate EmailIdMappings {"abc@123.com" 123})
(s/check EmailIdMappings {"123" "abc@123.com"})

(s/defschema EmailIdMappingsGeneric {s/Any s/Int})

(s/check EmailIdMappingsGeneric {123 "abc@123.com"})
(s/check EmailIdMappingsGeneric {123 123})
(s/check EmailIdMappingsGeneric {"123" 123})
(s/check EmailIdMappingsGeneric {["123"] 123})


(s/validate s/Num 42)
;; 42
(s/validate s/Num "42")
(s/check s/Num "42")
;; RuntimeException: Value does not match schema: (not (instance java.lang.Number "42"))

(s/validate s/Keyword :whoa)
;; :whoa
(s/validate s/Keyword 123)
;; RuntimeException: Value does not match schema: (not (keyword? 123))

;; On the JVM, you can use classes for instance? checks
(s/validate java.lang.String "schema")
(s/validate clojure.lang.AFn (fn [] "schema"))


;; list of strings
(s/validate [s/Str] ["a" "b" "c"])
(s/validate [s/Str] ["a" "b" "c" :d])
(s/check [s/Str] ["a" "b" "c" :d])


(remove nil? (s/check [s/Str] ["a" "b" "c" :d]))

(->> ["a" "b" "c" :d]
     (s/check [s/Str])
     (remove nil?)
     first
     type
     )

(remove nil? (s/check [s/Str] ["a" "b" "c" :d]))





;; Domain Specific Language

;; nested map from long to String to double
(s/validate {long {String double}}
            {1 {"2" 3.0 "4" 5.0}})

(s/validate {long {String double}}
            {:1 {"2" 3.0 "4" 5.0}})

(s/check {long {String double}}
         {1 {:2 3.0 "4" 5.0}})


(def StringList [s/Str])
(def StringScores {String double})
(def StringScoreMap {long StringScores})


(s/defschema StringList [s/Str])
(s/defschema StringScores {String double})
(s/defschema StringScoreMap {long StringScores})

;------------------------------------------

(s/validate StringList ["a" :b:b "c"])
(s/validate StringList ["a" ":b:b" "c"])
(s/validate StringScores {"a" 1.1})

(s/check StringScoreMap {1 {"2" 3.0 "3" [5.0]}
                         4 {}})
(s/check StringScoreMap {1 {"2" 3.0 "3" 5.0}
                         4 {}})

(s/check StringList ["a" :b "c"])
(s/check StringList ["a" ":b" "c"])

(s/check StringScoreMap {1 {"2" 3.0 "3" [5.0]} 4.0 {}})
(s/check StringScoreMap {1 {"2" 3.0 "3" 5.0} 4 {}})


;;;;;;;

(s/defschema Recursive {:key      s/Int
                        :value    s/Str
                        :children [(s/recursive #'Recursive)]})

(s/defschema Recursive2 {:key      s/Int
                         :value    s/Str
                         :children (s/recursive #'Recursive)})

;;=> #'schema-example.clj-example/Recursive
(s/check Recursive1
         {:key      1
          :value    "test",
          :children [{:key 2 :value "test2", :children []}
                     {:key 3 :value "test3", :children [{:key      4
                                                         :value    "test4"
                                                         :children [{:key      1
                                                                     :value    ""
                                                                     :children [{:foo :bar}]}]}]}]})

(s/check Recursive2
         {:key      1
          :value    "test",
          :children {:key 2 :value "test2", :children [{:key 2 :value "test2", :children []}]}})


(defprotocol TimestampOffsetter
  (offset-timestamp [this offset]
    "adds integer offset to stamped object and returns the result"))


(System/currentTimeMillis)

(defrecord StampedNames
  [^Long date names]                                        ;; a list of Strings
  TimestampOffsetter
  (offset-timestamp [this offset] (+ date offset)))

(defn ^StampedNames stamped-names
  "names is a list of Strings"
  [names]
  (StampedNames. (str (System/currentTimeMillis)) names))

(stamped-names ["Victor" "Brenda" "Thais" "Yasmin" "Luis" "Juliana"])

(def ^StampedNames example-stamped-names
  (stamped-names (map (fn [first-name]                      ;; takes and returns a string
                        (str first-name " Smith"))
                      ["Victor" "Brenda" "Thais" "Yasmin" "Luis" "Juliana"])))

(defprotocol TimestampOffsetter
  (offset-timestamp [this offset]))

(defprotocol MudarNome
  (muda-nome [this sobrenome]))

(s/defprotocol
  MudarNome
  (muda-nome :- s/Str [this sobrenome :- s/Str]))

(s/defn foo :- s/Str [{:keys [x :- s/Int]}])

(s/defrecord Pessoa
  [nome :- s/Str]
  MudarNome
  (muda-nome [this sobrenome]
    (str (.nome this) " " sobrenome)))

(Pessoa. "Victor")

(def v (Pessoa. "Victor"))
(muda-nome v "Inacio")

(s/defrecord PessoaInvertida
  [nome :- s/Str]
  MudarNome
  (muda-nome [this sobrenome]
    (str sobrenome " " (.nome this)))
  )

(def v2 (PessoaInvertida. "Victor"))
(def v2 (PessoaInvertida. 2))
(muda-nome v2 "Inacio")

(s/defprotocol TimestampOffsetter
               (offset-timestamp :- s/Int [this offset :- s/Int]))

(s/defrecord StampedNames
  [date :- Long
   names :- [s/Str]]
  TimestampOffsetter
  (offset-timestamp [this offset] (+ date offset)))


(s/defn stamped-names :- StampedNames [names :- [s/Str]]
  (StampedNames. (System/currentTimeMillis) names))

(s/def example-stamped-names :- StampedNames
  (stamped-names (map (s/fn :- s/Str [first-name :- s/Str]
                        (str first-name " Smith"))
                      ["Victor" "Brenda" "Thais" "Yasmin" "Luis" "Juliana"])))


(s/explain StampedNames)

schema.core/Schema
(s/explain (s/fn-schema stamped-names))

(stamped-names ["bob" 1 "victor"])

(s/with-fn-validation
  (stamped-names ["bob" 1]))

(s/with-fn-validation
  (stamped-names [1]))

(s/set-fn-validation! false)

(stamped-names [1])

(s/defschema FooBar
  {:foo s/Str
   :bar s/Keyword})

(s/check FooBar {:foo "s"
                 :bar :a})

(s/check FooBar {:foo0 "s"
                 :bar  :a})

(s/defschema FooBar
  {(s/required-key :foo) {(s/required-key :foo2) s/Str}
   (s/optional-key :bar) s/Keyword})


(s/check FooBar {:foo {:foo2 "s"}
                 :bar :a})
(s/check FooBar {:foo {:foo2 "s"}})
(s/check FooBar {:bar :k})

(s/validate FooBar {:foo "f" :bar :b})
;; {:foo "f" :bar :b}

(s/validate FooBar {:foo :f})

(s/defschema FancyMap
  "If foo is present, it must map to a Keyword.  Any number of additional
   String-String mappings are allowed as well."
  {(s/optional-key :foo) s/Keyword
   (s/required-key :int) s/Int})

(s/validate FancyMap {:foo :a
                      :int 1})
(s/validate FancyMap {"a" 1
                      2   1})

(s/validate FancyMap {"a" "b"})
(s/validate FancyMap {"a" :b})

(s/validate FancyMap {:foo :f "c" "d" "e" "f"})

(s/validate [s/Any] nil)

(s/defschema FancySeq
  "A sequence that starts with a String, followed by an optional Keyword,
   followed by any number of Numbers."
  [(s/one s/Str "s")
   (s/optional s/Keyword "k")
   s/Num])

(s/validate FancySeq ["test"])
(s/validate FancySeq ["test" :k])
(s/validate FancySeq ["test" :k])
(s/validate FancySeq ["test" :k 1 2 3])

(s/validate FancySeq [1 :k 2 3 "4"])


(s/check (s/pred even?) 1001)

(defn large? [n]
  (> n 10000))

(s/check large? 100000001)
(s/check (s/pred large?) 100000001)

(s/check (s/pred even?) 1001)

