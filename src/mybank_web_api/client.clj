(ns mybank-web-api.client
  (:require [clj-http.client :as client]))

(client/get "http://google.com")

(defn host
  ([path] (host "localhost" 9876 path))
  ([domain port path] (str "http://" domain ":" port path)))

(comment
  (client/get (host "/saldo/1"))
  (client/get (host "/par-ou-impar?n=1"))
  (client/get (host "/par-ou-impar?n=2"))
  (client/get (host "/par-ou-impar?n=a"))
  (client/get (host "/saldo/1"))
  (client/get (host "/home"))
  )