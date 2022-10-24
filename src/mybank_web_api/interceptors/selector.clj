(ns mybank-web-api.interceptors.selector
  (:require [io.pedestal.interceptor :as i]
            [io.pedestal.interceptor.chain :as chain]))

;; session Seletor

(def impar
  (i/interceptor
    {:name  ::odds
     :enter (fn [context]
              (assoc context :response {:body   "Eu recebo números impares\n"
                                        :status 200}))}))

(def par
  (i/interceptor
    {:name  ::evens
     :enter (fn [context]
              (assoc context :response {:body   "Eu recebo números pares\n"
                                        :status 200}))}))
(def dbg (atom {}))
(comment
  (-> @dbg
      (get-in [:ctx-before])
      :io.pedestal.interceptor.chain/stack
      ;:io.pedestal.interceptor.chain/queue
      ;keys
      ;clojure.pprint/pprint
      )

  (-> @dbg
      (get-in [:ctx-before])
      ::chain/queue
      clojure.pprint/pprint
      )

  (-> @dbg
      (get-in [:ctx-before])
      :route
      )


  (-> @dbg
      (get-in [:ctx-before])
      ::chain/stack
      )

  (-> @dbg
      (get-in [:ctx-after])
      ::chain/queue
      clojure.pprint/pprint
      ))



(def par-ou-impar
  {:name  ::par-ou-impar
   :enter (fn [context]
            (try
              (let [term (get-in context [:request :query-params :term])
                    _ (println term)
                    context (if term (assoc context :paratudo! :paratudo-valor!) context)
                    param (get-in context [:request :query-params :n])
                    n (Integer/parseInt param)
                    nxt (if (even? n) par impar)
                    ctx-after (chain/enqueue context [nxt])
                     ]
                (swap! dbg #(assoc % :ctx-before context))
                (swap! dbg #(assoc % :ctx-after ctx-after))
                ctx-after)
              (catch NumberFormatException e
                (assoc context :response {:body   "Número inválido\n"
                                          :status 400}))))})