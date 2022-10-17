(ns mybank-web-api.bank)



(defn get-saldo [context]
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (-> context :contas)]
    (assoc context :response {:status  200
                              :headers {"Content-Type" "text/plain"}
                              :body    (id-conta @contas "conta inválida!")})))

(defn make-deposit [context]
  (let [id-conta (-> context :request :path-params :id keyword)
        contas (-> context :contas)
        valor-deposito (-> context :request :body slurp parse-double)
        SIDE-EFFECT! (swap! contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito))))]
    (println "DEPOSITO")
    (assoc context :response {:status  200
                           :headers {"Content-Type" "text/plain"}
                           :body    {:id-conta   id-conta
                                     :novo-saldo (id-conta @contas)}})))