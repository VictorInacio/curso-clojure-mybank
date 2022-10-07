(ns mybank-web-api.client
  (:require [clj-http.client :as client]))

(client/get "http://example.com/resources/id")
