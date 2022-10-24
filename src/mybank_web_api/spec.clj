(ns mybank-web-api.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as gen]))

;; dados predicado ->
;; funs (:arg :ret :fn)


;(s/valid? string? "a")
;(s/valid? int? 1)
;(s/valid? number? 1)
;
;(s/valid? #(< % 0) 5)
;(s/valid? #(< % 0) 5)
;
;(s/valid? #{:do :re :mi} :fa)
;(s/valid? #{:do :re :mi} :do)
;
;(s/def ::positivo pos?)
;:positivo
;:mybank-web-api.contas/positivo
;(s/def ::pessoa keyword?)
;
;(s/def :idades (s/keys :req [:my.config/id :number/positivo]
;                       :opt [:my.config/port]))
;
;
;(s/def ::id-conta keyword?)
;(s/def ::saldo pos?)
;(s/def ::conta (s/keys :req [::id-conta]))
;(s/def ::contas (s/coll-of ::conta))
;
;(defn get-saldo
;  [id-conta contas]
;  (get contas id-conta))

(s/def ::pessoa keyword?)

(s/valid? ::pessoa :usuario)

(s/exercise ::pessoa 4)

(def gen-pessoa (s/gen ::pessoa))
(gen/generate (s/gen ::pessoa))
(gen/generate gen-pessoa)


(defn grande? [n]
  (> n 100))

(defn pequeno? [n]
  (< n 100))

(gen/generate (s/gen (s/and int? even? pos? pequeno?)))


(defn divisible-by [n] #(zero? (mod % n)))

(gen/sample (s/gen (s/and int?
                          #(> % 0)
                          (divisible-by 3))))

(s/def :bowling/roll (s/int-in 0 11))
;
;
;(def warehouses {:shelf/sp 10
;                 :shelf/rj 12
;                 :shelf/pr 6})
;
;(doall
;  (map (fn [[k v]]
;         (println k v)
;         (s/def k (s/int-in 0 (- v 1)))) warehouses))
;
;(s/def :shelf/num (s/int-in 0 9))
;
;(s/valid? :shelf/sp 11)
;(gen/sample (s/gen :shelf/sp))

(gen/sample (s/gen :bowling/roll))
(gen/generate (s/gen :bowling/roll))
;(gen/sample (s/gen int?))



(s/def ::username string?)
(s/def ::password string?)

(s/def ::last-login (s/and int? pos?))
(s/def ::last-login (s/or ::inteiro int?
                          ::texto string?))
(s/valid? ::last-login 1)

(s/def ::comment string?)

(s/def ::user
  (s/keys
    :req [::username ::password]
    :opt [::comment ::last-login]))


(s/def ::map #(contains? % :a))

(s/explain ::map {:a 1})
(s/explain ::map {:b 1})


(s/exercise ::user)

(s/valid?
  ::user
  {::username   "victor"
   ::password   "1234"
   ::comment    "usuario"
   ::last-login 11000})

(s/valid?
  ::user
  {::username   "victor"
   ::password   "1234"
   ::comment    "usuario"
   ::last-login "11000"})

(s/conform
  ::user
  {::username   "victor"
   ::password   "1234"
   ::comment    "usuario"
   ::last-login "11000"})

(s/conform
  ::user
  {::username   "victor"
   ::password   "1234"
   ::comment    "usuario"
   ::last-login 11000})

(s/valid?
  ::user
  {::username   "victor"
   ::password   "1234"
   ::comment    1
   ::last-login 11000})

(s/explain-data
  ::user
  {::username   "victor"
   ::password   "1234"
   ::comment    1
   ::last-login 11000})


(s/valid?
  ::user
  {::usernamee  "victor"
   ::password   "1234"
   ::comment    "usuario"
   ::last-login 11000})

(s/explain ::user
           {::usernamee  "victor"
            ::password   "1234"
            ::comment    "usuario"
            ::last-login 11000})

(s/explain-data ::user
                {::usernamee  "victor"
                 ::password   "1234"
                 ::comment    "usuario"
                 ::last-login 11000})


(gen/generate (s/gen ::user))

(s/explain
  ::user
  {::username "victor"
   ::comment  "usuario"})


(s/explain-data
  ::user
  {::username      "rich"
   ::password      "1234"
   ::comment       "this is a user"
   ::eunaosoudaqui 123})


;;;;;;;;;

(s/def ::function (s/cat :defn #{'defn}
                         :name symbol?
                         :doc (s/? string?)
                         :args vector?
                         :body (s/+ list?)))

(defn soma "soma" [a b]
  (+ a b)
  )
(s/valid? ::function '(defn soma "soma" [a b]
                      (+ a b)
                      ))

(s/def ::inteiros (s/coll-of integer?))

(s/valid? ::inteiros [1 2 3.1])
(s/explain-data ::inteiros [1 2 3.1])

;;;;;;;;;
;; fdef
(defn ranged-rand
  "Returns random int in range start <= rand < end"
  [start end]
  (+ start (long (rand (- end start)))))

(s/fdef ranged-rand
        :args (s/and (s/cat :start int? :end int?)
                     #(< (:start %) (:end %)))
        :ret int?
        :fn (s/and #(>= (:ret %) (-> % :args :start))
                   #(< (:ret %) (-> % :args :end))))

(s/exercise-fn `ranged-rand 40)

(stest/instrument `ranged-rand)

(ranged-rand  5 3)





(def suit? #{:club :diamond :heart :spade})
(def rank? (into #{:jack :queen :king :ace} (range 2 11)))
(def deck (for [suit suit? rank rank?] [rank suit]))

(s/def :game/card (s/tuple rank? suit?))

(s/explain-data :game/card [2 :heart])

(s/def :game/hand (s/* :game/card))

(s/explain-data :game/hand [[2 :heart]
                            [2 :heart]
                            [2 :heart]
                            [2 :heart]])

(s/def :game/name string?)
(s/def :game/score int?)
(s/def :game/player (s/keys :req [:game/name :game/score :game/hand]))

(s/def :game/players (s/* :game/player))
(s/def :game/deck (s/* :game/card))
(s/def :game/game (s/keys :req [:game/players :game/deck]))



(def kenny
  {:game/name  "Kenny Rogers"
   :game/score 100
   :game/hand  []})
(s/valid? :game/player kenny)


(s/explain :game/game
           {:game/deck    deck
            :game/players [{:game/name  "Kenny Rogers"
                            :game/score 100
                            :game/hand  [[2 :banana]]}]})

(gen/generate (s/gen :game/game))