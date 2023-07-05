(ns mybank-web-api.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test-http]
            [mybank-web-api.devops.memoize-redis :as memo]
            [taoensso.carmine :as car])
  (:gen-class))

(def tkey (partial car/key :mybank :rest-api))


(defonce contas (atom {:1 {:saldo 100}
                       :2 {:saldo 200}
                       :3 {:saldo 300}}))

(defn -saldo-by-id [id-conta]
  (println "saldo-by-id_: id-conta -> " id-conta)
  (-> @contas
      (get id-conta "conta inválida!")
      :saldo))

(def saldo-by-id
  (memo/memoize -saldo-by-id :key (tkey "saldo-by-id") :expire 60))

(defn get-saldo [request]
  (let [id-conta (-> request :path-params :id keyword)
        saldo    (saldo-by-id id-conta)]
    (println "saldo ->" saldo)
    {:status 200
     :body   {:result saldo}}))



#_(defn get-saldo [request]
    (let [id-conta (-> request :path-params :id keyword)]
      {:status 200
       :body   {:result (id-conta @contas "conta inválida!")}}))

(defn make-deposit [request]
  (let [id-conta       (-> request :path-params :id keyword)
        valor-deposito (-> request :body slurp parse-double)
        _              (swap! contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito))))
        memo-key (str (tkey "saldo-by-id") ":(" id-conta ")")
        del-return (memo/wcar* (car/del memo-key))]
    (println "DEBUG memo-key" memo-key del-return)
    ;; Cache invalidation
    {:status 200 :body {:id-conta   id-conta
                        :novo-saldo (id-conta @contas)}}))

(defn get-time-delay [_]
  (let [now        (System/currentTimeMillis)
        spend-time (fn []
                     (Thread/sleep 3000)
                     now)
        f          (future (spend-time))]
    {:status 200 :body {:result @f}}))

(get-time-delay {})

(def routes
  (route/expand-routes
    #{["/saldo/:id" :get get-saldo :route-name :saldo]
      ["/deposito/:id" :post make-deposit :route-name :deposito]
      ["/get-time-delay" :get get-time-delay :route-name :get-time-delay]}))

(defn create-server []
  (http/create-server
    {::http/routes routes
     ::http/type   :jetty
     ::http/port   8890
     ::http/join?  false}))

(defonce server (atom nil))

(defn start []
  (reset! server (http/start (create-server))))

(defn test-request [server verb url]
  (test-http/response-for (::http/service-fn @server) verb url))
(defn test-post [server verb url body]
  (test-http/response-for (::http/service-fn @server) verb url :body body))
(comment
  (start)
  (http/stop @server)

  (test-request server :get "/saldo/1")
  (test-request server :get "/saldo/2")
  (test-request server :get "/saldo/3")
  (test-request server :get "/saldo/4")
  (test-request server :get "/get-time-delay")

  (test-post server :post "/deposito/1" "59")
  (test-post server :post "/deposito/1" "47")
  )
