(ns mybank-web-api.devops.aula-4
  (:require [clojure.string :as str]
            [clojure.test :as test :refer [deftest testing is]]
            [taoensso.encore :as enc :refer [throws?]]
            [taoensso.carmine :as car :refer [wcar]]
            [taoensso.carmine.commands :as commands]
            [taoensso.carmine.protocol :as protocol]
            [taoensso.carmine.benchmarks :as benchmarks])
  (:import (scala.reflect.runtime JavaMirrors$JavaMirror)))
"Iniciar servidor Redis local
/opt/homebrew/opt/redis/bin/redis-server /opt/homebrew/etc/redis.conf"


"Conectar cliente"

(defonce my-conn-pool (car/connection-pool {}))             ; Create a new stateful pool
(def my-conn-spec-1 {:uri "redis://127.0.0.1:6379/"})

(def my-wcar-opts
  {:pool my-conn-pool
   :spec my-conn-spec-1})

;;;; Config, etc.

(def conn-opts my-wcar-opts)

(defn foo [a b c]

  (+ 1 1)
  (+ 1 1)
  (+ 1 1)
  (+ 1 1)
  )

(car/wcar conn-opts
          (doseq [n (range 5)]
            (car/set (str "key-" n) (str "val-" n))))


(defmacro wcar*
  [& body]
  `(car/wcar conn-opts ~@body))

(car/wcar conn-opts
          (car/ping))

(wcar*
  (doseq [n (range 15)]
    (car/set (str "key-" n) (str "val-" n))))

(wcar* (car/set :client/texto :valor))

(wcar* (car/set "mapa" {:a1 1
                        :b2 [4 5 6]}))

(wcar* (car/get "mapa"))





(def tkey (partial car/key :carmine :temp :test))
(tkey "texto")

(wcar* (car/set (tkey "texto") "valor"))
(wcar* (car/keys "carmine*"))


(defn clear-all-tkeys! []
  (when-let [ks (seq (wcar* (car/keys "*")))]
    (wcar* (doseq [k ks] (car/del k)))))


(defn clear-tkeys! []
  (when-let [ks (seq (wcar* (car/keys (tkey :*))))]
    (wcar* (doseq [k ks] (car/del k)))))

(comment
  (clear-all-tkeys!)
  (tkey :*)
  (wcar* (car/keys (tkey :*)))
  (wcar* (car/keys "*"))
  (clear-tkeys!))

(defn test-fixture [f] (clear-tkeys!) (f) (clear-tkeys!))
(test/use-fixtures :once test-fixture)


(defn sleep [n] (Thread/sleep (int n)) (str "slept " n "msecs"))

;;;;
(wcar* (car/echo "Message"))
(wcar* (car/ping))
(wcar* (car/ping "Oi ping"))

(deftest basic-tests
  [(is (= (wcar* (car/echo "Message")) "Message"))
   (is (= (wcar* (car/ping)) "PONG"))])

(deftest key-exists-test
  (let [_ (clear-tkeys!)
        k (tkey "exists")]
    [(is (= (wcar* (car/exists k)) 0) "Since key does not exists should return 0")
     (wcar* (car/set k "exists!"))
     (is (= (wcar* (car/exists k)) 1) "Now that key is set, it should exists!")]))

(def k (tkey "exists"))

[(is (= (wcar* (car/exists k)) 0) "Since key does not exists should return 0")
 (wcar* (car/set k "exists!"))
 (is (= (wcar* (car/exists k)) 1) "Now that key is set, it should exists!")]

(deftest getset-test
  (let [_ (clear-tkeys!)
        k (tkey "server:name")]
    [(is (= (wcar* (car/set k "fido")) "OK"))
     (is (= (wcar* (car/get k)) "fido"))
     (is (= (wcar* (car/append k " [shutdown]")) 15))
     (is (= (wcar* (car/getset k "fido [running]")) "fido [shutdown]"))
     (is (= (wcar* (car/getrange k 6 12)) "running"))]))
"Roundtrip"

(def k (tkey "server:name"))

(wcar* (car/set k "fido"))
(wcar* (car/append k " [shutdown]"))

(wcar* (car/getset k "fido [running]"))

(wcar* (car/get k))
(wcar* (car/getrange k 6 12))
(wcar* (car/getrange k -5 -1))

(deftest setbit-test
  (let [_ (clear-tkeys!)
        k (tkey "mykey")]
    [(is (= (wcar* (car/setbit k 7 1)) 0))
     (is (= (wcar* (car/getbit k 7)) 1) "7th bit of the key was set to 1")]))
(def k (tkey "mykey"))
(wcar* (car/setbit k 7 1))
(wcar* (car/getbit k 7))


(deftest multiline-test
  (let [_ (clear-tkeys!)
        k (tkey "multiline")]
    [(is (= (wcar* (car/set k "Redis\r\nDemo")) "OK"))
     (is (= (wcar* (car/get k)) "Redis\r\nDemo"))]))

(def k (tkey "multiline"))

(wcar* (car/set k "Redis\r\nDemo"))
(wcar* (car/get k))

(deftest inc-dec-tests
  (let [_ (clear-tkeys!)
        k (tkey "connections")]
    [(wcar* (car/set k 10))
     (is (= (wcar* (car/incr k)) 11))
     (is (= (wcar* (car/incrby k 9)) 20))
     (is (= (wcar* (car/decr k)) 19))
     (is (= (wcar* (car/decrby k 9)) 10))]))
(def k (tkey "connections"))
(wcar* (car/set k 10))
(wcar* (car/incr k))
(wcar* (car/incrby k 99))
(wcar* (car/incrby k -99))
(wcar* (car/decr k))
(wcar* (car/decrby k 118))
;;;;;
(deftest delete-key-test
  (let [_ (clear-tkeys!)
        k (tkey "something")]
    [(wcar* (car/set k "foo"))
     (is (= (wcar* (car/del k)) 1))]))

(wcar* (car/del "carmine:temp:test:connections"))
(wcar* (car/del "carmine:temp:test:multiline"))

(deftest expiry-tests
  (let [_ (clear-tkeys!)
        k (tkey "resource:lock")]
    [(wcar* (car/set k "Redis Demo"))
     (is (= (wcar* (car/ttl k)) -1))
     (is (= (wcar* (car/expire k 120)) 1))
     (is (pos? (wcar* (car/ttl k))))]))

(def k (tkey "resource:lock"))
(wcar* (car/set k "Redis Demo"))
(wcar* (car/get k))
(wcar* (car/ttl k))
(wcar* (car/expire k 10))

(deftest array-cmds-tests
  (let [_ (clear-tkeys!)
        k (tkey "friends")]
    [(testing "Push command"
       [(is (= (wcar* (car/rpush k "Tom")) 1))
        (is (= (wcar* (car/rpush k "Bob")) 2))
        (is (= (wcar* (car/lpush k "Sam")) 3))
        (is (= (wcar* (car/lrange k 0 -1)) ["Sam" "Tom" "Bob"]))])

     (testing "lrange command"
       [(is (= (wcar* (car/lrange k 0 1)) ["Sam" "Tom"]))
        (is (= (wcar* (car/lrange k 0 2)) ["Sam" "Tom" "Bob"]))])

     (testing "len and pop commands"
       [(is (= (wcar* (car/llen k)) 3))
        (is (= (wcar* (car/lpop k)) "Sam"))
        (is (= (wcar* (car/rpop k)) "Bob"))
        (is (= (wcar* (car/llen k)) 1))
        (is (= (wcar* (car/lrange k 0 -1)) ["Tom"]))])]))

(def k (tkey "friends"))

(wcar* (car/rpush k "Tom"))
(wcar* (car/rpush k "Tom2"))

(wcar* (car/lpush k "Sam"))

(str (Thread.))

(wcar* (car/del k))
(wcar* (car/rpush k "Tom"))
(wcar* (car/rpush k ["Tom" "ABC" "DEF"]))
(wcar* (car/rpush k "Tom" "ABC" "DEF"))
(wcar*
  (doseq [i ["Tom" "ABC" "DEF"]]
    (car/rpush k i)))
(wcar* (car/lpush k "Sam"))
(wcar* (car/lpush k "3"))
(wcar* (car/lpush k "4"))


(wcar* (car/lrange k 0 3))
(wcar* (car/lrange k 0 2))
(wcar* (car/lrange k -3 -2))


(deftest get-set-spanish-test
  (let [_ (clear-tkeys!)
        k (tkey "spanish")]
    [(is (= (wcar* (car/set k "year->año") (car/get k)) ["OK" "year->año"]))]))

(def k (tkey "spanish"))

(wcar* (car/set k "year->año")
       (car/get k)
       (car/get k)
       (car/get k)
       (car/get k)
       )

(deftest exception-test
  (let [_ (clear-tkeys!)
        k (tkey "str-field")]

    [(wcar* (car/set k "str-value"))
     (is (throws? clojure.lang.ExceptionInfo (wcar* (car/incr k)))
         "Can't increment a string value")

     (let [[r1 r2 r3] (wcar* (car/ping) (car/incr k) (car/ping))]
       [(is (= r1 "PONG"))
        (is (instance? clojure.lang.ExceptionInfo r2))
        (is (= r3 "PONG"))])]))


(def k (tkey "str-field"))
(wcar* (car/set k "str-value"))
(wcar* (car/incr k))
(wcar* (car/ping) (car/incr k) (car/ping))


(deftest malformed-tests
  [(is (= (wcar* "This is a malformed request") nil))
   (is (= (wcar* (car/ping) "This is a malformed request") "PONG"))])

(deftest pong-test
  (is (= (wcar* (doall (repeatedly 3 car/ping))) ["PONG" "PONG" "PONG"])))

(deftest echo-test
  (is (= (wcar* (doall (map car/echo ["A" "B" "C"]))) ["A" "B" "C"])))

(deftest key-inside-key-test
  (let [_     (clear-tkeys!)
        out-k (tkey "outside-key")
        in-k  (tkey "inside-key")]
    [(wcar*
       (car/set in-k "inside value")
       (car/set out-k in-k))
     (is (= (wcar* (car/get (last (wcar* (car/ping) (car/get out-k))))) "inside value")
         "Should get the inner value")]))

(def out-k (tkey "outside-key"))
(def in-k (tkey "inside-key"))

(wcar*
  (car/set in-k "inside value")
  (car/set out-k in-k))

(wcar*
  (car/get
    (last (wcar* (car/ping) (car/get out-k)))))

(wcar* (car/ping) (car/get out-k))

(wcar* (car/get (wcar* (car/get out-k))))



(= (wcar* (car/get (last (wcar* (car/ping) (car/get out-k))))) "inside value")

(let [out-k (tkey "outside-key")
      in-k  (tkey "inside-key")]
  [(wcar*
     (car/set in-k "inside value")
     (car/set out-k in-k))
   (is (= (wcar* (car/get (last (wcar* (car/ping) (car/get out-k))))) "inside value")
       "Should get the inner value")])



(deftest parallel-incr-test
  (let [_ (clear-tkeys!)
        k (tkey "parallel-key")]
    (wcar* (car/set k 0))
    (->>
      (repeatedly 100                                       ; No. of parallel clients
                  (fn [] (future (dotimes [n 100] (wcar* (car/incr k))))))
      (doall)
      (map deref)
      (dorun))
    (is (= (wcar* (car/get k)) "10000"))))

(deftest hash-set-test
  (let [_ (clear-tkeys!)
        k (tkey "myhash")]
    [(is (= (wcar* (car/hset k "field1" "value1")) 1))
     (is (= (wcar* (car/hget k "field1")) "value1"))
     (is (= (wcar* (car/hsetnx k "field1" "newvalue")) 0))
     (is (= (wcar* (car/hget k "field1")) "value1"))
     (is (= (wcar* (car/hexists k "field1")) 1))
     (is (= (wcar* (car/hgetall k)) ["field1" "value1"]))
     (is (= (wcar* (car/hset k "field2" 1)) 1))
     (is (= (wcar* (car/hincrby k "field2" 2)) 3))
     (is (= (wcar* (car/hkeys k)) ["field1" "field2"]))
     (is (= (wcar* (car/hvals k)) ["value1" "3"]))
     (is (= (wcar* (car/hlen k)) 2))
     (is (= (wcar* (car/hdel k "field1")) 1))
     (is (= (wcar* (car/hexists k "field1")) 0))]))

(def k (tkey "myhash"))

(wcar* (car/hset k "field1" "value1"))
(wcar* (car/hset k "field2" 0))
(wcar* (car/hset k "field3" 10))
(wcar* (car/hset k "field4" 10))
(wcar* (car/hset k "field5" 5 "field6" 6))
(wcar* (car/hget k "field1"))

(wcar* (car/hsetnx k "fieldNEW" "newvalue"))

(wcar* (car/hexists k "field1"))
(wcar* (car/hexists k "field2"))
(wcar* (car/hexists k "fieldNOT"))

(wcar* (car/hgetall k))

(apply hash-map (wcar* (car/hgetall k)))

(apply hash-map ["field1" "value1" "field2" "value2"])
(hash-map :1 1 :2 2)

(wcar* (car/hincrby k "field1" 1))
(wcar* (car/hincrby k "field2" 2))


(wcar* (car/hkeys k))
(wcar* (car/hvals k))
(wcar* (car/hlen k))
(wcar* (car/hdel k "field1"))

(wcar* (car/hrandfield k))


(deftest set-tests
  (let [_  (clear-tkeys!)
        k1 (tkey "superpowers")
        k2 (tkey "birdpowers")]

    [(testing "Member in set case"
       [(is (= (wcar* (car/sadd k1 "flight")) 1))
        (is (= (wcar* (car/sadd k1 "x-ray vision")) 1))
        (is (= (wcar* (car/sadd k1 "reflexes")) 1))
        (is (= (wcar* (car/sadd k1 "reflexes")) 0))
        (is (= (wcar* (car/srem k1 "reflexes")) 1))
        (is (= (wcar* (car/sismember k1 "flight")) 1))])

     (testing "Member NOT in set case"
       [(is (= (wcar* (car/sismember k1 "reflexes")) 0))])

     (testing "Set union case"
       [(is (= (wcar* (car/sadd k2 "pecking")) 1))
        (is (= (wcar* (car/sadd k2 "flight")) 1))
        (is (= (set (wcar* (car/sunion k1 k2)))
               #{"flight" "pecking" "x-ray vision"}))])]))

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



(deftest sorted-set-tests
  (let [_       (clear-tkeys!)
        k1      (tkey "hackers")
        k2      (tkey "slackers")
        k1-U-k2 (tkey "hackersnslackers")
        k1-I-k2 (tkey "hackerslackers")]

    [(testing "Sorted Set case 1"
       [(wcar*
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

        (is (= (wcar* (car/zrange k1 2 4))
               ["Alan Kay" "Richard Stallman" "Yukihiro Matsumoto"]))])

     (testing "Sorted Set Union Case"
       [(is (= (wcar* (car/zrange k1-U-k2 2 5))
               ["Claude Shannon" "Alan Kay" "Richard Stallman" "Ferris Beuler"]))])

     (testing "Sorted Set Intersect Case"
       [(is (= (wcar* (car/zrange k1-I-k2 0 1))
               ["Emmanuel Goldstein" "Dade Murphy"]))])]))
(def
  k1
  (tkey "hackers"))
(def
  k2
  (tkey "slackers"))
(def
  k1-U-k2
  (tkey "hackersnslackers"))
(def
  k1-I-k2
  (tkey "hackerslackers"))

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
  ;(car/zinterstore* k1-I-k2 [k1 k2])
  )

(wcar* (car/zunionstore* k1-U-k2 [k1 k2]))
(wcar* (car/zinterstore* k1-I-k2 [k1 k2]))


(deftest getset-test-2
  (let [_  (clear-tkeys!)
        k1 (tkey "children")
        k2 (tkey "favorite:child")]

    [(testing "Case 1"
       [(wcar*
          (car/rpush k1 "A")
          (car/rpush k1 "B")
          (car/rpush k1 "C")
          (car/set k2 "B"))

        (is (= (wcar*
                 (car/get k2)
                 (car/get k2))
               ["B" "B"]))])

     (testing "Case 2"
       [(is (= (wcar*
                 (car/get k2)
                 (car/lrange k1 0 3)
                 (car/get k2))
               ["B" ["A" "B" "C"] "B"]))])]))

;;; Pub/sub

(deftest pubsub-single-channel-test
  (let [_         (clear-tkeys!)
        received_ (atom [])
        listener
                  (car/with-new-pubsub-listener
                    {} {"ps-foo" #(swap! received_ conj %)}
                    (car/subscribe "ps-foo"))]

    (sleep 200)
    [(is (= (wcar*
              (car/publish "ps-foo" "one")
              (car/publish "ps-foo" "two")
              (car/publish "ps-foo" "three"))
            [1 1 1]))

     (sleep 400)
     (do (car/close-listener listener) :close-listener)

     (is (= [["subscribe" "ps-foo" 1]
             ["message" "ps-foo" "one"]
             ["message" "ps-foo" "two"]
             ["message" "ps-foo" "three"]]
            @received_))]))

(let [_         (clear-tkeys!)
      received_ (atom [])
      listener
                (car/with-new-pubsub-listener
                  {} {"ps-foo" #(swap! received_ conj %)}
                  (car/subscribe "ps-foo"))]

  (sleep 200)
  [(is (= (wcar*
            (car/publish "ps-foo" "one")
            (car/publish "ps-foo" "two")
            (car/publish "ps-foo" "three"))
          [1 1 1]))

   (sleep 400)
   (do (car/close-listener listener) :close-listener)

   (is (= [["subscribe" "ps-foo" 1]
           ["message" "ps-foo" "one"]
           ["message" "ps-foo" "two"]
           ["message" "ps-foo" "three"]]
          @received_))])

(def received_ (atom []))

(deref received_)
(def listener (car/with-new-pubsub-listener
                {} {"ps-foo" #(println %)}
                (car/subscribe "ps-foo")))

(wcar*
  (car/publish "ps-foo" "uma")
  )


(deftest pubsub-multi-channels-test
  (let [_         (clear-tkeys!)
        received_ (atom [])
        listener
                  (car/with-new-pubsub-listener
                    {} {"ps-foo" #(swap! received_ conj %)
                        "ps-baz" #(swap! received_ conj %)}
                    (car/subscribe "ps-foo" "ps-baz"))]

    (sleep 200)
    [(is (= (wcar*
              (car/publish "ps-foo" "one")
              (car/publish "ps-bar" "two")
              (car/publish "ps-baz" "three"))
            [1 0 1]))

     (sleep 400)
     (do (car/close-listener listener) :close-listener)
     (is (= [["subscribe" "ps-foo" 1]
             ["subscribe" "ps-baz" 2]
             ["message" "ps-foo" "one"]
             ["message" "ps-baz" "three"]]
            @received_))]))

(deftest pubsub-unsubscribe-test
  (let [_         (clear-tkeys!)
        received_ (atom [])
        listener
                  (car/with-new-pubsub-listener {}
                                                {"ps-*"   #(swap! received_ conj %)
                                                 "ps-foo" #(swap! received_ conj %)})]

    (sleep 200)
    [(car/with-open-listener listener
                             (car/psubscribe "ps-*")
                             (car/subscribe "ps-foo"))

     (sleep 400)
     (is (= (wcar*
              (car/publish "ps-foo" "one")
              (car/publish "ps-bar" "two")
              (car/publish "ps-baz" "three"))
            [2 1 1]))

     (sleep 400)
     (car/with-open-listener listener
                             (car/unsubscribe "ps-foo"))

     (sleep 400)
     (is (= (wcar*
              (car/publish "ps-foo" "four")
              (car/publish "ps-baz" "five"))
            [1 1]))

     (sleep 400)
     (do (car/close-listener listener) :close-listener)

     (is (= [["psubscribe" "ps-*" 1]
             ["subscribe" "ps-foo" 2]
             ["message" "ps-foo" "one"]
             ["pmessage" "ps-*" "ps-foo" "one"]
             ["pmessage" "ps-*" "ps-bar" "two"]
             ["pmessage" "ps-*" "ps-baz" "three"]
             ["unsubscribe" "ps-foo" 1]
             ["pmessage" "ps-*" "ps-foo" "four"]
             ["pmessage" "ps-*" "ps-baz" "five"]]
            @received_))]))

(deftest pubsub-ping-and-errors-test
  (let [_               (clear-tkeys!)
        received_       (atom [])
        received-pong?_ (atom false)
        listener
                        (car/with-new-pubsub-listener
                          {:ping-ms 1000} ^:parse
                          (fn [msg _]
                            (let [{:keys [kind channel pattern payload raw]} msg]
                              (cond
                                ;; Precisely timing pong difficult in GitHub workflow
                                (= kind "pong") (reset! received-pong?_ true)
                                (= payload "throw") (throw (Exception.))
                                :else (swap! received_ conj msg))))

                          (car/psubscribe "*"))]

    (sleep 200)                                             ; < ping-ms
    [(is (= (wcar*
              (car/publish "a" "1")
              (car/publish "a" "2")
              (car/publish "b" "1")
              (car/publish "b" "throw")
              (car/publish "b" "2"))
            [1 1 1 1 1]))

     (sleep 1200)                                           ; > ping-ms

     (wcar* (car/publish "a" "3"))

     (sleep 400)
     (do (car/close-listener listener) :close-listener)

     (sleep 400)
     (let [received @received_
           clean-received
                    (mapv
                      (fn [{:keys [kind channel payload]}]
                        (cond
                          (= kind "pmessage") [kind (str channel "/" payload)]
                          (= channel "carmine:listener:error") [kind (:error payload)]
                          :else [kind channel]))
                      received)]

       (is (= clean-received
              [["psubscribe" "*"]
               ["pmessage" "a/1"]
               ["pmessage" "a/2"]
               ["pmessage" "b/1"]
               ["carmine" :handler-ex]
               ["pmessage" "b/2"]
               ["pmessage" "a/3"]
               ["carmine" :conn-closed]])))]))
