(ns mybank-web-api.core
  (:require [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]
            [mybank-web-api.database :as db]
            [mybank-web-api.server :as web-server]
            [clj-http.client :as client])
  (:gen-class))


(def new-sys
  (component/system-map
    :database (db/new-database)
    :web-server (component/using
                  (web-server/new-servidor)
                  [:database])))

(def sys (atom nil))
(defn main [] (reset! sys (component/start new-sys)))

(comment
  (require '[clj-http.client :as client])
  (client/post "http://localhost:9999/deposito/1" {:body "199.93"})
  (main)
  (:web-server @sys)
  (start)
  (http/stop @server)
  (component/stop new-sys)
  )
