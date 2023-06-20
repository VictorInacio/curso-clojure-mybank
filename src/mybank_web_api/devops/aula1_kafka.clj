(ns mybank-web-api.devops.aula1-kafka
  (:require [org.apache.kafka.clients.consumer :as kafka]
            [clj-http.client :as http]))

;; Define the transducer (same as before)
(defn candle-transducer [hour-period]
  ...)

;; Kafka consumer configuration
(def kafka-config
  {:bootstrap.servers  "localhost:9092"
   :group.id           "candle-consumer-group"
   :key.deserializer   org.apache.kafka.common.serialization.StringDeserializer
   :value.deserializer org.apache.kafka.common.serialization.StringDeserializer})

;; Kafka topic to consume from
(def kafka-topic "candle-topic")

;; Function to process Kafka messages using the transducer
(defn process-kafka-message [message]
  ;; Parse the message into a candle map (assuming it's a JSON string)
  (let [candle (read-json message)]
    ;; Apply the transducer to the candle
    (transduce (candle-transducer 1) conj [candle])))

;; Function to consume messages from Kafka
(defn consume-from-kafka []
  (let [consumer (kafka/KafkaConsumer. kafka-config)]
    (.subscribe consumer [kafka-topic])
    (while true
      (let [records (.poll consumer 100)]
        (doseq [record (iterator-seq records)]
          (let [message (.value record)]
            (process-kafka-message message)))))))

;; HTTP request handler
(defn handle-http-request [request]
  ;; Parse the request body into a candle map (assuming it's a JSON string)
  (let [candle (read-json (:body request))]
    ;; Apply the transducer to the candle
    (let [result (transduce (candle-transducer 1) conj [candle])]
      ;; Return the result as a JSON response
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body    (json/write-str result)})))

;; HTTP server
(defn start-http-server []
  (http/start-server handle-http-request {:port 8080}))

;; Start consuming from Kafka and the HTTP server
(defn -main []
  (future (consume-from-kafka))
  (start-http-server))


(comment
  (ns your-namespace
    (:require [org.apache.kafka.clients.consumer :as consumer]
              [clojure.java.io :as io]))

  (defn process-candle [candle]
    ;; Apply the transducer to calculate candles
    (let [calculate-candles (candle-transducer 1)]
      (transduce calculate-candles conj [candle])))

  (defn consume-kafka-messages []
    (let [props {:bootstrap.servers "localhost:9092"
                 :group.id "your-consumer-group"
                 :auto.offset.reset "latest"}
          consumer (consumer/new-consumer props)
          topic "candle-topic"]
      (consumer/subscribe consumer [topic])
      (while true
        (let [records (consumer/poll 1000)]
          (doseq [record records]
            (let [candle (read-candle-from-kafka-record record)]
              (process-candle candle)))))))

  (defn read-candle-from-kafka-record [record]
    (let [value (.. record (value))]
      ;; Assuming value is a string, you need to parse it into a map
      (read-string value)))

  ;; Call the function to start consuming Kafka messages
  (consume-kafka-messages)
  )