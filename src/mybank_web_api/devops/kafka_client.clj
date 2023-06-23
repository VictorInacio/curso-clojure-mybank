(ns mybank-web-api.devops.kafka-client
  (:import [org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
           [org.apache.kafka.clients.consumer KafkaConsumer ConsumerRecords ConsumerRecord]
           (org.apache.kafka.common TopicPartition)
           [org.apache.kafka.common.serialization Serdes]
           [org.apache.kafka.clients.producer.internals DefaultPartitioner]))

(set! *warn-on-reflection* true)

(defn create-producer []
  (let [props {"bootstrap.servers" "localhost:9092"
               "key.serializer"    "org.apache.kafka.common.serialization.StringSerializer"
               "value.serializer"  "org.apache.kafka.common.serialization.StringSerializer"}]
    (KafkaProducer. props)))

(defn produce-message [producer topic key value]
  (.send ^KafkaProducer producer (ProducerRecord. topic key value)))

(defn create-consumer []
  (let [props {"bootstrap.servers"  "localhost:9092"
               "group.id"           "my-consumer-group"
               "key.deserializer"   "org.apache.kafka.common.serialization.StringDeserializer"
               "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"}]
    (KafkaConsumer. props)))

(defn consume-messages [consumer topic]
  (.subscribe ^KafkaConsumer consumer [topic])
  ;(.seekToBeginning ^KafkaConsumer consumer [(TopicPartition. topic 0)])
  (while true
    (let [records (.poll consumer 1000)]
      (doseq [^ConsumerRecord record (seq records)]
        (println "Received message: "
                 (.key record)
                 (.value record)
                 (.offset record))))))


(comment
  (def producer (create-producer))

  (def consumer (create-consumer))

  (produce-message producer "quickstart-events" "chave test 101" (str "Mensagem do Clojure " 101))

  (consume-messages consumer "quickstart-events")

  (.subscribe consumer ["quickstart-events"])

  (.subscription consumer)

  (def result (.poll consumer 1000))
  (def result-seq (seq result))
  (def new-record (-> (.poll consumer 1000)
                      seq
                      first))

  {:value  (.value new-record)
   :key    (.key new-record)
   :offset (.offset new-record)}

  (.seek ^KafkaConsumer consumer (TopicPartition. "quickstart-events" 0) 101)
  (.groupMetadata consumer)
  (.position consumer (TopicPartition. "quickstart-events" 0) )

  (.seekToEnd ^KafkaConsumer consumer (TopicPartition. "quickstart-events" 0) 101)
  (. ^KafkaConsumer consumer (TopicPartition. "quickstart-events" 0) 101)
  )