(ns mybank-web-api.clojure-language.aula8)


(comment
  "Transducers https://www.youtube.com/watch?v=1sC71eb9Ox0"


  "reducing function signature
  whatever, input -> whatever"

  "
  The following functions produce a transducer when the input collection is omitted:

  map cat mapcat filter remove take take-while take-nth
  drop drop-while replace
  partition-by partition-all keep keep-indexed
  map-indexed distinct interpose dedupe random-sample
  "
  (filter odd?)                                             ;; returns a transducer that filters odd
  (map inc)                                                 ;; returns a mapping transducer for incrementing
  (take 5)                                                  ;; returns a transducer that will take the first 5 values

  (def xf
    (comp
      (filter odd?)
      (map inc)
      (take 5)))

  (transduce xf conj () [0 1 2 3 4 5 6 7])
  (transduce xf + 0 [0 1 2 3 4 5 6 7])

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
     (sequence (filter p?) coll))))
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
     (sequence (filter p?) coll))))


(defn candle-transducer [hour-period]
  (let [hour-ms (* hour-period 60 60 1000)]
    (fn [redux-fn]
      (let [buffer     (atom (array-map))
            period     (atom 0)
            start-time (atom 0)
            end-time   (atom 0)]
        (fn
          ([] (redux-fn buffer))
          ([result]
           (redux-fn result))
          ([result candle]
           (let [timestamp (:timestamp candle)]
             (if (zero? @start-time)
               (do
                 (reset! start-time timestamp)
                 (reset! end-time (+ timestamp hour-ms))
                 (reset! period 1)
                 (redux-fn result (assoc candle :period @period)))
               (if (<= timestamp @end-time)
                 (do
                   (swap! period inc)
                   (redux-fn result (assoc candle :period @period)))
                 (let [new-result (redux-fn result (assoc candle :period @period))]
                   (reset! period 1)
                   (reset! start-time timestamp)
                   (reset! end-time (+ timestamp hour-ms))
                   (reset! buffer (assoc @buffer :period 1))
                   new-result))))))))))

;; Example usage
(def candles
  [{:timestamp 1623212000000 :value 9}
   {:timestamp 1624212000000 :value 10}
   {:timestamp 1624215600000 :value 15}
   {:timestamp 1624219200000 :value 20}
   {:timestamp 1624222800000 :value 25}
   {:timestamp 1624226400000 :value 30}
   {:timestamp 1624230000000 :value 35}
   {:timestamp 1624233600000 :value 40}
   {:timestamp 1624237200000 :value 45}
   {:timestamp 1624240800000 :value 50}
   {:timestamp 1624244400000 :value 55}
   {:timestamp 1694244400000 :value 59}])

(def calculate-candles (candle-transducer 1))

;; Using the transducer to calculate candles
(def result (transduce calculate-candles conj candles))

;; Printing the result
(for [candle result]
  candle)