(ns mybank-web-api.devops.aula-3
  (:require [clojure.tools.logging :as log]
            [environ.core :refer [env]])
  (:import [org.apache.kafka.clients.admin AdminClientConfig NewTopic KafkaAdminClient]
           [org.apache.kafka.clients.consumer KafkaConsumer]
           [org.apache.kafka.clients.consumer ConsumerRecord]
           [org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
           [org.apache.kafka.common.serialization StringDeserializer StringSerializer]
           [org.apache.kafka.common TopicPartition]
           [java.time Duration]))

(defn create-topics!
  "Create the topic "
  [bootstrap-server topics ^Integer partitions ^Short replication]
  (let [config      {AdminClientConfig/BOOTSTRAP_SERVERS_CONFIG bootstrap-server}
        adminClient (KafkaAdminClient/create config)
        new-topics  (map (fn [^String topic-name]
                           (NewTopic. topic-name partitions replication)) topics)]
    (.createTopics adminClient new-topics)))

(defn- pending-messages
  [end-offsets consumer]
  (some true? (doall
                (map
                  (fn [topic-partition]
                    (let [position (.position consumer (key topic-partition))
                          value    (val topic-partition)]
                      (< position value)))
                  end-offsets))))

(defn parse-consumer-record
  [^ConsumerRecord record]
  {:key       (.key record)
   :value     (.value record)
   :offset    (.offset record)
   :topic     (.topic record)
   :partition (.partition record)
   :timestamp (.timestamp record)})

(defn search-topic-by-key
  "Searches through Kafka topic and returns those matching the key"
  [^KafkaConsumer consumer topic search-key]
  (let [topic-partitions (->> (.partitionsFor consumer topic)
                              (map #(TopicPartition. (.topic %) (.partition %)),,,))
        _                (.assign consumer topic-partitions)
        _                (.seekToBeginning consumer (.assignment consumer))
        end-offsets      (.endOffsets consumer (.assignment consumer))
        found-records    (transient [])]
    (printf "end offsets %s \n" end-offsets)
    (printf "Pending messages? %s \n" (pending-messages end-offsets consumer))
    (while (pending-messages end-offsets consumer)
      (printf "Pending messages? %s \n" (pending-messages end-offsets consumer))
      (let [records            (.poll consumer (Duration/ofMillis 50))
            matched-search-key (filter #(= (.key %) search-key) records)]
        (doseq [record-m (mapv parse-consumer-record matched-search-key)]
          (conj! found-records record-m))
        (doseq [record matched-search-key]
          (let [{:keys [key value]} (parse-consumer-record record)]
            (printf "Searched %s Found Matching Key %s Value %s \n" search-key key value)))))
    (persistent! found-records)))

(defn build-consumer
  "Create the consumer instance to consume
from the provided kafka topic name"
  [bootstrap-server]
  (let [consumer-props
        {"bootstrap.servers",  bootstrap-server
         "group.id",           "example"
         "key.deserializer",   StringDeserializer
         "value.deserializer", StringDeserializer
         "auto.offset.reset",  "earliest"
         "enable.auto.commit", "true"}]
    (KafkaConsumer. consumer-props)))

(defn consumer-subscribe
  [consumer topic]
  (.subscribe consumer [topic]))

(defn build-producer ^KafkaProducer
  ;"Create the kafka producer to send on messages received"
  [bootstrap-server]
  (let [producer-props {"value.serializer"  StringSerializer
                        "key.serializer"    StringSerializer
                        "bootstrap.servers" bootstrap-server}]
    (KafkaProducer. producer-props)))

(defn run-application
  "Create the simple read and write topology with Kafka"
  [bootstrap-server]
  (let [consumer-topic   "example-consumer-topic"
        producer-topic   "example-produced-topic"
        bootstrap-server (env :bootstrap-server bootstrap-server)
        ;replay-consumer  (build-consumer bootstrap-server)
        consumer         (build-consumer bootstrap-server)
        producer         (build-producer bootstrap-server)]
    (printf "Creating the topics %s \n" [producer-topic consumer-topic])
    (create-topics! bootstrap-server [producer-topic consumer-topic] 1 1)
    (printf "Starting the kafka example app. With topic consuming topic %s and producing to %s \n"
            consumer-topic producer-topic)
    ;(search-topic-by-key replay-consumer consumer-topic "1")
    (consumer-subscribe consumer consumer-topic)
    (while true
      (let [records (.poll consumer (Duration/ofMillis 100))]
        (doseq [record records]
          (println "Sending on value" (str "Processed Value: " (.value record)))
          (.send producer (ProducerRecord. producer-topic (.key record) (str "Processed Value: " (.value record))))))
      (.commitAsync consumer))))

(comment
  (def fut-app (future (run-application "localhost:9092")))



  (def bootstrap-server "localhost:9092")
  (def consumer-topic "example-consumer-topic")
  (def producer-topic "example-produced-topic")
  (def replay-consumer (build-consumer bootstrap-server))
  (def consumer (build-consumer bootstrap-server))
  (def producer (build-producer bootstrap-server))
  (println "Creating the topics %s" [producer-topic consumer-topic])
  (create-topics! bootstrap-server [producer-topic consumer-topic] 1 1)
  (search-topic-by-key replay-consumer consumer-topic "1")
  (consumer-subscribe consumer consumer-topic)

  (def records (.poll consumer (Duration/ofMillis 100)))

  (doseq [record records]
    (println "Sending on value" (str "Processed Value: " (.value record)))
    (.send producer (ProducerRecord. producer-topic "a" (str "Processed Value: " (.value record)))))

  (->>
    (let [bootstrap-server "localhost:9092"
          consumer-topic   "example-consumer-topic"
          producer-topic   "example-produced-topic"
          bootstrap-server (env :bootstrap-server bootstrap-server)
          replay-consumer  (build-consumer bootstrap-server)
          consumer         (build-consumer bootstrap-server)
          producer         (build-producer bootstrap-server)]
      ;(println "Creating the topics %s" [producer-topic consumer-topic])
      ;(create-topics! bootstrap-server [producer-topic consumer-topic] 1 1)
      (println "Starting the kafka example app. With topic consuming topic %s and producing to %s"
               consumer-topic producer-topic)
      ;(search-topic-by-key replay-consumer consumer-topic "\"1\"")
      (consumer-subscribe consumer consumer-topic)
      (future
        (while true
          (println ".p.")
          (let [records (.poll consumer (Duration/ofMillis 100))]
            (println "Count " (count records))
            (doseq [record records]
              (println "Sending on value" (str "Processed Value: " (.value record)))
              (.send producer (ProducerRecord. producer-topic "a" (str "Processed Value: " (.value record))))))
          (.commitAsync consumer))))
    (def f))

  (future-cancel f)
  (future-done? f)
  (future-cancelled? f)
  )

(comment
  ;(def bootstrap-server "localhost:9092")
  ;(def app-future (future (run-application-key-topic bootstrap-server)))
  ;(def producer (build-producer bootstrap-server))
  ;(.send producer (ProducerRecord. "generic-messages" (str "client")
  ;                                 "Client event here!"))
  ;(future-cancel app-future)
  )