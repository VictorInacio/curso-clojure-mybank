(ns math
  (:use [incanter.core :only [view xy-plot]])
  (:require [incanter.charts :refer :all]))

(defn plot-equation []
  (let [theta-values (range 0.0 (* 2 Math/PI) 0.01)         ;; Generate theta values
        calculate-z  (fn [theta]
                       (->> theta
                            (complex-math/exp)
                            (complex-math/add (complex-math/exp (* Math/PI theta)))))
        z-values     (map calculate-z theta-values)         ;; Compute z for each theta
        real-parts   (map #(.re %) z-values)                ;; Extract real parts
        imag-parts   (map #(.im %) z-values)]               ;; Extract imaginary parts

    (view (xy-plot (into [] real-parts) (into [] imag-parts)))))