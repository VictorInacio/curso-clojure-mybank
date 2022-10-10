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

(deftest a-test
  (testing "FAIL, I will not fail."
    (is (= 1 1))))


(defn test-post [server verb url body]
  (test-http/response-for (::http/service-fn @server) verb url :body body))

(defn test-request [server verb url]
  (test-http/response-for (::http/service-fn @server) verb url))

(deftest ^:basic obviedades
    (let [a 1]
      (testing "Verificando valores estáticos!"
        ;;Numeros simples
        (is (= 4 (+ 2 2)))

        (testing "Testes Tipos"
          (is (instance? Long 256))
          (is (.startsWith "abcde" "ab")))

        ;; Chamadas de função

        (is (= 0 (quadrado 0)))
        (is (= 1 (quadrado 1)))
        (is (= 1 (quadrado -1)))
        (is (= 16 (quadrado 4))))))

(deftest api-test
    (testing "Verificar ."
      (is (= (let [_ (start)
                   resp (test-request server :get "/hello")
                   _ (http/stop @server)]
               resp)
             {:status  200,
              :body    "Hello!",
              :headers {"Strict-Transport-Security"         "max-age=31536000; includeSubdomains",
                        "X-Frame-Options"                   "DENY",
                        "X-Content-Type-Options"            "nosniff",
                        "X-XSS-Protection"                  "1; mode=block",
                        "X-Download-Options"                "noopen",
                        "X-Permitted-Cross-Domain-Policies" "none",
                        "Content-Security-Policy"           "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;",
                        "Content-Type"                      "text/plain"}}))))
(deftest api-test2
    (testing "Verificar ."
      (is (= (let [_ (start)
                   resp (test-request server :get "/hello")
                   _ (http/stop @server)]
               (:body resp))
             "Hello!"))))
#_
(deftest enter-wig
    (let [test-fn (:enter widget-finder)]
      (is (= {:id 1 :title "foobar"} (:widget (test-fn {:title "foobar"})))))
    (testing "Interceptor Chains"
      (is (= {:id 1 :title "foobar"} (:widget (chain/execute {:title "foobar"} [widget-finder]))))
      (is (= "Widget ID 1, Title 'foobar'"
             (get-in (chain/execute {:title "foobar"} [widget-renderer widget-finder])
                     [:response :body])))))


(deftest requests
  (testing "Chamando request e testando o valor de body retornado"
    (let [_ (start)
          resp (test-request server :get "/saldo/1")
          _ (http/stop @server)]
      (is (= "{:saldo 100}" (:body resp))))))

(deftest are-req
  (testing "Multiple get resp"
    (are [a b] (= (-> (test-request server :get a)
                      :body) b)
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

  ;(cljs.core.match)

  (test-request server :get "/saldo/1")
  (test-request server :get "/saldo/2")
  (test-request server :get "/saldo/3")
  (test-request server :get "/saldo/4")
  (test-post server :post "/deposito/1" "199.93")
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