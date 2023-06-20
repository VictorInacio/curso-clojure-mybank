(ns mybank-web-api.clojure-language.aula8-repl)


(def line "O homem que diz sou não é")

(defn parse-line [line]
  (let [tokens (.split (.toLowerCase line) " ")]
    (map #(vector % 1) tokens)))

#_(defn parse-line [line]
    (let [tokens (.split (.toLowerCase line) " ")]
      (map #(vector % (clojure.string/upper-case %)) tokens)))

(parse-line line)

(defn read-lines [file]
  (-> file
      slurp
      clojure.string/split-lines))

(def file "/Users/victorinacio/ada/rehearsal/curso-clojure-mybank/canto.txt")

(read-lines file)

(->> file
     read-lines
     (map parse-line))

(defn combine [mapped]
  (->> (apply concat mapped)
       (group-by first)
       (map (fn [[k v]]
              {k (map second v)}))
       (apply merge-with conj)))

(merge-with conj '({"é" (1)}
                   {"já" (1)}
                   {"quem" (1)}))

(merge-with +
            {:a 1 :b 2}
            {:a 9 :b 98 :c 0}
            {:a -100})
(->> file
     read-lines
     (map parse-line)
     (take 2)
     combine)

(defn sum [[k v]]
  {k (apply + v)})

(sum ["o" '(1 1)])
(apply + '(1 1))
(+ 1 1)


(defn reduce-parsed-lines [collected-values]
  (->> collected-values
       (map sum)
       (apply merge)))

(reduce-parsed-lines {"quem"   '(1),
                      "mesmo"  '(1),
                      "que"    '(1),
                      "homem"  '(1),
                      "dou"    '(1),
                      "não"    '(1 1),
                      "diz"    '(1 1),
                      "dá"     '(1 1),
                      "o"      '(1),
                      "porque" '(1)})

(apply merge '({"quem" 1} {"mesmo" 1} {"que" 1} {"homem" 1} {"dou" 1} {"não" 2} {"diz" 2} {"dá" 2} {"o" 1} {"porque" 1}))

(merge {:a 1} {:b 2})

(defn word-frequency [filename]
  (->> (read-lines filename)
       (map parse-line)
       (combine)
       (reduce-parsed-lines)))

(word-frequency file)

(sort (word-frequency file))

(sort-by second > (word-frequency file))

(into {}
      (sort-by second > (word-frequency file)))

(hash "cai")
(hash "esquecer")
(hash "porque")

(defn map-reduce [mapper reducer args-seq]
  (->> (map mapper args-seq)
       (combine)
       (reducer)))

(defn word-frequency-mr [filename]
  (map-reduce parse-line reduce-parsed-lines (read-lines filename)))

(word-frequency-mr file)

;;;;;;;;;;;;;;;;;

(def any-word "_")

(defn parse-line [line]
  (let [tokens (.split (.toLowerCase line) " ")]
    [[any-word (count tokens)]]))

(parse-line line)

(->> file
     read-lines
     (map parse-line)
     ;(take 25)
     combine)

(defn average [numbers]
  (/ (apply + numbers)
     (count numbers)))

(float
  (average '(7 6 7 6 7 7 7 5 5 5 5 5 5 5 5 7 4 7 8 3 6 3 5 7 5)))

(defn reducer [combined]
  (average (val (first combined))))

[:a 1]
(val (first {:a 1
             :b 2}))

(float (map-reduce parse-line reducer (read-lines file)))


(defn average-line-length [filename]
  (float (map-reduce parse-line reducer (read-lines filename))))

(average-line-length file)



