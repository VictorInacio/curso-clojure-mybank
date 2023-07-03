(ns mybank-web-api.devops.memoize-redis
  (:refer-clojure :exclude [memoize])
  (:require [taoensso.carmine :as car]))

;; boilerplate stuff that is not in Carmine

(def ^:dynamic ^:private *pool*)
(def ^:dynamic ^:private *spec*)

(defmacro with-redis
  "Establish the Redis connection pool and server spec to be used in
  body, and execute body."
  [pool spec & body]
  `(binding [*pool* ~pool
             *spec* ~spec]
     ~@body))

(defmacro exec-commands
  "Execute Redis commands in body using connection pool and server
  spec established using with-redis."
  [& body]
  `(car/with-conn *pool* *spec* ~@body))

;; memoization function

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
      (if-let [val (exec-commands (car/get memo-key))]
        val
        (let [ret (apply f args)]
          (exec-commands (car/set memo-key ret)
                         (when expire (car/expire memo-key expire)))
          ret)))))



(alias 'redis 'mybank-web-api.devops.memoize-redis)
(def pool (car/make-conn-pool))
(def spec (car/make-conn-spec))

(defn add [x y] (+ x y))
(def memoized-add (redis/memoize add :key "add"))

(defn fetch-url [url] (slurp url))
(def memoized-fetch-url (redis/memoize fetch-url :key "urls" :expire 60))

(redis/with-redis pool spec
                  (println (memoized-add 1 1))
                  (println (memoized-add 41 1))

                  (time (memoized-fetch-url "http://www.guardian.co.uk"))
                  (time (memoized-fetch-url "http://www.guardian.co.uk")))