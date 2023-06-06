(ns mybank-web-api.clojure-language.aula3)

(set! *warn-on-reflection* true)


(defn test [^String s]
  (.toUpperCase s))

(test "abc")
;; Funções

(fn [])
(def blabla 0)
(def blabla {:a 0})
(def blabla {:a 0})
(def f (fn []))
(defn f [])


(comment
  (fn f [])


  (fn f [] nil)
  ((fn f [] nil))
  (apply (fn f [] nil) [])

  (fn f [] "fn invoked")

  (def 🤖 (fn f [] "fn invoked"))
  (🤖)
  (fn f [x] (str "fn invoked with 1 arg x as -> " x))
  ((fn f [x] (str "fn invoked with 1 arg x as -> " x)))

  ((def 🤖 (fn f [x] (str "fn invoked with 1 arg x as -> " x))) 0)
  (first (map (fn f [x] (str "fn invoked with 1 arg x as -> " x))
              [0]))
  ((fn f [x y z] (str "fn invoked with 1 arg x as -> " x)) 0)

  (fn f [& xs] (str "fn invoked with rest-args xs as -> " xs))
  ((fn f [& xs] (str "fn invoked with rest-args xs as -> " xs)))
  ((fn f [& xs] (str "fn invoked with rest-args xs as -> " xs)) 0 0 0)
  (apply (fn f [& xs] (str "fn invoked with rest-args xs as -> " xs)) (repeat 999 0))
  (apply (fn f [x y z & xs] (str x y z "fn invoked with rest-args xs as -> " xs)) (range 3))

  (apply f [1 2 3 4])
  (repeat 10 0)
  (range 999)

  (fn f [& xs] (str "fn invoked with args xs as -> " xs " type - x " (type xs)))
  ((fn f [& xs] (str "fn invoked with args xs as -> " xs " type - x " (type xs))) (range 3))

  (defn f [& xs] (str "fn invoked with args xs as -> " xs " type - x " (type xs)))
  (type f)

  (defn f [x & xs] (str "fn invoked with positional x arg as -> " x " and args xs as -> " xs " type - x " (type xs)))

  (defn f [x y & xs] (str "fn invoked with positional x arg as -> " x " second positional y -> " y " and args xs as -> " xs " type - x " (type xs)))

  (defn f [x y & xs] (str "fn invoked with positional x arg as -> " x
                          " second positional y -> " y
                          " and args xs as -> " xs
                          " type - x " (type xs)
                          " and aware of itself as f -> " f))

  (defn f [x y & xs] (str "fn invoked with positional x arg as -> " x
                          " second positional y -> " y
                          " and args xs as -> " xs
                          " type - x " (type xs)
                          " and aware of itself as f -> " f
                          " type of f -> " (type f)))

  (apply f (range 3))
  ((fn f [x y & xs] (str "fn invoked with positional x arg as -> " x
                         " second positional y -> " y
                         " and args xs as -> " xs
                         " type - x " (type xs)
                         " and aware of itself as f -> " f
                         " type of f -> " (type f))) nil nil)

  (apply (defn f [x y & xs] (str "fn invoked with positional x arg as -> " x
                                 "\n second positional y -> " y
                                 "\n and args xs as -> " xs
                                 "\n type - x " (type xs)
                                 "\n and aware of itself as f -> " f
                                 "\n type of f -> " (type f))) (range 3))

  (defn f [x y & xs] (str "fn invoked with positional x arg as -> " x
                          "\n second positional y -> " y
                          "\n and args xs as -> " xs
                          "\n type - x " (type xs)
                          "\n and aware of itself as f -> " f
                          "\n type of f -> " (type f)))

  (def f (fn f [x y & xs] (str "fn invoked with positional x arg as -> " x
                               "\n second positional y -> " y
                               "\n and args xs as -> " xs
                               "\n type - xs " (type xs)
                               "\n and aware of itself as f -> " f
                               "\n type of f -> " (type f))))

  (def f (fn f [x y & xs] (str "fn invoked with positional x arg as -> " x
                               "\n second positional y -> " y
                               "\n and args xs as -> " xs
                               "\n type - x " (type xs)
                               "\n and aware of itself as f -> " f
                               "\n type of f -> " (type f))))

  (defn sym [x y & xs] (str "fn invoked with positional x arg as -> " x
                            "\n second positional y -> " y
                            "\n and args xs as -> " xs
                            "\n type - x " (type xs)
                            "\n and aware of itself as f -> " f
                            "\n type of f -> " (type f)))

  (defn f [x y & xs] (str "fn invoked with positional x arg as -> " x
                          "\n second positional y -> " y
                          "\n and args xs as -> " xs
                          "\n type - x " (type xs)
                          "\n and aware of itself as f -> " f
                          "\n type of f -> " (type f)))

  (fn [] "fn invoked")

  (fn [x] (str "fn invoked with 1 arg x as -> " x))

  (def f (fn [] (str "fn invoked with 1 arg x as -> ")))
  (def f-an #(str "fn invoked with 1 arg x as -> "))
  (meta #'f)
  (meta #'f-an)
  (meta {:a 1})
  (#(str "fn invoked with 1 arg x as -> " %))
  (#(str "fn invoked with 1 arg x as -> " %))
  (#(str "fn invoked with 1 arg x as -> " %) 0)
  (#(str "fn invoked with 1 arg x as -> " %1) 0)
  (#(str "fn invoked with 1 arg x as -> " %1) 0)
  (#(str "fn invoked with 1 arg x as -> " %1 %1 %1) 0)
  (#(str "fn invoked with 1 arg x as -> " %1 %2 %3) 9 8 7)
  (#(str "fn invoked with 1 arg x as -> " %1 %2 %3) 9 8 7)
  (#(str "fn invoked with 1 arg x as -> " %1 %2 %3 %4 %5) 1 2 3 4 5)
  (#(str "fn invoked with 1 arg x as -> " %1 " " %&) 0 9 8 7)
  (#(str "foo -> " %1 ((fn [] (str %1 %1)))) "bar")

  (#(str "foo " %1 %2) 0 0 0)
  (map #(str "foo " %1) [[1 2] [3 4]])
  (map #(str "foo " %1) [1 2 3 4 5 6])
  (map #(str "foo " %1 %2) [1 2] [3 4])
  (map #(str "foo " %1 %2 %3) [1 2] [3 4] [5 6])
  (map #(str "foo " %1 %2 %3) [1 2 3] [3 4 5] [5 6 7 8])

  (def imprime #(str "foo " %1 %2 %3))
  (map imprime [1 2 3] [3 4 5] [5 6 7 8])

  (def imprime #(str "foo " %1))
  (-> "abc"
      clojure.string/upper-case)

  (macroexpand '(-> "abc"
                    clojure.string/upper-case
                    clojure.string/lower-case))

  (->> [1 2 3 4]
       (map imprime))
  (->> [1 2 3 4]
       (map imprime))

  (macroexpand (->> [1 2 3 4]
                    (map imprime)))

  (macroexpand '(->> [1 2 3 4 5 6 7]
                     (map imprime)
                     (filter #(> 2))
                     (remove #(> 6))
                     ))

  (->> [1 2 3 4 5 6 7]
       ;(map imprime)
       (filter #(> % 2))
       (remove #(> % 6))
       )

  (macroexpand (quote
                 (->> [1 2 3 4]
                      (map imprime))))

  ;[[1 2] [3 4]]

  (def map-result-lazy (map #(str "first arg " %1 " second arg " %&) [1 2] [10 20 30]))
  (def map-result-lazy (map #(str "first arg " %1 " second arg " %&) [1 2 3] [10 20 30]))
  (def map-result-lazy (map #(str "arg 1 " %1 " arg 2 " %&) [1 2]))

  (first map-result-lazy)                                   ;; CAR
  (rest map-result-lazy)                                    ;; CAR
  (second map-result-lazy)
  (next map-result-lazy)

  (nth map-result-lazy 2)
  (nth map-result-lazy 3 "vazio")

  (defn asdasdasdas [])


  (apply + [0 1])
  (+ 0 1)

  (-> #(str "return ->" %1)
      (apply [0]))

  (-> #(str "fn invoked with 1 arg x as -> " %1 " and second positional arg -> " %2)
      (apply [0 0]))

  (map (fn [x]
         (println "passe por x -> " x)
         (+ x 9)) [1 2])

  (apply (fn [x y]
           (println "passe por x -> " x)
           (println "passe por x -> " y)
           (+ x y 9)) [1 2])

  (fn [x y]
    (println "passe por x -> " x)
    (println "passe por x -> " y)
    (+ x y 9))

  ;; Closure

  (let [mensagen-externa "Hi! I'm from the fn definition outer context! 1234"]
    (fn [x] (str "I know a closure -> " closure))
    ((fn [x] (str "I know a closure -> " closure)))
    ((fn [x] (str "I know a closure -> " closure)) 0)
    )

  (def mensagen-externa "Hi! I'm from the fn definition outer context! 1234")
  (def mensagen-externa "Hi! I'm from the fn definition outer context! 5678")

  (defn f [x] (str "I receive x -> " x " and I know a closure -> " mensagen-externa))
  (defn f2 [x] (str "I receive x -> " x " and I know a closure -> " mensagen-externa))

  (let []
    (fn [x] (str "I know a closure -> " closure))
    ((fn [x] (str "I know a closure -> " closure)))
    ((fn [x] (str "I know a closure -> " closure)) 0)
    )

  (def closure2 "Hi! I'm anotehr definition outer context! 5678")
  (def 🦠 "Hi! I'm external alien")



  (defn f []
    (str "I remember this stuff from outside -> " 🦠))

  (defn 🤖 [x] (str x " I'm from external alien "))


  (def comands {:run  (defn 🤖 [x] (str x " I'm running "))
                :stop (defn 🤖 [x] (str x " I'm stopping "))})

  (defn call-command [cmd]
    ((get comands cmd))

    )
  (defn f🤖 [x]
    (str "From f " (🤖 x))
    )

  (defn f []
    (str "I remember this stuff from outside -> " 🦠))


  (f 0)

  ;; Advanced arities destructuring

  (apply (fn [] "fn") [])

  (apply (fn [x] "fn") [0])
  (apply (fn [x y] "fn") [0 1])
  (apply (fn [x y] (str "fn" x y)) [0 1])
  (apply (fn [x y] (clojure.string/join " , " ["fn" x y])) [0 1])
  (apply (fn [x y z] (clojure.string/join " , " ["fn" x y z])) [0 1])
  (apply (fn [x y z] (clojure.string/join " , " ["fn" x y z])) [0 1 2])
  (apply (fn [x y z & args] (clojure.string/join " , " ["fn" x y z args])) [0 1 2])
  (apply (fn [x y z & args] (clojure.string/join " , " ["fn" x y z args])) [0 1 2 3])

  (clojure.string/join ["a" "b" "c"])
  (clojure.string/join " , " ["a" "b" "c"])
  \a
  (clojure.string/join \| ["a" "b" "c"])
  (defn j [x y z & args]
    (clojure.string/join " , " ["fn" x y z args]))

  (apply j (range 5))




  (apply (fn [x y z & args]
           (clojure.string/join " , " ["fn" x y z args]))
         [0 1 2 3])

  (apply (fn [x y z & [args]]
           (clojure.string/join " , " ["fn" x y z args]))
         [0 1 2 3])
  (apply (fn [x y z & [args]]
           (clojure.string/join " , " ["fn" x y z args]))
         [0 1 2 3 5 6 7])

  (apply (fn [x y z & [c1 c2]]
           (clojure.string/join " , " ["fn" x y z c1 c2]))
         [0 1 2 3 4 5 6 7])

  (apply (fn [x y z & [args]] (clojure.string/join " , " ["fn" x y z args])) [0 1 2 3 4])
  (apply (fn [x y z & [args1]] (clojure.string/join " , " ["fn" x y z args1])) [0 1 2 3 4])
  (apply (fn [x y z & [args1 args2]] (clojure.string/join " , " ["fn" x y z args1])) [0 1 2 3 4])
  (apply (fn [x y z & [args1 args2]] (clojure.string/join " , " ["fn" x y z args1 args2])) [0 1 2 3 4])
  (apply (fn [x y z & [args1 args2]] (clojure.string/join " , " ["fn" x y z args1 args2])) [0 1 2 3 4 5])
  (apply (fn [x y z & [args1 args2]] (clojure.string/join " , " (concat ["fn" x y z args1 args2]))) [0 1 2 3 4 5])
  (apply (fn [x y z & args] (clojure.string/join " , " (concat ["fn" x y z] args))) [0 1 2 3 4 5])
  (apply (fn [x y z & args] (clojure.string/join " , " (concat ["fn" x y z] args))) [0 1 2 3 4 5 6 7 8])

  (def j clojure.string/join)

  (apply (fn [x] "fn") [0])
  (apply (fn [x] (str "fn " x)) [0])
  (apply (fn [x & options] "fn") [0])
  (apply (fn [x & options] (j " , " ["fn" x])) [0])
  (def j-pipe (partial clojure.string/join " | "))

  (j-pipe ["a" "b" "c"])

  (defn j-pre [del prefixo coll]
    (str prefixo " -> " (clojure.string/join del coll)))

  ((partial j-pre ";" "foo") ["a" "b" "c"])
  ((apply partial j-pre [";" "foo"]) ["a" "b" "c"])

  (defn j-pipe2 [prefixo coll]
    (clojure.string/join prefixo coll))


  (partial clojure.string/join " | ")

  ;; Curring
  (defn f [x y] [x y])
  (f 1 2)

  (def f-pre (partial f))
  (f-pre 1 2)

  (def f-pre2 (partial f 999))
  (f-pre2 1)
  (f-pre2 2)
  (f-pre2 3)

  (def f-pre3 (partial f 999 999))
  (f-pre3)

  (apply (fn [x & options] (j ["fn" x])) [0])


  (defn f [x y] [x y])

  (def f1 (apply partial f [0 1]))
  (f1)

  (def j (partial j " , "))

  (apply (fn [x & options] (j ["fn" x])) [0])


  (def j (partial clojure.string/join " , "))

  (apply (fn [x & options] (j ["fn" x])) [0])

  ;; Map args

  (apply (fn [x & options] (j ["fn" x])) [{:a 1}])
  (apply (fn [x & options] (j ["fn" x])) [{:a 1 :b 1}])
  (apply (fn [x & {:as options}] (j ["fn" x])) [{:a 1 :b 1}])
  (apply (fn [x & {:as options}] (j ["fn" x])) [0 {:a 1 :b 1}])
  (apply (fn [x & {:as options}] (j ["fn" x options])) [0 {:a 1 :b 1}])
  (apply (fn [x & {:keys [a b] :as options}] (j ["fn" x options])) [0 {:a 1 :b 1}])
  (apply (fn [x & {:keys [a b] :as options}] (j ["fn" x a])) [0 {:a 1 :b 1}])
  (apply (fn [x & {:keys [a b]
                   :as   options}] (j ["fn" x a b])) [0 {:a 1 :b 2}])

  (defn f-map [{:keys [a b] :as options}] [a b])
  (def name (fn ([params*] exprs*) +))
  (f-map {:a 1
          :b 2})

  (defn multi-arity
    ([] "zero")
    ([x] "One"))

  (defn multi-arity
    ([] "zero")
    ([x] "One")
    )

  (multi-arity)
  (multi-arity 0)

  (defn multi-arity2
    ([] (multi-arity2 "from zero"))
    ([x] (or x "one"))
    ([y x] (or y x "one"))
    )

  (map)
  (multi-arity2)
  (multi-arity2 0)
  (multi-arity2 "asdsad 0")
  (multi-arity2 nil)

  (defn multi-arity3
    ([a b] (multi-arity3 a b 100))
    ([a b c] (* a b c)))


  (meta #'multi-arity3)

  (defn f [x] (str "fn invoked with 1 arg x as -> " x
                   "\n " (meta #'f)))

  (f 0)
  )

(comment
  "High Order fns"
  (def comands {:run  (fn runf [x] (str x " I'm running "))
                :stop (fn stopf [x] (str x " I'm stopping "))
                :str  #(str "prefix " %)})

  (get comands :run)
  (fn [])
  ((get comands :run) 0)
  ((get comands :stop) 0)
  ((get comands :str) 0)


  (defn call-command [cmd x]
    ((get comands cmd) x))

  (call-command :run 0)
  (call-command :stop 0))
