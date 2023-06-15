(ns mybank-web-api.clojure-language.aula-7-repl)

"Sequential"
"Associative"
"Namespaced"

(def my-line [[5 10] [10 20]])


(let [p1 (first my-line)
      p2 (second my-line)
      x1 (first p1)
      y1 (second p1)
      x2 (first p2)
      y2 (second p2)]
  (println "Line from ( x1 , y1 ) : " x1 "," y1 ") to (" x2 ", " y2 ")"))

(let [[p1 p2] my-line
      [x1 y1] p1
      [x2 y2] p2]
  (println "Line from (" x1 "," y1 ") to (" x2 ", " y2 ")"))


(def my-vector [1 2 3 4])
(def my-list '(1 2 3))
(def my-string "abc")

(let [[x y z] my-vector]
  (println x y z))

(let [[x y z] my-list]
  (println x y z))

(let [[x y z] my-string]
  (println x y z)
  (map type [x y z]))

(def small-list '(1 2 3))
(let [[a b c d e f g] small-list]
  (println a b c d e f g))

(def large-list '(1 2 3 4 5 6 7 8 9 10))
(let [[a b c] large-list]
  (println a b c))

(def names ["Michael" "Amber" "Aaron" "Nick" "Earl" "Joe"])
(def names ["Michael" "Amber" "Aaron" "Nick" "Earl" "Joe"
            "Michael" "Amber" "Aaron" "Nick" "Earl" "Joe"])

(let [[item1 item2 item3 item4 item5 item6] names]
  (println item1)
  (println item2 item3 item4 item5 item6))

(let [[item1 & remaining] names]
  (println item1)
  (apply println remaining))

(let [[item1 _ item3 _ item5 _] names]
  (println "Odd names:" item1 item3 item5)
  (println _)
  )

(let [[item1 :as all] names]
  (println "The first name from" all "is" item1))

(defn foo [a] (+ a 1))

(foo 5)


(def numbers [1 2 3 4 5])
(let [[x & remaining :as all] numbers]
  ;(apply prn [remaining all])
  (println all))

(def fruits ["apple" "orange" "strawberry" "peach" "pear" "lemon"])
(let [[item1 _ item3 & remaining :as all-fruits] fruits]
  (println "The first and third fruits are" item1 "and" item3)
  (println "These were taken from" all-fruits)
  (println "The fruits after them are" remaining))

(def my-line [[5 10] [10 20]])

(let [[[a b :as group1] [c d :as group2]] my-line]
  (println a b group1)
  (println c d group2))

"Associative"

(def client
  {:name        "Super Co."
   :location    "Philadelphia"
   :description "The worldwide leader in plastic tableware."})

(def client
  {:category    "foo"
   :name        "Super Co."
   :location    "Philadelphia"
   :description "The worldwide leader in plastic tableware."})

(get client :name2 "not found")

(let [name        (:name client)
      location    (:location client)
      description (:description client)]
  (println name location "-" description))

(let [{name2       :name
       location    :location
       description :description} client]
  (println name2 location "-" description))

(let [{category :category} client]
  (println category))

(let [{category :category,
       :or      {category "Category not found"}} client]
  (println category))

(let [{name :name
       :as  all} client]
  (println "The name from" all "is" name))

(def my-map {:a "A" :b "B" :c 3 :d 4})
(let [{a   :a, x :x,
       :or {x "Not found!"}
       :as all} my-map]
  (println "I got" a "from" all)
  (println "Where is x?" x))

(let [{name        :name
       location    :location
       description :description} client]
  (println name location "-" description))

"Keywords"
(let [{:keys [name location description]} client]
  (println name location "-" description))

"Strings"
(def string-keys {"first-name" "Joe"
                  "last-name"  "Smith"})

(get string-keys "first-name")

(let [{:strs [first-name last-name]} string-keys]
  (println first-name last-name))

"Symbols"
(def symbol-keys {'first-name "Jane" 'last-name "Doe"})

(let [{:syms [first-name last-name]} symbol-keys]
  (println first-name last-name))




(def multiplayer-game-state
  {:joe  {:class  "Ranger"
          :weapon {:name  "Longbow"
                   :power 100}
          :score  100}
   :jane {:class  "Knight"
          :weapon "Greatsword"
          :score  140}
   :ryan {:class  "Wizard"
          :weapon "Mystic Staff"
          :score  150}})

(let [{{:keys          [class]
        {power :power
         :as   weapon} :weapon} :joe} multiplayer-game-state]
  (println "Joe is a" class "wielding a" weapon)
  (println "weapon power is" power))

(let [{{:keys [class weapon]} :joe} multiplayer-game-state]
  (let [{power :power
         :as   weapon} weapon]
    (println "Joe is a" class "wielding a" weapon)
    (println "weapon power is" power)))

(name :name)
(name :game.power.player/name)
(namespace :namespace-type.players/name)


"Namespaced"

(def human {:person/name        "Franklin"
            :person/age         25
            :hobby/hobbies      "running"
            :work-hobby/hobbies "bla bla"})

(let [{:keys         [hobby/hobbies]
       :person/keys  [name age]
       :or           {age 0}
       :as           all-map
       hobbies       :hobby/hobbies
       hobbies-works :work-hobby/hobbies} human]
  (println name "is" age "and likes" hobbies)
  (println name "is" age "and works" hobbies-works)
  (println "as" all-map)
  )

(let [{:keys         [hobby/hobbies]
       :person/keys  [name age]
       :or           {age 0}
       hobbies-works :work-hobby/hobbies} human]
  (println name "is" age "and likes" hobbies)
  (println name "is" age "and works" hobbies-works)
  )


"Map Reduce"

(defn calculate []
  (reduce + (map #(* % %) (filter odd? (range 10)))))

(calculate)

[1 2 3]
[1, 2, 3]

(defn calculate* []
  (->> (range 10)
       (filter odd?,,,)
       (map #(* % %),,,)
       (reduce +,,,)))

(calculate*)

(as-> [:foo :bar] v
      (map name v)
      (first v)
      (.substring v 1))

(map inc [0 1 2 3])

(def m (map #(* 2 %) [0 1 2 3]))

(type (map + [0 1 2 3] [4 5 6]))

(mapv inc [0 1 2 3])

(map-indexed (fn [i x] [i x]) "Hello")

(map-indexed + [0 0 0])

(require '[clojure.string :as cs])

(map #(cs/split % #"\d") ["aa1bb2cc3dd" "cc2dd" "ee3ff"])

(map #(cs/split % #"1") ["aa1bb" "cc1dd" "ee1ff"])

(mapcat #(cs/split % #"\d") ["aa1bb2xx4yy" "cc2dd" "ee3ff"])


(reduce #(assoc %1 %2 (inc (%1 %2 0)))
        {}
        (re-seq #"\w+" "aaaaaaaaa"))


;; Calculate primes until 1000

(reduce
  (fn [primes number]
    (if (some zero? (map (partial mod number) primes))
      primes
      (conj primes number)))
  [2]
  (take 1000 (iterate inc 3)))

(take 1000 (iterate inc 3))


(reductions
  (fn [primes number]
    (if (some zero? (map (partial mod number) primes))
      primes
      (conj primes number)))
  [2]
  (take 16 (iterate inc 3)))


(defn map* [f & c]
  (let [c* (partition (count c)
                      (apply interleave c))]
    (reduce (fn [s k]
              (println k)
              (conj s (apply f k))) [] c*)))

(map* inc [2 2 2])
(map* (fn [x y] (* x y)) [1 2 3] [4 5 6])
(map* (fn [x y z] (* x y z)) [1 2 3] [4 5 6] [7 8 9])

(reduce
  (fn [flattened [k v]]
    (clojure.set/union flattened v))
  #{}
  {:e #{:m :f}
   :c #{:f}
   :b #{:c :f}
   :d #{:m :f}
   :a #{:c :f}})

(reductions
  (fn [flattened [k v]]
    (println k)
    (clojure.set/union flattened v))
  #{}
  {:e #{:m :f}
   :c #{:f}
   :b #{:c :f}
   :d #{:m :f}
   :a #{:c :f}})

(filter odd?)                                               ;; returns a transducer that filters odd
(map inc)                                                   ;; returns a mapping transducer for incrementing
(take 5)                                                    ;; returns a transducer that will take the first 5 values


map cat mapcat filter remove take take-while
take-nth drop drop-while replace
partition-by partition-all keep keep-indexed
map-indexed distinct interpose dedupe
random-sample

(transduce (comp (filter odd?)
                 (map inc)
                 (take 4)) conj [1 2 3 4 5 6 7 8 9 10])

(def xf
  (comp
    (filter odd?)
    (map inc)
    (take 5)))

(conj [] 1)
(conj [] 1)
(conj [1 2 3 4])

(transduce xf conj [1 2 3 4 5 6 7 8 9 10])
(transduce xf + [1 2 3 4 5 6 7 8 9 10])


(def xf-using-partial (comp
                        (partial filter even?)
                        (partial map inc)))

(xf-using-partial (vec (range 10)))


(defn filter2
  ([p?]
   (fn [step]
     (fn
       ([]
        (println "step")
        (step))
       ([result]
        (println "step result" result)
        (step result))
       ([result input]
        (println "step result " result " input " input)
        (if (p? input)
          (step result input)
          result)))))
  ([p? coll]
   (sequence (filter p?) coll)))

(transduce (filter2 odd?) + [1 2 3 4])

