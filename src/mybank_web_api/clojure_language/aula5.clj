(ns mybank-web-api.clojure-language.aula5)




(def counter (atom 0))
(defn inc-print [val] (println val))

(def counter (atom 0))

(let [n 2]
  (future (dotimes [_ n] (swap! counter inc-print)))
  (future (dotimes [_ n] (swap! counter inc-print)))
  (future (dotimes [_ n] (swap! counter inc-print))))







;; Concurrency

(def a (atom 0))

(deref a)

(reset! a 0)

(let [fs (for [n (range 100)]
           (+ n 10))]
  fs)

(let [fs (for [_ (range 100)]
           (future
             (dotimes [_ 100]
               (reset! a (inc @a)))))]
  (doseq [f fs]
    @f))

(deref a)








;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(reset! a 0)

(let [fs (for [_ (range 100)]
           (future
             (dotimes [_ 100]
               (let [v @a]
                 (compare-and-set! a v (inc v))))))]
  (doseq [f fs]
    @f))

(deref a)










;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(reset! a 0)
(let [fs (for [_ (range 100)]
           (future
             (dotimes [_ 100]
               (loop []
                 (let [v @a]
                   (when-not (compare-and-set! a v (inc v))
                     (recur)))))))]
  (doseq [f fs]
    @f))


(deref a)










;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(let [fs (for [_ (range 10000)]
           (future
             (dotimes [_ 10000]
               (loop []
                 (let [v @a]
                   (when-not (compare-and-set! a v (inc v))
                     (println \.)
                     (recur)))))))]
  (doseq [f fs]
    @f))

(deref a)




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(let [fs (for [_ (range 100)]
           (future
             (dotimes [_ 100]
               (swap! a inc))))]
  (doseq [f fs]
    @f))

(reset! a 0)
(deref a)




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Futures

(def f
  (future
    (Thread/sleep 1000)
    (println "done")
    100))

@f
(future? f)
(future-done? f)
(future-cancel f)
(future-cancelled? f)




(defn long-calculation [num1 num2]
  (Thread/sleep 1000)
  (* num1 num2))

(defn long-run []
  (let [x (long-calculation 11 13)
        y (long-calculation 13 17)
        z (long-calculation 17 19)]
    (* x y z)))

(time (long-run))

(defn fast-run []
  (let [x (future (long-calculation 11 13))
        y (future (long-calculation 13 17))
        z (future (long-calculation 17 19))]
    (* @x @y @z)))

(time (fast-run))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Promisses

(def x (promise))
(realized? x)
(deliver x "p")

@x

(def p (promise))

(future
  (Thread/sleep 10000)
  (deliver p 123))

(realized? p)
(deref p)

(let [p (promise)]
  (let [google {:url "https://amazon.com"
                :site-name :google}
        news   "https://news.ycombinator.com"]
    (doseq [url [google news]]
      (future (let [response (slurp url)]
                (deliver p response)))))
  @p)


(def x (promise))
(def y (promise))
(def z (promise))

(future
  (do (deliver z (+ @x @y))
      (println "z value : " @z)))

(realized? x)
(realized? y)
(realized? z)
(deliver x 56)
(deliver y 54)


(defn long-running-task []
  (Thread/sleep 5000)
  (println "COMPLETED long-running-task"))


(defn launch-timed []
  (let [begin-promise (promise)
        end-promise   (promise)]
    (future (deliver begin-promise (System/currentTimeMillis))
            (long-running-task)
            (deliver end-promise (System/currentTimeMillis)))
    (println "task begin at" @begin-promise)
    (println "task end at" @end-promise)))


;; Delay

(def my-delay (delay (println "did some work") 100))

(force my-delay)

;; Memoize

(defn myfunc [a] (println "doing some work") (+ a 10))

(myfunc 1)
(myfunc 2)

(def myfunc-memo (memoize myfunc))

(myfunc-memo 1)
(myfunc-memo 2)

;; Fibonacci number with recursion.
(defn fib [n]
  (condp = n
    0 1
    1 1
    (+ (fib (dec n)) (fib (- n 2)))))

(time (fib 40))

(def m-fib
  (memoize (fn [n]
             (condp = n
               0 1
               1 1
               (+ (m-fib (dec n)) (m-fib (- n 2)))))))

(time (m-fib 50))


(defn memoize2
  [f mem]
  (fn [& args]
    (if-let [e (find @mem args)]
      (val e)
      (let [ret (apply f args)]
        (swap! mem assoc args ret)
        ret))))

(def memoria-fn-quadrat (atom {}))

(defn quadrado [x]
  (* x))

(def quadrat-mem
  (memoize2 quadrado memoria-fn-quadrat))

(deref memoria-fn-quadrat)

(quadrat-mem 0)
(quadrat-mem 2)

(doseq [n (range 10)]
  (quadrat-mem n))