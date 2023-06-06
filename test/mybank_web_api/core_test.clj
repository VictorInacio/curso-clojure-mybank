(ns mybank-web-api.core-test
  (:require [clojure.test :refer :all]
            [mybank-web-api.core :refer :all]))


(deftest a-test
  (testing "FIXED, I will not fail."
    (is (fn? (get-in [:a :b] (s/check
                               Data
                               {:a {:b ["abc"]
                                    :c 123}
                                :d [{:e :bc
                                     :f [12.2 13 100]}
                                    {:e :bc
                                     :f [-1]}]}))))))


(deftest api-test
  (testing "Verificar ."
    (is (= (do (start) (test-request server :get "/greet"))
           {:status  200,
            :body    "Olá Pedestal!",
            :headers {"Strict-Transport-Security"         "max-age=31536000; includeSubdomains",
                      "X-Frame-Options"                   "DENY",
                      "X-Content-Type-Options"            "nosniff",
                      "X-XSS-Protection"                  "1; mode=block",
                      "X-Download-Options"                "noopen",
                      "X-Permitted-Cross-Domain-Policies" "none",
                      "Content-Security-Policy"           "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;",
                      "Content-Type"                      "text/plain"}}

           ))))

(run-all-tests)

