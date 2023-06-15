(ns mybank-web-api.clojure-language.aula_7_exerc_resolv)

;; Create a for loop which iterates through two vectors
;; [:top :middle :bottom] and [:left :middle :right]
;; returning the carthesian product of them,

(= [[:top    :left] [:top    :middle] [:top    :right]
    [:middle :left] [:middle :middle] [:middle :right]
    [:bottom :left] [:bottom :middle] [:bottom :right]]
   (for [row    [:top :middle :bottom]
         column [:left :middle :right]]
     [row column]))

(mapv (fn [row column]
        [row column])
      [:top :middle :bottom]
      [:left :middle :right])

;; Generate a list of numbers that are only odd numbers squared
;; from number X to number Y by using
;; higher order functions map and filter (and no for loops)

;; "Combinations of these transformations is trivial"
(= '(1 9 25 49 81)
   (map (fn [x] (* x x))
        (filter odd? (range 10)))
   (for [x (range 10) :when (odd? x)]
     (* x x)))

