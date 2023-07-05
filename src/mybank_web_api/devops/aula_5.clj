(ns mybank-web-api.devops.aula_5
  (:require [taoensso.carmine :as car]))

(defonce my-conn-pool (car/connection-pool {}))             ; Create a new stateful pool
(def my-conn-spec-1 {:uri "redis://127.0.0.1:6379/"})

(def conn-opts
  {:pool my-conn-pool
   :spec my-conn-spec-1})

(defmacro wcar*
  [& body]
  `(car/wcar conn-opts ~@body))

(def tkey (partial car/key :carmine :aula5))

(defn clear-all-tkeys! []
  (when-let [ks (seq (wcar* (car/keys "*")))]
    (wcar* (doseq [k ks] (car/del k)))))\

(comment
  (clear-all-tkeys!)
  )

(comment
  (def k1 (tkey "superpowers"))
  (def k2 (tkey "birdpowers"))
  (wcar* (car/sadd k1 "flight"))
  (wcar* (car/sadd k1 "x-ray vision"))
  (wcar* (car/sadd k1 "reflexes"))
  (wcar* (car/srem k1 "reflexes"))
  (wcar* (car/sismember k1 "flight"))
  (wcar* (car/sismember k1 "flight2"))

  (wcar* (car/sadd k2 "pecking"))

  (wcar* (car/sunion k1 k2))


  (def k1 (tkey "hackers"))
  (def k2 (tkey "slackers"))
  (def k1-U-k2 (tkey "hackersnslackers"))
  (def k1-I-k2 (tkey "hackerslackers"))

  (wcar* (car/zadd k1 "1940" "Alan Kay"))

  (wcar*
    (car/zadd k1 "1940" "Alan Kay")
    (car/zadd k1 "1953" "Richard Stallman")
    (car/zadd k1 "1965" "Yukihiro Matsumoto")
    (car/zadd k1 "1916" "Claude Shannon")
    (car/zadd k1 "1969" "Linus Torvalds")
    (car/zadd k1 "1912" "Alan Turing")
    (car/zadd k1 "1972" "Dade Murphy")
    (car/zadd k1 "1970" "Emmanuel Goldstein")
    (car/zadd k2 "1968" "Pauly Shore")
    (car/zadd k2 "1972" "Dade Murphy")
    (car/zadd k2 "1970" "Emmanuel Goldstein")
    (car/zadd k2 "1966" "Adam Sandler")
    (car/zadd k2 "1962" "Ferris Beuler")
    (car/zadd k2 "1871" "Theodore Dreiser")
    (car/zunionstore* k1-U-k2 [k1 k2])
    (car/zinterstore* k1-I-k2 [k1 k2]))

  (wcar* (car/zunionstore* k1-U-k2 [k1 k2]))
  (wcar* (car/zinterstore* k1-I-k2 [k1 k2]))
  )



(comment

  "GEOHASH"

  (wcar*
    (car/geoadd (tkey "geo-limits") -180, -85.05112878 "Min")
    (car/geoadd (tkey "geo-limits") 180, 85.05112878 "Max")

    )
  (wcar*
    (car/geoadd (tkey "geo-limits") -180, -85.05112878 "Min")
    (car/geoadd (tkey "geo-limits") 180, 85.05112878 "Max"))

  (wcar*
    (car/zrem (tkey "geo-limits") "Min"))

  (wcar*
    (car/geoadd (tkey "locations") -23.56929876722052, -46.69214033719587 "Ada Office")
    (car/geoadd (tkey "locations") -23.5734476869011, -46.68903598417748 "Padoca"))

  (wcar*
    (car/geohash (tkey "locations") "Ada Office"))

  (wcar*
    (car/geohash (tkey "locations") "Padoca"))

  (wcar*
    (car/geopos (tkey "locations") "Ada Office"))

  (wcar*
    (car/zrange (tkey "locations") 0 -1 "WITHSCORES"))

  (wcar*
    (car/geodist (tkey "locations") "Ada Office" "Padoca"))

  (wcar*
    (car/geodist (tkey "locations") "Ada Office" "Padoca" "km"))

  (wcar*
    (car/geosearch (tkey "locations") "FROMLONLAT" -23.571271096228035, -46.69073797395948 "BYRADIUS" 5 "km" "WITHDIST"))


  (wcar*
    (car/geosearch (tkey "locations") "FROMMEMBER" "Ada Office" "BYRADIUS" 5 "km" "WITHDIST"))


  (wcar*
    (car/geosearch (tkey "locations") "FROMMEMBER" "Ada Office" "BYRADIUS" 5 "km" "WITHDIST" "WITHCOORD"))


  (wcar*
    (car/geosearch (tkey "locations") "FROMMEMBER" "Ada Office" "BYRADIUS" 5 "km" "WITHDIST" "ASC"))

  (wcar*
    (car/geosearch (tkey "locations") "FROMMEMBER" "Ada Office" "BYRADIUS" 5 "km" "WITHDIST" "DESC")))



