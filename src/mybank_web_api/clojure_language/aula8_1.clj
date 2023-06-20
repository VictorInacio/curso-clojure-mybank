(ns mybank-web-api.clojure-language.aula8_1)

(defn parse-line [line]
  (let [tokens (.split (.toLowerCase line) " ")]
    (map #(vector % 1) tokens)))

(parse-line "O é homem que diz sou não é")

(->> [(parse-line "texto palavra palavro")
      (parse-line "texto palavra ")
      (parse-line "texto")]
     (apply concat)
     (group-by first)
     (map (fn [[k v]]
            {k (map second v)}))
     (apply merge-with conj)

     (merge-with conj {"texto" '(1 1)}
                 {"texto2" '(1 1)})

     (map second [["texto" 1] ["texto" 1]])
     )

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


(comment
  (->> "/Users/victorinacio/ada/rehearsal/curso-clojure-mybank/canto.txt"
       read-lines
       (map parse-line)
       combine)

  )

(defn sum [[k v]]
  {k (apply + v)})

[(sum ["de" '(1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1)])
 (sum ["tristeza" '(1 1 1)])]

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


;;;; AVG

(def any-word "_")

(defn parse-line [line]
  (let [tokens (.split (.toLowerCase line) " ")]
    [[any-word (count tokens)]]))

(defn average [numbers]
  (/ (apply + numbers)
     (count numbers)))

(defn reducer [combined]
  (average (val (first combined))))

(defn average-line-length [filename]
  (map-reduce parse-line reducer (read-lines filename)))


(float
  (average-line-length "/Users/victorinacio/ada/rehearsal/curso-clojure-mybank/canto.txt"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def replace-map {\( ""
                  \) ""})

(clojure.string/escape "(a) (b) (c)" replace-map)



