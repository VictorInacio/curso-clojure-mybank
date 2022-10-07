(ns mybank-web-api.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test-http]
            [clojure.pprint :as pp])
  (:gen-class))

(def contas (atom {:1 {:saldo 100}
                   :2 {:saldo 200}
                   :3 {:saldo 300}}))

(defn get-saldo [request]
  (let [id-conta (-> request :path-params :id keyword)]
    {:status 200 :body {:saldo (id-conta @contas "conta inválida!")}}))

(defn make-deposit [request]
  (let [id-conta (-> request :path-params :id keyword)
        valor-deposito (-> request :body slurp parse-double)
        _ (swap! contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito))))]
    {:status 200 :body {:id-conta id-conta
                        :novo-saldo (id-conta @contas)}}))

(def routes
  (route/expand-routes
    #{["/saldo/:id" :get get-saldo :route-name :saldo]
      ["/deposito/:id" :post make-deposit :route-name :deposito]}))

(defn create-server []
  (http/create-server
    {::http/routes routes
     ::http/type   :jetty
     ::http/port   8890
     ::http/join?  false}))
(defn start []
  (reset! server (http/start (create-server))))

(defn test-request [server verb url]
  (test-http/response-for (::http/service-fn @server) verb url))
(defn test-post [server verb url options]
  (test-http/response-for (::http/service-fn @server) verb url options))
(comment
  (start)

  (test-request server :post "/saldo/1")
  (test-request server :get "/saldo/2")
  (test-request server :get "/saldo/3")
  (test-request server :get "/saldo/4")

  (test-post server :post "/deposito/2" :body "100.99")
)
