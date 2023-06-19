(ns mybank-web-api.core_middleware
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test-http]
            [io.pedestal.interceptor :as i]
            [io.pedestal.interceptor.chain :as chain])
  (:gen-class))

(def impar
  (i/interceptor
    {:name  ::odds
     :enter (fn [context]
              (assoc context :response {:body   "Eu recebo números impares\n"
                                        :status 200}))}))

(def par
  (i/interceptor
    {:name  ::evens
     :enter (fn [context]
              (assoc context :response {:body   "Eu recebo números pares\n"
                                        :status 200}))}))

(def par-ou-impar
  {:name  ::par-ou-impar
   :enter (fn [context]
            (try
              (let [param (get-in context [:request :query-params :n])
                    n     (Integer/parseInt param)
                    nxt   (if (even? n) par impar)]
                (chain/enqueue context [nxt]))
              (catch NumberFormatException e
                (assoc context :response {:body   "Número inválido catch\n"
                                          :status 400}))))})

(def routes
  #{["/par-ou-impar" :get par-ou-impar]})


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

  (test-request server :get "/par-ou-impar?n=1")
  (test-request server :get "/par-ou-impar?n=2")
  (test-request server :get "/par-ou-impar?n=a")
  )

(def par-ou-impar2
  {:name  ::par-ou-impar
   :enter (fn [context]
            (let [n (-> context :request :query-params :n Integer/parseInt)
                  nxt (if (even? n) par impar)]
              (chain/enqueue context [nxt])))})

(def number-format-handler
  {:name  ::number-format-handler
   :error (fn [context exc]
            (if (= :java.lang.NumberFormatException (:exception-type (ex-data exc)))
              (assoc context :response {:body "Número inválido interceptor!!!\n" :status 400})
              (assoc context :io.pedestal.interceptor.chain/error exc)))})

(def routes
  #{["/par-ou-impar" :get [number-format-handler par-ou-impar2]]})

;; :enter :: Request  -> Request
;; :leave :: Response -> Response
;; :error :: Request  -> Exception -> Response
