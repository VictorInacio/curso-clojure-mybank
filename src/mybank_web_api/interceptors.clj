(ns mybank-web-api.interceptors
  (:require [mybank-web-api.db :as db]
            [io.pedestal.interceptor :as i]
            [clojure.data.json :as json]
            [clojure.pprint :as pp]))

;; Cria funções p/ facilitar o retorno
(defn response
  "Create a map representing the response of a HTTP Request, using `status`, `body` and the rest as headers."
  [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok       (partial response 200))
(def created  (partial response 201))
(def accepted (partial response 202))
(def error    (partial response 500))
(def bad-request (partial response 400))

;; 1. Debugging Interceptors
(def echo
  {:name ::echo
   :enter #(assoc % :response (ok (:request %)))})

(defn print-n-continue
  "Logs the context and return the same."
  [context]
  (println "LOG: " context)
  context)

(def print-n-continue-int
  (i/interceptor {:name :print-n-continue
                  :enter print-n-continue
                  :leave print-n-continue}))

(defn hello
  "Função de roteamento que retorna uma mensagem HTTP de sucesso com `Hello!` no corpo."
  [request]
  (ok "Hello!"))

(def hello-interceptor
  (i/interceptor {:name ::hello
                  :enter (fn [ctx] (->> ctx
                                        hello
                                        (assoc ctx :response)))}))

;; business interceptors
(defn carrega-contas
  "Função de roteamento que carrega as contas ao contexto."
  [context]
  (println "Carregando contas ao contexto!!")
  (assoc context :contas db/contas-))

(def carrega-contas-interceptor
  (i/interceptor {:name  :contas-interceptor
                  :enter carrega-contas}))

(defn conta-existe?
  [context]
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (:contas context)]
    (not (nil? (get @contas id-conta)))))

(def conta-existe-interceptor
  (i/interceptor
    {:name  :conta-existe-interceptor
     :enter (fn [context]
              (if (conta-existe? context)
                context
                (assoc context :response (bad-request "conta nao existe!" "Content-Type" "text/plain"))))}))

(defn lista-contas
  "Recupera contas (`:contas`) e associa a um :response"
  [context]
  (as-> (:contas context) contas
    (response (if (nil? @contas) 500 200) @contas)
    (assoc context :response contas)))

(def lista-contas-interceptor
  (i/interceptor {:name  :contas-interceptor
                  :enter lista-contas}))

(defn get-saldo
  "Recupera saldo das contas disponíveis em :contas de `request`.
  Retorna uma response com :status :headers e :body."
  [contexto]
  (let [id-conta (keyword (get-in contexto [:request :path-params :id] nil))]
    (as-> (:contas contexto) contas
      (response (if (nil? @contas) 500 200)
                (id-conta @contas "conta inválida!"))
      (assoc contexto :response contas))))

(def get-saldo-interceptor
  (i/interceptor {:name  :get-saldo
                  :enter get-saldo}))

(defn make-deposit [contexto]
  (let [contas (:contas contexto)
        id-conta (-> contexto :request :path-params :id keyword)
        valor-deposito (-> contexto :request :body slurp parse-double)
        SIDE-EFFECT! (swap! contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito))))]
    (merge contexto {:response
                     (ok {:id-conta   id-conta
                          :novo-saldo (id-conta @contas)})})))

(def make-deposit-interceptor
  (i/interceptor {:name :make-deposit
                  :enter make-deposit}))

;; 3. transorma resposta para o tipo pedido
(defn accepted-type
  "Retorna accept-type da requisição."
  [context]
  (get-in context [:request :accept :field] "text/plain"))

(defn transform-content
  "Transforma conteúdo de `body` para o tipo `content-type`."
  [body content-type]
  (case content-type
    "text/html"        body
    "text/plain"       body
    "application/edn"  (pr-str body)
    "application/json" (json/write-str body)))

(defn coerce-to
  "Atualiza `body` para que esteja no formato adequado ao `Content-Type`."
  [response content-type]
  (-> response
      (update :body transform-content content-type)
      (assoc-in [:headers "Content-Type"] content-type)))

(def coerce-body
  "Interceptor p/ forçar `body` para o tipo escolhido em `Content-Type`."
  (i/interceptor {:name ::coerce-body
                  :leave
                  (fn [context]
                    (println "Forçando body a se adaptar ao Content-Type..." context)
                    (if (get-in
                         context
                         [:response :headers "Content-Type"])
                      context
                      (update-in context [:response]
                       coerce-to (accepted-type context))))}))


(comment
  (macroexpand-1 `(as-> (:contas context) contas
                    {:status (if (nil? contas) 500 200)
                     :headers {"Content-Type" "text/plain"}
                     :body contas}))
  (get-saldo {:contas @db/contas- :path-params {:id "1"}})
  ((:enter recupera-contas) {:contas db/contas- :path-params {:id "1"}})

  ;; 3.
  (require '[io.pedestal.interceptor.chain :as chain])
  (chain/execute {:request {:accept {:field "application/json"}}}
                 [coerce-body
                  print-n-continue-int
                  carrega-contas-interceptor
                  lista-contas-interceptor])

  )
