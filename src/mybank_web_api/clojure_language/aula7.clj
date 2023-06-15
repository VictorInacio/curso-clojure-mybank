(ns mybank-web-api.clojure-language.aula7)

(comment
  "Desctructuring"
  (def my-line [[5 10] [10 20]])

  (let [p1 (first my-line)
        p2 (second my-line)
        x1 (first p1)
        y1 (second p1)
        x2 (first p2)
        y2 (second p2)]
    (println "Line from (" x1 "," y1 ") to (" x2 ", " y2 ")"))

  (let [[p1 p2] my-line
        [x1 y1] p1
        [x2 y2] p2]
    (println "Line from (" x1 "," y1 ") to (" x2 ", " y2 ")"))


  "Sequential"

  (def my-vector [1 2 3])
  (def my-list '(1 2 3))
  (def my-string "abc")

  ;= It should come as no surprise that this will print out 1 2 3
  (let [[x y z] my-vector]
    (println x y z))
  ;= 1 2 3

  ;= We can also use a similar technique to destructure a list
  (let [[x y z] my-list]
    (println x y z))
  ;= 1 2 3

  ;= For strings, the elements are destructured by character.
  (let [[x y z] my-string]
    (println x y z)
    (map type [x y z]))
  ;= a b c
  ;= (java.lang.Character java.lang.Character java.lang.Character)

  (def small-list '(1 2 3))
  (let [[a b c d e f g] small-list]
    (println a b c d e f g))

  (def large-list '(1 2 3 4 5 6 7 8 9 10))
  (let [[a b c] large-list]
    (println a b c))

  (def names ["Michael" "Amber" "Aaron" "Nick" "Earl" "Joe"])

  (let [[item1 item2 item3 item4 item5 item6] names]
    (println item1)
    (println item2 item3 item4 item5 item6))

  (let [[item1 & remaining] names]
    (println item1)
    (apply println remaining))

  (let [[item1 _ item3 _ item5 _] names]
    (println "Odd names:" item1 item3 item5))

  (let [[item1 :as all] names]
    (println "The first name from" all "is" item1))

  (def numbers [1 2 3 4 5])
  (let [[x & remaining :as all] numbers]
    (apply prn [remaining all]))

  (def word "Clojure")
  (let [[x & remaining :as all] word]
    (apply prn [x remaining all]))

  (def fruits ["apple" "orange" "strawberry" "peach" "pear" "lemon"])
  (let [[item1 _ item3 & remaining :as all-fruits] fruits]
    (println "The first and third fruits are" item1 "and" item3)
    (println "These were taken from" all-fruits)
    (println "The fruits after them are" remaining))

  (def my-line [[5 10] [10 20]])

  (let [[[x1 y1] [x2 y2]] my-line]
    (println "Line from (" x1 "," y1 ") to (" x2 ", " y2 ")"))

  (let [[[a b :as group1] [c d :as group2]] my-line]
    (println a b group1)
    (println c d group2))


  "Associative"

  (def client {:name        "Super Co."
               :location    "Philadelphia"
               :description "The worldwide leader in plastic tableware."})

  (let [name        (:name client)
        location    (:location client)
        description (:description client)]
    (println name location "-" description))

  (let [{name        :name
         location    :location
         description :description} client]
    (println name location "-" description))

  (let [{category :category} client]
    (println category))

  (let [{category :category, :or {category "Category not found"}} client]
    (println category))

  (let [{name :name :as all} client]
    (println "The name from" all "is" name))

  (def my-map {:a "A" :b "B" :c 3 :d 4})
  (let [{a :a, x :x, :or {x "Not found!"}, :as all} my-map]
    (println "I got" a "from" all)
    (println "Where is x?" x))

  (let [{:keys [name location description]} client]
    (println name location "-" description))

  (def string-keys {"first-name" "Joe" "last-name" "Smith"})

  (let [{:strs [first-name last-name]} string-keys]
    (println first-name last-name))

  (def symbol-keys {'first-name "Jane" 'last-name "Doe"})

  (let [{:syms [first-name last-name]} symbol-keys]
    (println first-name last-name))

  (def multiplayer-game-state
    {:joe  {:class  "Ranger"
            :weapon "Longbow"
            :score  100}
     :jane {:class  "Knight"
            :weapon "Greatsword"
            :score  140}
     :ryan {:class  "Wizard"
            :weapon "Mystic Staff"
            :score  150}})

  (let [{{:keys [class weapon]} :joe} multiplayer-game-state]
    (println "Joe is a" class "wielding a" weapon))

  "Keywords"

  (defn configure [val options]
    (let [{:keys [debug verbose] :or {debug false, verbose false}} options]
      (println "val =" val " debug =" debug " verbose =" verbose)))

  (configure 12 {:debug true})

  (configure 12 :debug true)

  (defn configure [val & {:keys [debug verbose]
                          :or   {debug false, verbose false}}]
    (println "val =" val " debug =" debug " verbose =" verbose))

  (configure 10)
  ;;val = 10  debug = false  verbose = false

  (configure 5 :debug true)
  ;;val = 5  debug = true  verbose = false

  ;; Note that any order is ok for the kwargs
  (configure 12 :verbose true :debug true)
  ;;val = 12  debug = true  verbose = true

  (configure 12 {:verbose true :debug true})
  ;;val = 12  debug = true  verbose = true

  (configure 12 :debug true {:verbose true})
  ;;val = 12  debug = true  verbose = true

  "Namespaced"

  (def human {:person/name   "Franklin"
              :person/age    25
              :hobby/hobbies "running"})
  (let [{:keys        [hobby/hobbies]
         :person/keys [name age]
         :or          {age 0}} human]
    (println name "is" age "and likes" hobbies))

  (def human {:person/name "Franklin"
              :person/age  25
              :hobby/name  "running"})
  (let [{:person/keys [age]
         hobby-name   :hobby/name
         person-name  :person/name} human]
    (println person-name "is" age "and likes" hobby-name))


  (create-ns 'person) (alias 'p 'person)
  (require '[person :as p])

  (let [person {::p/name "Franklin", ::p/age 25}
        {:keys [::p/name ::p/age]} person]
    (println name "is" age))

  (defn f-with-options
    [a b & {:keys [opt1]}]
    (println "Got" a b opt1))

  (f-with-options 1 2 :opt1 true)

  "More Usage Examples"

  (defn print-coordinates-1 [point]
    (let [x (first point)
          y (second point)
          z (last point)]
      (println "x:" x ", y:" y ", z:" z)))

  (defn print-coordinates-2 [point]
    (let [[x y z] point]
      (println "x:" x ", y:" y ", z:" z)))

  (defn print-coordinates-3 [[x y z]]
    (println "x:" x ", y:" y ", z:" z))

  (def john-smith {:f-name  "John"
                   :l-name  "Smith"
                   :phone   "555-555-5555"
                   :company "Functional Industries"
                   :title   "Sith Lord of Git"})

  (defn print-contact-info [{:keys [f-name l-name phone company title]}]
    (println f-name l-name "is the" title "at" company)
    (println "You can reach him at" phone))

  (print-contact-info john-smith)

  (def john-smith {:f-name  "John"
                   :l-name  "Smith"
                   :phone   "555-555-5555"
                   :address {:street "452 Lisp Ln."
                             :city   "Macroville"
                             :state  "Kentucky"
                             :zip    "81321"}
                   :hobbies ["running" "hiking" "basketball"]
                   :company "Functional Industries"
                   :title   "Sith Lord of Git"})


  (defn print-contact-info
    [{:keys                           [f-name l-name phone company title]
      {:keys [street city state zip]} :address
      [fav-hobby second-hobby]        :hobbies}]
    (println f-name l-name "is the" title "at" company)
    (println "You can reach him at" phone)
    (println "He lives at" street city state zip)
    (println "Maybe you can write to him about" fav-hobby "or" second-hobby))

  (print-contact-info john-smith)

  )

(comment
  "Map Reduce"


  (map inc [0 1 2 3])

  (map #(* 2 %) [0 1 2 3])

  (map + [0 1 2 3] [4 5 6])

  (mapv inc [0 1 2 3])

  (map-indexed (fn [i x] [i x]) "Hello")

  (require '[clojure.string :as cs])

  (map #(cs/split % #"\d") ["aa1bb" "cc2dd" "ee3ff"])

  (mapcat #(cs/split % #"\d") ["aa1bb" "cc2dd" "ee3ff"])

  (reduce + [1 2 3 4 5])

  (+ (+ (+ (+ 1 2) 3) 4) 5)

  (reduce + [])
  (+)

  (reduce + 99 [1 2])

  (+ (+ 99 1) 2)

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

  ;; The reduction will terminate early if an intermediate result uses the
  ;; `reduced` function.

  (defn limit [x y]
    (let [sum (+ x y)]
      (if (> sum 10) (reduced sum) sum)))

  (reduce + 0 (range 10))
  (reductions + 0 (range 10))
  ;; => 45

  (reduce limit 0 (range 10))
  ;; => 15

  ;; Implementing Reduce

  (defn reduce* [rf accum coll]
    (if (seq coll)
      (let [[head & tail] coll
            accum (rf accum head)]
        (recur rf accum tail))
      coll))

  (defn reduce* [rf accum coll]
    (if (seq coll)
      (let [[head & tail] coll
            result (rf accum head)]
        (if (reduced? result)
          result
          (recur rf result tail)))
      coll))

  (reduced 1)
  ;; Reduce can be used to reimplement a map function:

  (defn map* [f & c]
    (let [c* (partition (count c)
                        (apply interleave c))]
      (reduce (fn [s k] (conj s (apply f k))) [] c*)))

  ;; Flatten values in a map.
  (reduce
    (fn [flattened [k v]]
      (clojure.set/union flattened v))
    #{}
    {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}})

  ;; => #{:m :c :f}


  (defn calculate []
    (reduce + (map #(* % %) (filter odd? (range 10)))))

  (defn calculate* []
    (->> (range 10)
         (filter odd?,,,)
         (map #(* % %),,,)
         (reduce +,,,)))

  (as-> [:foo :bar] v
        (map name v)
        (first v)
        (.substring v 1))

  )

