(ns mybank-web-api.bank
  (:require [schema.core :as s]))


(s/defschema IdConta s/Keyword)
(s/defschema Contas {s/Keyword {:saldo Number}})
(s/defschema SaldoResult (s/maybe {:saldo Number}))


(s/defn ^:always-validate get-saldo :- SaldoResult
  [id-conta :- IdConta
   contas :- Contas]
  (get contas id-conta))


(s/defschema Context {s/Any s/Any})

(s/defschema Response {s/Any s/Any
                       :response {:body s/Any
                                  :status s/Int
                                  s/Any s/Any}})

(s/defn ^:always-validate get-saldo-interceptor :- Response
  [context :- Context]
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (-> context :contas)
        saldo (get-saldo id-conta @contas)]
    (assoc context :response {:status  200
                              :headers {"Content-Type" "text/plain"}
                              :body    saldo})))

(defn make-deposit! [id-conta contas valor-deposito]
  (swap! contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito)))))

(defn make-deposit-interceptor [context]
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (-> context :contas)
        valor-deposito (-> context :request :body slurp parse-double)
        _ (make-deposit! id-conta contas valor-deposito)
        novo-saldo (id-conta @contas)
        ]
    (assoc context :response {:status  200
                              :headers {"Content-Type" "text/plain"}
                              :body    {:id-conta   id-conta
                                        :novo-saldo novo-saldo}})))

(comment
  (def context {:request {:body        "100.00"
                          :path-params {:id 1}}
                :contas  (atom {:1 {:saldo 10}})})
  (make-deposit-interceptor context)

  )