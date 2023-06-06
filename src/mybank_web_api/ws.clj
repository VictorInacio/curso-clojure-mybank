(ns mybank-web-api.ws
  (:require [manifold.deferred :as d]
            [manifold.stream :as s]
            [aleph.http :as http]
            [hato.websocket :as ws]))

;; CLIENT
(defn ws-cli [api-url]
  (ws/websocket api-url
                {:on-message (fn [ws msg last?] (println "Received message:" msg))
                 :on-close   (fn [ws status reason] (println "WebSocket closed!"))}))

(def stream-url "wss://stream.binance.com:9443/ws/")

(def sub "{\"method\":\"SUBSCRIBE\",\"params\":[\"btcusdt@aggTrade\",\"btcusdt@depth\"],\"id\":1}")
(def sub {:method :SUBSCRIBE
          :params ["a" "_ _"]})

(print sub)
(comment
  (do
    (def ws @(ws-cli (str stream-url "btcbrl@trade")))
    (ws/send! ws sub)
    (Thread/sleep 5000)
    (ws/close! ws)))



;; SERVER
(comment
  (defn uppercase-handler
    "Handle a message by upper casing it and echoing it back."
    [socket msg]
    (s/put! socket (clojure.string/upper-case msg)))

  (defn uppercase-handler
    "Handle a message by upper casing it and echoing it back."
    [socket msg]
    (s/put! socket (str (clojure.string/upper-case msg) " "
                        (clojure.string/lower-case msg))))

  (def server
    (http/start-server
      (fn [req]
        (d/chain
          (http/websocket-connection req)
          (fn [socket]
            (s/consume (fn [msg] (uppercase-handler socket msg)) socket))))
      {:port 9999}))


  (ws/close! ws2)

  (do
    (def ws2 @(ws-cli "ws://127.0.0.1:9999"))
    ; > A MESSAGE
    (ws/send! ws2 "A message")
    ; > ANOTHER MESSAGE
    (ws/send! ws2 "Another message"))

  (ws/send! ws2 "asaaaaaaa")
  )