(ns mybank-web-api.clojure-language.aula8_1)

(require '[clojure.java.io])

(defn parse-line [line]
  (let [tokens (.split (.toLowerCase line) " ")]
    (map #(vector % 1) tokens)))

(parse-line "O homem que diz sou não é")

(defn combine [mapped]
  (->> (apply concat mapped)
       (group-by first)
       (map (fn [[k v]]
              {k (map second v)}))
       (apply merge-with conj)))


(defn read-lines [file]
  (-> file
      slurp
      clojure.string/split-lines))

(combine
  (map parse-line
       (-> "/Users/victorinacio/ada/rehearsal/curso-clojure-mybank/canto.txt"
           slurp)))

(comment
  (->> "/Users/victorinacio/ada/rehearsal/curso-clojure-mybank/canto.txt"
       slurp
       clojure.string/split-lines
       (map parse-line)
       combine)
  )

(defn sum [[k v]]
  {k (apply + v)})

(defn reduce-parsed-lines [collected-values]
  (apply merge (map sum collected-values)))

(defn word-frequency [filename]
  (->> (read-lines filename)
       (map parse-line)
       (combine)
       (reduce-parsed-lines)))

(->>
  (word-frequency "/Users/victorinacio/ada/rehearsal/curso-clojure-mybank/canto.txt")
  (sort-by second >))


(defn map-reduce [mapper reducer args-seq]
  (->> (map mapper args-seq)
       (combine)
       (reducer)))

(defn word-frequency-mr [filename]
  (map-reduce parse-line reduce-parsed-lines (read-lines filename)))

(word-frequency-mr "/Users/victorinacio/ada/rehearsal/curso-clojure-mybank/canto.txt")



(def IGNORE "_")

(defn parse-line [line]
  (let [tokens (.split (.toLowerCase line) " ")]
    [[IGNORE (count tokens)]]))

(defn average [numbers]
  (/ (apply + numbers)
     (count numbers)))

(defn reducer [combined]
  (average (val (first combined))))

(defn average-line-length [filename]
  (map-reduce parse-line reducer (read-lines filename)))

(average-line-length "/Users/victorinacio/ada/rehearsal/curso-clojure-mybank/canto.txt")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;





