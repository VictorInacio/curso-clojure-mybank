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

(defn test-json-post [server verb url body]
  (test-http/response-for (::http/service-fn @server)
                          verb url
                          :headers {:Content-Type "application/json"} :body body))

(deftest are-req
  (testing "Multiple get resp"
    (are [a b] (= (-> (test-request server :get a)
                      :body) b)
      "/hello" "Hello!"
      "/contas" "(:1 :2 :3)"
      "/saldo/1" "{:saldo 100}"
      "/saldo/2" "{:saldo 200}"
      "/saldo/3" "{:saldo 300}")))


(comment
  (def resp (test-request server :get "/saldo/1"))
  (:body resp)
  (run-tests)
  (macroexpand '(are [x y] (= x y)
                           2 (+ 1 1)
                           4 (* 2 2)))

  (macroexpand '(are [a b c] (= (+ a b) c)
                           1 2 3
                           6 6 12  ))

  (macroexpand '(are [a b c] (= (* a b) c)
                             1 2 2
                             6 6 36  ))

  (macroexpand '(are [a b] (= (-> (test-request server :get a)
                                  :body) c)
                           "/saldo/1" "{:saldo 100}"))

  (test-request server :get "/saldo/1")
  (run-tests)
  (start)
  (http/stop @server)
  (reset-server!)

  ;(cljs.core.match)

  ;; 1. testes basicos
  (test-request server :get "/hello")
  (test-request server :get "/hellov2")
  (test-request server :get "/hello/eric")
  (test-request server :get "/echo")

  ;; 2. testes com parametros e body-params
  (test-request server :get "/body-params")
  (test-request server :get "/contas")
  (test-request server :get "/pega-tudo/por/exemplo")
  (test-request server :get "/pega-tudo/por/exemplo?foo=bar&foo=foobar")
  ;; curl -i -H "Content-Type: application/json" --data '{"name":"bob"}' http://localhost:9999/body-params

  ;; 3.coerce body response
  ;; curl -i -H "Accept: application/json" http://localhost:9999/contas
  ;; curl -i -H "Accept: application/edn" http://localhost:9999/contas
  ;; curl -i -H "Accept: text/html" http://localhost:9999/contas
  (test-request server :get "/constraints/1")
  (test-request server :get "/constraints/bla")

  ;; business
  (test-request server :get "/saldo/1")
  (test-request server :get "/saldo/2")
  (test-request server :get "/saldo/3")
  (test-request server :get "/saldo/4")
  (test-post server :post "/deposito/1" "199.93")
  (test-post server :post "/deposito/2" "1.00")
  (test-post server :post "/deposito/4" "325.99")

  ;curl http://localhost:9999/saldo/1
  ;curl -d "199.99" -X POST http://localhost:9999/deposito/1

  (chain/execute {:title "Titulo"} [contas-interceptorwidget-finder])
  ;(http/default-interceptors [])
  (chain/execute {:title "Titulo"} [(i/interceptor contas-interceptor) widget-finder widget-renderer])
  (chain/execute {:title "Titulo"} [widget-renderer])
  {:title    "Titulo",
   :widget   {:id 1, :title "Titulo"},
   :response {:status 200, :body "Widget ID 1, Title 'Titulo'"}}
  {:title    "Titulo",
   :response {:status 404, :body "Not Found"}}
  )
