(ns mybank-web-api.config
  (:require [com.stuartsierra.component :as component]))

(defrecord Config []
  component/Lifecycle
  (start [this]
    (let [arquivo (-> "resources/config.edn"
                     slurp
                     read-string)]
      (assoc this :config arquivo)))

  (stop [this]
    (dissoc this :config))
  )

(defn new-config []
  (->Config))

(comment
  (slurp "http://www.google.com")
  (slurp "resources/config.edn")
  (spit "resources/config2.edn" {:a 1 :b 2})

  (read-string "{:port 9999\n:db-file \"resources/contas.edn\"}")
  )
