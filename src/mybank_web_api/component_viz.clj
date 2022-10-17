(ns mybank-web-api.component-viz
  (:require [com.walmartlabs.system-viz :refer [visualize-system]]
            [com.stuartsierra.component :as component]))



(def sys
  (component/system-map
    :database (component/using {} [])
    :web-server (component/using {} [:database])))

(visualize-system sys)