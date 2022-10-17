(ns mybank-web-api.core-test
  (:require [clojure.test :refer :all]
            [mybank-web-api.core :refer :all]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test-http]
            [io.pedestal.interceptor :as i]
            [io.pedestal.interceptor.chain :as chain]
            [clojure.pprint :as pp]))

;1 - Executar os testes em um terminal.
;
;2 - Adicionar casos de testes para saque. (Não pode ter saldo negativo)
;
;3 - Implementar pelo menos 2 melhorias nos endpoints a partir de casos de teste imaginados que falham.

(defn test-post [server verb url body]
  (test-http/response-for (::http/service-fn @server) verb url :body body))

(defn test-request [server verb url]
  (test-http/response-for (::http/service-fn @server) verb url))


(def server ())
(test-post server :post "/deposito/1" "199.93")



