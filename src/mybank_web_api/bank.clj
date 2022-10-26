(ns mybank-web-api.bank
  (:require
    [com.stuartsierra.component :as component]
    [schema.core :as s :include-macros true]
    [mybank-web-api.schema :as schema]))

;; CRIA CONTA



;; SALDO
(s/defn ^:always-validate get-saldo :- schema/SaldoResult
  [id-conta :- schema/IdConta
   contas :- schema/Contas]
  (get contas id-conta))

(s/defn ^:always-validate get-saldo-interceptor :- schema/Response
  [context :- schema/Context]
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (-> context :contas)
        saldo (get-saldo id-conta @contas)]
    (assoc context :response {:status  200
                              :headers {"Content-Type" "text/plain"}
                              :body    saldo})))


(defrecord Bank [config database]
  component/Lifecycle

  (start [this]
    (let [_ (println config)
          arquivo (-> (-> config :config :db-file )
                      slurp
                      read-string)]
      (assoc this :contas (atom arquivo))))


  (stop [this]
    (println "Limpar as contas.edn da memória e Salvar em disco para uso futuro.")
    (assoc this :store nil)))



;; DEPOSITO
(s/defn ^:always-validate make-deposit! :- schema/Contas
  [id-conta :- schema/IdConta
   contas :- schema/ContasAtom
   valor-deposito :- schema/ValorDeposito]
  (swap! contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito)))))

(defn make-deposit-interceptor [context]
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (-> context :contas)
        valor-deposito (-> context :request :body slurp parse-double)
        _ (make-deposit! id-conta contas valor-deposito)
        novo-saldo (id-conta @contas)]
    (assoc context :response {:status  200
                              :headers {"Content-Type" "text/plain"}
                              :body    {:id-conta   id-conta
                                        :novo-saldo novo-saldo}})))