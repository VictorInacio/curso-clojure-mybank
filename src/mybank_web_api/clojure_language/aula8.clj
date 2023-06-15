(ns mybank-web-api.clojure-language.aula8)

(comment
  "Transducers https://www.youtube.com/watch?v=1sC71eb9Ox0"


  "reducing function signature
  whatever, input -> whatever"

  "
  The following functions produce a transducer when the input collection is omitted:

  map cat mapcat filter remove take take-while take-nth drop drop-while replace
  partition-by partition-all keep keep-indexed map-indexed distinct interpose dedupe random-sample
  "
  (filter odd?) ;; returns a transducer that filters odd
  (map inc)     ;; returns a mapping transducer for incrementing
  (take 5)      ;; returns a transducer that will take the first 5 values

  (comment
    (def xf
      (comp
        (filter odd?)
        (map inc)
        (take 5)))

    (transduce xf conj () [0 1 2 3 4 5 6 7])
    (transduce xf + 100 [0 1 2 3 4 5 6 7])

    (->> (range 10)
         (filter odd?)
         (map inc)
         (take 5))

    (def xf
      (comp
        (filter odd?)
        (map inc)
        (take 5)))



    (def xf-using-partial (comp
                            (partial filter even?)
                            (partial map inc)))

    (xf-using-partial (vec (range 10)))

    (def xf (map inc))

    (transduce xf conj [0 1 2])

    (transduce xf conj () [0 1 2])

    (def xf (comp
              (map inc)
              (filter even?)))

    (transduce xf conj (range 10))

    (into [] xf (range 10))

    (into () xf (range 10))

    (sequence xf (range 10))

    (defn map
      ([f]
       (fn [step]
         (fn
           ([] (step))
           ([result] (step result))
           ([result input]
            (step result (f input))))))
      ([f coll]
       (sequence (map f) coll)))

    (defn filter
      ([p?]
       (fn [step]
         (fn
           ([] (step))
           ([result] (step result))
           ([result input]
            (if (p? input)
              (step result input)
              result)))))
      ([p? coll]
       (sequence (filter p?) coll)))
