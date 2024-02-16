(ns mybank-web-api.clojure.guide.syntax)


42        ; integer
-1.5      ; floating point
22/7      ; ratio

"hello"         ; string
\e              ; character
#"[0-9]+"       ; regular expression


map             ; symbol
+               ; symbol - most punctuation allowed
clojure.core/+  ; namespaced symbol
nil             ; null value
true false      ; booleans
:alpha          ; keyword
:release/alpha  ; keyword with namespace

'(1 2 3)     ; list
[1 2 3]      ; vector
#{1 2 3}     ; set
{:a 1, :b 2} ; map

(require '[clojure.repl :refer :all])

(doc +)


(apply f '(1 2 3 4))    ;; same as  (f 1 2 3 4)
(apply f 1 '(2 3 4))    ;; same as  (f 1 2 3 4)
(apply f 1 2 '(3 4))    ;; same as  (f 1 2 3 4)
(apply f 1 2 3 '(4))    ;; same as  (f 1 2 3 4)

(defn messenger-builder [greeting]
  (fn [who] (println greeting who))) ; closes over greeting

;; greeting provided here, then goes out of scope
(def hello-er (messenger-builder "Hello"))

;; greeting value still available because hello-er is a closure
(hello-er "world!")
;; Hello world!


;; Sequential Collections

;; vectors
[1 2 3]

(get ["abc" false 99] 0)

(get ["abc" false 99] 0)

(vector 1 2 3)

(conj [1 2 3] 4 5 6)

(def v [1 2 3])

(conj v 4 5 6)


;; lists
(def cards '(10 :ace :jack 9))

(first cards)

(rest cards)

(conj cards :queen)

(def stack '(:a :b))

(peek stack)

(pop stack)



;; Hashed collections

;; set
(def players #{"Alice", "Bob", "Kelly"})

(conj players "Fred")

(contains? players "Kelly")

(conj (sorted-set) "Bravo" "Charlie" "Sigma" "Alpha")

(def players #{"Alice" "Bob" "Kelly"})

(def new-players ["Tim" "Sue" "Greg"])

(into players new-players)

;; Maps

(def scores {"Fred"  1400
             "Bob"   1240
             "Angela" 1024})

(def scores {"Fred" 1400, "Bob" 1240, "Angela" 1024})

(assoc scores "Sally" 0)

(assoc scores "Bob" 0)

(dissoc scores "Bob")

(get scores "Angela")

(def directions {:north 0
                 :east 1
                 :south 2
                 :west 3})

(directions :north)

(def bad-lookup-map nil)

(bad-lookup-map :foo)

(contains? scores "Fred")

(find scores "Fred")

(keys scores)

(vals scores)

(def players #{"Alice" "Bob" "Kelly"})

(zipmap players (repeat 0))

(into {} (map (fn [player] [player 0]) players))

(reduce (fn [m player]
          (assoc m player 0))
        {} ; initial value
        players)

(def new-scores {"Angela" 300 "Jeff" 900})

(merge scores new-scores)

(def sm (sorted-map
          "Bravo" 204
          "Alfa" 35
          "Sigma" 99
          "Charlie" 100))

(def person
  {:first-name "Kelly"
   :last-name "Keen"
   :age 32
   :occupation "Programmer"})

;; Iterations

(dotimes [i 3]
  (println i))

(doseq [n (range 3)]
  (println n))

(doseq [letter [:a :b]
        number (range 3)] ; list of 0, 1, 2
  (prn [letter number]))

(for [letter [:a :b]
      number (range 3)] ; list of 0, 1, 2
  [letter number])

(loop []
  (println "Clock: " (new java.util.Date))
  (Thread/sleep 1000)
  (recur))



