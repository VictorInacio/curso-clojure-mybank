(ns mybank-web-api.core
  (:require [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]
            [mybank-web-api.database :as db]
            [mybank-web-api.server :as web-server]
            [mybank-web-api.config :as config]
            [mybank-web-api.routing :as routes]
            [clj-http.client :as client]
            [com.walmartlabs.system-viz :refer [visualize-system]])
  (:gen-class))


(def new-sys
  (component/system-map
    :config (config/new-config)
    :routes (routes/new-routes)
    :database (component/using
                (db/new-database)
                [:config])
    :web-server (component/using
                  (web-server/new-servidor)
                  [:database :routes :config])))

(def sys (atom nil))
(defn main [] (reset! sys (component/start new-sys)))

(comment
  (visualize-system new-sys)
  (require '[clj-http.client :as client])
  (client/post "http://localhost:9999/deposito/1" {:body "199.93"})
  (main)
  (:web-server @sys)
  (start)
  (http/stop @server)
  (component/stop new-sys)
  )
