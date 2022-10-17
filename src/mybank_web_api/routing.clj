(ns mybank-web-api.routing
  (:require [mybank-web-api.bank :as bank]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as i]))


(def routes
  (route/expand-routes
    #{["/saldo/:id" :get (i/interceptor {:name  :get-saldo
                                         :enter bank/get-saldo}) :route-name :saldo]
      ["/deposito/:id" :post (i/interceptor {:name  :make-deposit
                                             :enter bank/make-deposit}) :route-name :deposito]}))