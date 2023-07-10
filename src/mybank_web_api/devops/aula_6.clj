(ns mybank-web-api.devops.aula_6
  (:require [taoensso.carmine :as car]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as i]
            [io.pedestal.interceptor.chain :as chain]
            [io.pedestal.test :as test-http]
            [taoensso.carmine :as redis]))

(defonce my-conn-pool (redis/connection-pool {}))           ; Create a new stateful pool
(def my-conn-spec-1 {:uri "redis://127.0.0.1:6379/"})

(def conn-opts
  {:pool my-conn-pool
   :spec my-conn-spec-1})

(defmacro wcar*
  [& body]
  `(redis/wcar conn-opts ~@body))

;; Rate Limiting
(def tkey (partial redis/key :mybank :aula6 :rate-limit))

(def dbg (atom nil))

(defn rate-limit-interceptor [max-requests seconds-window]
  {:name  :rate-limiting
   :enter (fn [context]
            (let [remote-addr (get-in context [:request :remote-addr])
                  redis-key   (tkey "rate-limit-counter" remote-addr)
                  [remaining-requests] (wcar* (redis/incr redis-key)
                                              (redis/expire redis-key seconds-window "LT"))
                  expiration  (wcar* (redis/ttl redis-key))]
              (println "Remaining Requests" (str remaining-requests))
              (println "Retry-After ttl" (str expiration))
              (if (<= remaining-requests max-requests)
                context
                (chain/terminate
                  (assoc context
                    :response
                    {:status  429
                     :body    "Rate limit exceeded."
                     :headers {"Retry-After" (str expiration)}})))))})

(def test
  (i/interceptor
    {:name  ::test
     :enter (fn [context]
              (assoc context :response {:body   "API respondendo!"
                                        :status 200}))}))
(defonce user-credentials {:1 "123"
                           :2 "123"
                           :3 "123"})
(def login
  (i/interceptor
    {:name  ::login
     :enter (fn [context]
              (let [request       (get context :request)
                    user-id       (-> request :path-params :id keyword)
                    sent-password (-> request :body slurp str)
                    user-password (get user-credentials user-id)
                    authorized?   (= sent-password user-password)]
                (println "user-key -> " user-id)
                (println "sent-password -> " sent-password)
                (println "user-password -> " user-password)
                (if authorized?
                  (let [session-id     (str (random-uuid))
                        user-key       (tkey "user-session" user-id)
                        redis-response (wcar* (redis/set user-key session-id)
                                              (redis/expire user-key (* 60 60) "LT"))]
                    (println "Session created ->" session-id)
                    (assoc context :response
                                   {:status 200 :body (str session-id)}))
                  (chain/terminate
                    (assoc context
                      :response
                      {:status 401
                       :body   "Usuário ou senha inválido."})))))}))

(def session
  (i/interceptor
    {:name  ::login
     :enter (fn [context]
              (let [request        (get context :request)
                    user-id        (-> request :path-params :id keyword)
                    user-key       (tkey "user-session" user-id)
                    sent-session   (-> request :body slurp str)
                    session-state  (wcar* (redis/get user-key))
                    authenticated? (= sent-session session-state)]
                (println "user-key -> " user-key)
                (println "sent-session -> " sent-session)
                (println "session-state -> " session-state)
                (if authenticated?
                  (assoc context :session session-state)
                  (chain/terminate
                    (assoc context
                      :response
                      {:status 401
                       :body   "Acesso não authorizado."})))))}))



(def routes
  #{["/test" :get [(rate-limit-interceptor 3 5) test] :route-name :test]
    ["/login/:id" :post [login] :route-name :login]
    ["/auth-test/:id" :post [session test] :route-name :auth-test]})

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
  (test-request server :get "/test")
  (test-post server :post "/login/1" "123")
  (test-post server :post "/auth-test/1" "35ac6577-2048-4b21-92b9-86a2eda57524")
  )