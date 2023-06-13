(ns mybank-web-api.clojure-language.aula6)

(comment
  (first '(0 1))                                            ;; CAR
  (first (first '([0 1])))                                  ;; CAR
  (ffirst '([0 1]))                                         ;; CAR
  (rest '(0 1))                                             ;; CDR

  (def xs (cons 0 '(1 2 3)))
  (first xs)
  (rest xs)

  (def fs [first rest])
  ((second fs) xs)

  (def fs2 [#(+ % 1) 1 #(println %) 1 "asdsad" {}])
  ((second fs2) xs)


  (first xs)
  (second xs)
  (nth xs 3)
  (nth xs 4)
  (nth xs 4 "not found")
  (last xs)
  (rest xs)

  (next xs)

  (-> xs
      next
      next
      next
      next
      )

  (next [])
  (rest [])
  (rest xs)
  (-> xs
      rest
      rest
      rest
      rest
      )




  (def xs (range 10))
  (def xs-mil (range 1000000))
  (def xs-infinity (range))
  xs-infinity

  (cons 7777 [0 1 2 3])
  (conj [0 1 2 3] 7777)
  (conj '(0 1 2 3) 7777)


  (def xs-start-stop (range 0 46))
  (type (first xs-start-stop))
  (def xs-step (range 0N 1000 100))
  (def xs-step (range 0 1000N 100))
  (def xs-step (range 0 1000 100N))
  (type (first xs-step))
  (type (second xs-step))
  (type (last xs-step))
  (type 1.1)
  (type 1N)
  (type 1.0M)
  (type 1/2)

  (def xs-step (range 0 10 1/2))
  (def xs-step (range 1/2 10 1/2))
  (def xs-step (range -256 1984 64))

  (instance? clojure.lang.Seqable xs)
  (seq? xs)
  (seqable? xs)

  (cons 0 xs)

  (def xs (range 10))

  (take 2 xs)
  (take-nth 2 xs)
  (take-nth 3 xs)
  (drop 9 (range))
  (drop-while neg? [-1 -2 -6 -7 1 2 3 4 -5 -6 0 1])
  (take-while pos? [1 2 3 4 -1 -2 -6 -7 -5 -6 0 1])


  (def entries [{:month 1 :val 12}
                {:month 2 :val 3}
                {:month 3 :val 32}
                {:month 4 :val 18}
                {:month 5 :val 32}
                {:month 6 :val 62}
                {:month 7 :val 12}
                {:month 8 :val 142}
                {:month 9 :val 52}
                {:month 10 :val 18}
                {:month 11 :val 23}
                {:month 12 :val 56}])

  (defn get-result
    [coll m]
    (take-while
      #(<= (:month %) m) coll))

  (defn ignore-results
    [coll m]
    (drop-while
      #(<= (:month %) m) coll))

  (get-result entries 7)
  (ignore-results entries 7)

  ;;;;;;;; seq de num inteiros

  (def s-map {:a 1 :b 2 :c 3})

  (first s-map)
  (next s-map)

  (-> s-map
      next
      next
      next)

  (-> s-map
      rest
      rest
      rest)

  (rest s-map)

  (instance? clojure.lang.Seqable s-map)

  (hash :a)
  (hash :4)
  (hash [:a])
  (hash {:a 1})
  (hash 0)
  (hash 0)

  (type s-map)

  (first s-map)
  (second s-map)
  (last s-map)
  (rest s-map)

  (cons [:d 4] s-map)
  (cons :d s-map)

  (into {} (rest s-map))
  (into {} '([:d 4] [:a 1] [:b 2] [:c 3]
             [:1 1] [:2 1] [:2 1] [:3 1] [:4 1] [:5 1]))
  (into {:a 1} (rest s-map))



  (defn soma-val
    [[k v]]
    [k (inc v)])

  (soma-val [:a 1])

  (def s-map {:a 1 :b 2 :c 3})

  (map soma-val s-map)

  (->> s-map
       (map soma-val)
       (into {}))

  ;; Seq in, Seq out

  ;Shorter seq from a longer seq:

  (def xs (range 10))

  (distinct [1 1 2 2 3 3])
  (distinct [[1 1]
             [1 1]
             [2 2]
             [2 2]])

  (filter odd? xs)
  (filter even? xs)
  (keep odd? xs)
  ;(keep odd? [0 2 ""])

  (mod 25 5)

  (defn odd-null-pin? [n]
    (when-not (= 0 (mod n 5))
      n))

  (odd-null-pin? 5)
  (odd-null-pin? 6)
  (odd-null-pin? 26)

  (map odd-null-pin? (range 1 16))
  (keep odd-null-pin? (range 1 16))


  (remove odd? xs)
  (remove even? xs)

  (first "tattoo")
  (last "tattoo")
  (apply str (distinct "tattoo"))

  (seq? "tattoo")
  (seqable? "tattoo")

  (for [x (range 10)]
    [(* x x) x])

  (for [x xs]
    x)

  (for [[k v] {:a 1 :b 2 :c 3}]
    [k (inc v)])

  (into {} (for [[k v] {:a 1 :b 2 :c 3}]
             [k (inc v)]))




  (defn odd-null-pin-idx? [idx n]
    (when-not (= 0 (mod n 5))
      [idx n]))

  (keep-indexed odd-null-pin-idx? (range 16))
  (map-indexed odd-null-pin-idx? (range 16))

  )

(comment
  ;;;; first
  (def phone-numbers ["221 610-5007" "221 433-4185"
                      "661 471-3948" "661 653-4480"
                      "661 773-8656" "555 515-0158"])

  (defn unique-area-codes [numbers]
    (->> numbers
         (map #(clojure.string/split % #" "))
         (map first)
         distinct))

  (unique-area-codes phone-numbers)

  (defn all-positives? [coll]
    (cond
      (empty? coll) true
      (pos? (first coll)) (recur (rest coll))
      :else false))

  (all-positives? (list 1 2 3 5 6 7))
  (all-positives? (list 0 1 2 3 4 5 6 -1))

  ;;;; second
  (def temp '((60661 95.2 72.9) (38104 84.5 50.0) (80793 70.2 43.8)
              (99999 199 43.8)))
  (defn max-recorded [temp]
    (->> temp
         (sort-by second >)
         first))
  (max-recorded temp)


  ;;; last
  (def message
    "user:root   echo[b]
     user:ubuntu mount /dev/so
     user:root   chmod 755 /usr/bin/pwd")

  (last (re-seq #"user\:\S+" message))

  ;;; nth
  (let [coll [0 1 2 3 4]] (nth coll 2))

  ;;into
  (into [:g :x :d] [1 5 9])

  (defn maintain [fx f coll]
    (into (empty coll) (fx f coll)))

  (->> #{1 2 3 4 5}
       (maintain map inc)
       (maintain filter odd?))

  (->> {:a 1 :b 2 :c 5}
       (maintain filter (comp odd? last)))

  (into [] (take-nth 2) (range 10))

  )

(comment
  (zipmap [:a :b :c :d :e] [1 2 3 4])
  (zipmap [:a :b :c :d :e] [1 2 3 4 5 7 8])

  (keys s-map)
  (vals s-map)
  (zipmap (keys s-map)
          (vals s-map))

  (reduce into {} [{:dog :food} {:cat :chow}])
  (reductions into {} [{:dog :food} {:cat :chow}])
  (reductions into {:a 1} [{:dog :food} {:cat :chow}])

  (reduce + (map #(* % %) (range 5)))
  (reductions + (map #(* % %) (range 5)))

  (:user-id {:user-id 1 :uri "/"})

  (group-by (fn [x] (odd? (:user-id x)))
            [{:user-id 1 :uri "/"}
             {:user-id 2 :uri "/foo"}
             {:user-id 2 :uri "/foo2"}
             {:user-id 2 :uri "/foo4"}
             {:user-id 3 :uri "/foo5"}
             {:user-id 3 :uri "/account"}])

  (group-by (fn [x] (:user-id x))
            [{:user-id 1 :uri "/"}
             {:user-id 2 :uri "/foo"}
             {:user-id 2 :uri "/foo2"}
             {:user-id 2 :uri "/foo4"}
             {:user-id 3 :uri "/foo5"}
             {:user-id 3 :uri "/account"}])
  (map identity (group-by (fn [x] (odd? (:user-id x)))
                          [{:user-id 1 :uri "/"}
                           {:user-id 2 :uri "/foo"}
                           {:user-id 2 :uri "/foo2"}
                           {:user-id 2 :uri "/foo4"}
                           {:user-id 3 :uri "/foo5"}
                           {:user-id 3 :uri "/account"}]))

  (for [x [0 1 2 3 4 5]
        :let [y (* x 3)]
        :when (even? y)]
    y)

  (for [x (range 3)
        y (range 9)]
    [x y])

  (for [x (range 3) y (range 3)
        :when (not= x y)] [x y])

  (for [x (range 1 6)
        :let [y (* x x)
              z (* x x x)]]
    [x y z])



  (for [x (range 1 6)
        y (range 2 10)]
    [x y])

  )
(comment
  (map - (range 10))

  (apply map vector [[:a :b :c]
                     [:d :e :f]
                     [:g :h :i]])

  (map {2 "two" 3 "three"} [5 3 2])

  (map #(vector (first %) (* 2 (second %)))
       {:a 1 :b 2 :c 3})

  (map-indexed list [:a :b :c])

  (map-indexed (fn [i n]
                 {i n}) (range 10))

  (map-indexed (fn [idx itm] [idx itm]) "foobar")

  (frequencies [:a :b :b :c :c :d])

  (frequencies [(byte 1) (short 1) (int 1) (long 1) 1N])

  (def students
    [{:name "Alice" :age 23 :gender :female}
     {:name "Bob" :age 21 :gender :male}
     {:name "John" :age 23 :gender :male}
     {:name "Maria" :age 22 :gender :female}
     {:name "Julie" :age 22 :gender :female}])

  (frequencies (map :gender students))
  ;; => {:female 3, :male 2}

  (frequencies (map :age students))
  (frequencies (map :name students))

  )
(comment
  (clojure.string/join ";" ["abc" "def"])
  (def my-strings ["one" "two" "three"])

  (interpose ", " my-strings)
  (apply str (interpose ", " my-strings))
  (interpose [] my-strings)
  (interpose \| [1 2 4 {} #{}])
  (apply str (interpose \| [1 2 4 {} #{}]))

  (apply str (interpose ", " my-strings))

  (interpose :orange [:green :red :green :red])

  (interleave [:green :red]
              [:yellow :magenta :cyan]
              [:a :b :c :d])
  )

(comment
  (sort [:a :z :h :e :w])
  (sort [:a :z :h :e :w])
  (sort < [1 8 4 3 3 0 9])
  (sort > [1 8 4 3 3 0 9])

  (sort-by :age [{:name "a" :age 65}
                 {:name "a" :age 13}
                 {:name "a" :age 8}])

  (sort-by :age > [{:name "a" :age 65}
                   {:name "a" :age 13}
                   {:name "a" :age 8}])

  (sort-by str [:f "s" \c 'u])
  )

(comment
  (partition 3 (range 10))
  (partition 3 5 (range 20))
  (partition 5 3 (range 20))
  (partition 4 3 (range 20))
  ;; ((0 1 2) (3 4 5) (6 7 8))
  (partition-all 3 (range 10))

  (partition-by count
                (map str [12 11 8 2 100 102 105 1 3]))

  (partition 3 3 (range 10))

  (partition 3 2 (range 10))
  )
(comment

  (distinct [1 2 1 1 3 2 4 1])
  (distinct? 1 2 3 2 4 1)
  (distinct? 1 2 3 4)
  (dedupe [1 2 1 1 3 2 4 1])

  )
(comment
  ;; Clojure SET
  (set [1 1 2 2 3 3])
  (set [[1 1] [2 2] [3 3] [1 1] [2 2] [3 3] [1 1] [2 2] [3 3]])

  (clojure.set/union #{1 2 3} #{2 3 4 5})
  ;; => #{1 4 3 2 5}

  (clojure.set/intersection #{2 3 4 5} #{1 2 3})
  ;; => #{3 2}

  (clojure.set/difference #{2 3 4 5} #{1 2 3})
  (clojure.set/difference #{1 2 3} #{2 3 4 5} )
  )
(comment
  ;; Exercicios Sequences

  ;; Retorne os multiplos de um dado numero


  ;;; Cartesian products of two sets



  (#(set
      (for [x %1, y %2]
        [x y])) #{1 2 3} #{4 5})

  (= [0 1 2 3 4 5]
     (for [x (range 6)]
       x))

  (= '(0 1 4 9 16 25)
     (map (fn [x] (* x x))
          (range 6))
     (for [x (range 6)]
       (* x x)))

  (= '(1 3 5 7 9)
     (filter odd? (range 10))
     (for [x (range 10) :when (odd? x)]
       x))
  )

(comment
  (defn fibo
    ([n]
     (fibo [0N 1N] n))
    ([xs n]
     (if (<= n (count xs))
       xs
       (let [x'  (+ (last xs)
                    (nth xs (- (count xs) 2)))
             xs' (conj xs x')]
         (fibo xs' n)))))

  (last (fibo 100))

  (last (fibo 10000))
  (last (fibo 30000))

  (last (fibo 30822))

  (defn fibo-recur
    ([n]
     (fibo-recur [0N 1N] n))
    ([xs n]
     (if (<= n (count xs))
       xs
       (let [x'  (+ (last xs)
                    (nth xs (- (count xs) 2)))
             xs' (conj xs x')]
         (recur xs' n)))))

  (last (fibo-recur 30822))

  (last (fibo-recur 30822))

  (last (fibo-recur 40000))

  (last (fibo-recur 50000))

  (defn fibo-loop [n]
    (loop [xs [0N 1N]
           n  n]
      (if (<= n (count xs))
        xs
        (let [x'  (+ (last xs)
                     (nth xs (- (count xs) 2)))
              xs' (conj xs x')]
          (recur xs' n)))))

  )