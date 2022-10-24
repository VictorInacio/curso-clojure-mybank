(ns mybank-web-api.schemas.schemas
  (:require [schema.core :as s]))



(s/defschema IdConta s/Keyword)
(s/defschema Contas {s/Keyword {:saldo Number}})
(s/defschema SaldoResult (s/maybe {:saldo Number}))


(s/defschema Context {s/Any s/Any})


(s/defschema DepositoResult {:id-conta s/Keyword
                             :novo-saldo s/Num})

(s/defschema ValorDeposito (s/pred number?))
(s/defschema ContasAtom (s/pred #(instance? clojure.lang.Atom %)))


(s/defschema Response {s/Any     s/Any
                       :response {:body   s/Any
                                  :status s/Int
                                  s/Any   s/Any}})