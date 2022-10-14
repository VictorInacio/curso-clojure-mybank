(ns mybank-web-api.core
  (:require [mybank-web-api.db :as db]
            [mybank-web-api.interceptors :as app-interceptors]
            [io.pedestal.http.content-negotiation :as conneg]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test-http]
            [io.pedestal.interceptor :as i]

            [io.pedestal.interceptor.chain :as chain]
            [clojure.pprint :as pp])
  (:gen-class))

;; 2. check content
;; curl -i -H "Accept: application/xml" http://localhost:9999/contas
(def supported-types ["text/html" "application/edn" "application/json" "text/plain"])
(def content-neg-intc (conneg/negotiate-content supported-types))

(def routes
  (route/expand-routes
   #{
     ;; 1. using function
     ["/hello" :get app-interceptors/hello :route-name :hello]
     ;; 1. using interceptor
     ["/hellov2" :get app-interceptors/hello-interceptor :route-name :hellov2]
     ;; 1. using anom func
     ["/echo" :get #(hash-map :body % :status 200) :route-name :echo]

     ;; ;; 2. using parameters
     ;; TODO: use interceptors
     ["/hello/:name" :get (fn [context]
                            {:status 200
                             :body (format "Hello, %s!"
                                           (get-in context [:path-params :name]))})
      :route-name :hello-user]
     ;; 2. Pega tudo
     ["/pega-tudo/*subpage" :get app-interceptors/echo :route-name :pega-tudo]
     ;; 2. body/params interceptor
     ["/body-params" :post [(body-params/body-params)
                            app-interceptors/print-n-continue
                            app-interceptors/echo] :route-name :hello-body]
     ;; 2. Using constraints
     ["/constraints/:user-id" :get [(body-params/body-params)
                                    app-interceptors/print-n-continue
                                    app-interceptors/echo]
      :constraints {:user-id #"^[a-zA-Z0-9]*"}]
     ;; ^[a-zA-Z0-9]*

     ;; 3. business logic (coerce-body)
     ["/contas" :get [app-interceptors/print-n-continue
                      ;; 3. add interceptador p/ alterar reposta final
                      app-interceptors/coerce-body
                      app-interceptors/lista-contas-interceptor] :route-name :contas]
     ["/saldo/:id" :get [app-interceptors/get-saldo-interceptor] :route-name :saldo]
     ["/deposito/:id" :post app-interceptors/make-deposit-interceptor :route-name :deposito]}))

(defonce server (atom nil))

(def service-map-simple {::http/routes routes
                         ::http/port   9999
                         ::http/type   :jetty
                         ::http/join?  false})

(def service-map (-> service-map-simple
                     (http/default-interceptors)
                     (update ::http/interceptors conj
                             ;; 3. checa content header
                             content-neg-intc
                             ;; 2. carrega contas ao contexto
                             app-interceptors/carrega-contas-interceptor)))

(http/log-request)

(defn create-server
  "Creates a HTTP Server using `service-map` definitions."
  []
  (http/create-server
   service-map))

(defn start
  "Starts the HTTP Server using `create-server` and holds its reference on `server` atom."
  []
  (reset! server (http/start (create-server))))

(defn reset-server!
  "Restarts the HTTP server defined by `server`"
  []
  (try
    (when @server
      (println "Trying to stop server...")
      (http/stop @server))
    (catch Exception e
      (str "Couldn't start the server: " (.getMessage e)))
    (finally (start))))

(comment
  ;; 1. dados das rotas
  routes)
