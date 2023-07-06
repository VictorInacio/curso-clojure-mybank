(ns mybank-web-api.devops.memoize-redis
  (:refer-clojure :exclude [memoize])
  (:require [taoensso.carmine :as car]))

;; boilerplate stuff that is not in Carmine

;(def ^:dynamic ^:private *pool*)
;(def ^:dynamic ^:private *spec*)

;(def ^:dynamic *pool*)
;(def ^:dynamic *spec*)
;
;(defonce pool (car/make-conn-pool))
;(defonce spec (car/make-conn-spec))
;
;(defmacro with-redis
;  "Establish the Redis connection pool and server spec to be used in
;  body, and execute body."
;  [pool spec & body]
;  `(binding [*pool* ~pool
;             *spec* ~spec]
;     ~@body))
;
;(defmacro exec-commands
;  "Execute Redis commands in body using connection pool and server
;  spec established using with-redis."
;  [& body]
;  `(car/with-conn *pool* *spec* ~@body))

;; memoization function

(defonce my-conn-pool (car/connection-pool {}))             ; Create a new stateful pool
(def my-conn-spec-1 {:uri "redis://127.0.0.1:6379/"})

(def conn-opts
  {:pool my-conn-pool
   :spec my-conn-spec-1})

(defmacro wcar*
  [& body]
  `(car/wcar conn-opts ~@body))


(defn memoize
  "Similar to clojure.core/memoize, but uses Redis to store the
  mapping from arguments to results. The key used will be :key,
  concatenated with \":\" and the EDN representation of the arguments,
  which is not a universally sound idea, but has the advantage of
  being readable. An optional TTL in seconds can be specified
  using :expire."
  [f & {:keys [key expire]}]
  {:pre [(string? key)]}
  (fn [& args]
    (let [memo-key (str key ":" (pr-str args))]
      (if-let [val (wcar* (car/get memo-key))]
        (do
          (println "CACHE HIT 🤠")
          val)
        (let [ret (apply f args)]
          (println "CACHE MISS! 😿")
          (wcar* (car/set memo-key ret)
                 (when expire (car/expire memo-key expire)))
          ret)))))

(comment


  (alias 'redis 'mybank-web-api.devops.memoize-redis)
  (def pool (car/make-conn-pool))
  (def spec (car/make-conn-spec))

  (defn add [x y] (+ x y))

  (add 1 2)
  (defn hash-f [f]
    (-> f
        str
        ))

  (-> add
      str
      ;hash
      )

  (def memoized-add (redis/memoize add :key "add"))

  (defn fetch-url [url] (slurp url))

  (fetch-url "http://www.google.com")
  (def memoized-fetch-url (redis/memoize fetch-url :key "urls" :expire 60))

  (redis/with-redis pool spec
                    (memoized-add 1 1))

  (redis/with-redis pool spec
                    (memoized-fetch-url "https://www.google.com"))

  (redis/with-redis pool spec
                    (time (memoized-fetch-url "https://www.google.com"))
                    (time (memoized-fetch-url "https://www.google.com")))
  (time (+ 1 1))
  (time (+ 1 1)))