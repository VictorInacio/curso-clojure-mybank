(ns mybank-web-api.clojure-language.aula_7_exerc)

;; Generate a list of numbers that are only odd numbers squared
;; from number X to number Y by using
;; higher order functions map and filter (and no for loops)
(= '(1 9 25 49 81)
   (for [x (range 10) :when (odd? x)]
     (* x x)))



;; Create a for loop which iterates through two vectors
;; [:top :middle :bottom] and [:left :middle :right]
;; returning the carthesian product of them,

(= [[:top    :left] [:top    :middle] [:top    :right]
    [:middle :left] [:middle :middle] [:middle :right]
    [:bottom :left] [:bottom :middle] [:bottom :right]]
   ,,,)