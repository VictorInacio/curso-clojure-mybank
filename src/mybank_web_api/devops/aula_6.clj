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


(defn rate-limit-interceptor [max-requests]
  {:name  :rate-limiting
   :enter (fn [context]
            (let [redis-key  (tkey "rate-limit-counter")
                  [remaining-requests] (wcar* (redis/incr redis-key)
                                              (redis/expire redis-key 5 "LT"))
                  expiration (wcar* (redis/ttl redis-key))]
              (println "Remaining Requests" (str remaining-requests))
              (println "Retry-After ttl" (str expiration))
              (if (and (<= remaining-requests max-requests) #_(pos? expiration))
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

(def routes
  #{["/test" :get [(rate-limit-interceptor 3) test]]})

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

(comment
  (start)
  (http/stop @server)
  (test-request server :get "/test")
  )