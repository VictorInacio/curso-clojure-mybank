(ns mybank-web-api.interceptors.terminate
  (:require [io.pedestal.interceptor :as interceptor]
            [io.pedestal.interceptor.chain :as interceptor.chain]))


;(def query-params
;  (interceptor/interceptor
;    {:name  ::query-params
;     :enter (fn [ctx]
;              (try
;                (update-in ctx [:request] parse-query-params)
;                (catch IllegalArgumentException iae
;                  (interceptor.chain/terminate
;                    (assoc ctx :response {:status 400
;                                          :body   (str "Bad Request - " (.getMessage iae))})))))}))

(def verifica-acesso
  (interceptor/interceptor
    {:name  ::verifica-acesso
     :enter (fn [context]
              (let [autorizado (= (-> context :request :query-params :username) "admin")]
                (if-not autorizado
                  (interceptor.chain/terminate
                    (assoc context :response {:status 403
                                              :body   "Acesso inválido."}))
                  context)))}))

(def para-tudo-quando
  (interceptor/interceptor
    {:name  ::verifica-acesso
     :enter (fn [context]
              (interceptor.chain/terminate-when context
                (fn [ctx]
                  (let [pt (ctx :paratudo!)]
                    (println pt)
                    pt))))}))