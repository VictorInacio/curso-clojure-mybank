(ns mybank-web-api.routing
  (:require [mybank-web-api.bank :as bank]
            [mybank-web-api.interceptors.html :as html]
            [mybank-web-api.interceptors.selector :as selector]
            [mybank-web-api.interceptors.terminate :as terminate]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as i]
            [io.pedestal.http.sse :as sse]
            [com.stuartsierra.component :as component]))


(def routes
  (route/expand-routes
    #{["/home" :get (i/interceptor {:name  :home
                                    :enter html/home-page}) :route-name :home]
      ["/par-ou-impar" :get [terminate/verifica-acesso terminate/para-tudo-quando selector/par-ou-impar]]
      ["/saldo/:id" :get [terminate/verifica-acesso
                          (i/interceptor {:name  :get-saldo
                                          :enter bank/get-saldo-interceptor})] :route-name :saldo]
      ["/deposito/:id" :post [terminate/verifica-acesso
                              (i/interceptor {:name  :make-deposit
                                              :enter bank/make-deposit-interceptor})] :route-name :deposito]}))



(defrecord Routes []
  component/Lifecycle
  (start [this]
    (assoc this :routes routes))
  (stop [this]
    (dissoc this :routes))
  )

(defn new-routes []
  (->Routes))