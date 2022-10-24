(ns mybank-web-api.core
  (:require [com.stuartsierra.component :as component]
            [mybank-web-api.database :as db]
            [mybank-web-api.server :as web-server]
            [mybank-web-api.config :as config]
            [mybank-web-api.routing :as routes])
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
  (main)

  )
