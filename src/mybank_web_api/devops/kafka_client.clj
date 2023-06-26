(ns mybank-web-api.devops.kafka-client
  (:import [org.apache.kafka.clients.admin AdminClient]
           [java.util.concurrent TimeUnit]
           [org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
           [org.apache.kafka.clients.consumer KafkaConsumer ConsumerRecords ConsumerRecord]
           [org.apache.kafka.clients.admin Admin]
           (org.apache.kafka.common TopicPartition)
           [org.apache.kafka.common.serialization Serdes]
           [org.apache.kafka.clients.producer.internals DefaultPartitioner]))

(set! *warn-on-reflection* false)

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
  (while true
    (let [records (.poll consumer 1000)]
      (doseq [^ConsumerRecord record (seq records)]
        (println "Received message: "
                 (.key record)
                 (.value record)
                 (.offset record))))))

(defn create-admin []
  (let [props {"bootstrap.servers"  "localhost:9092"}]
    (Admin/create props)))

(defn consume-messages [consumer topic]
  (.subscribe ^KafkaConsumer consumer [topic])
  (while true
    (let [records (.poll consumer 1000)]
      (doseq [^ConsumerRecord record (seq records)]
        (println "Received message: "
                 (.key record)
                 (.value record)
                 (.offset record))))))

(comment
  (def admin  (create-admin))
  (.close admin 1000 TimeUnit/MILLISECONDS)
  (def tlist (.listTopics admin))
  (.names tlist)
  (.crea tlist)


  (def producer (create-producer))
  (produce-message producer "quickstart-events" "chave test 101" (str "Mensagem do Clojure 20230623 " 101))
  (def iter (range 10))
  (doseq [n iter]
    (produce-message producer "quickstart-events" (str n) (str "Mensagem do Clojure SEQ " n)))
  (produce-message producer "quickstart-events" "chave test 101" (str "Mensagem do Clojure 20230623 " 101))



  ;;;;;;;;;;;;;;;;;;;

  (def consumer (create-consumer))
  (.subscribe consumer ["quickstart-events"])
  (.subscription consumer)

  (def result (.poll consumer 1000))
  (def result-seq (seq result))

  (def msg1 (last result-seq))
  (def new-record (-> (.poll consumer 1000)
                      seq
                      first))

  (for [msg1 result-seq]
    {:value  (.value msg1)
     :key    (.key msg1)
     :offset (.offset msg1)})

  (consume-messages consumer "quickstart-events")
  (.seek ^KafkaConsumer consumer (TopicPartition. "quickstart-events" 0) 60)
  (.seekToBeginning ^KafkaConsumer consumer (TopicPartition. "quickstart-events" 0))
  (.seekToEnd ^KafkaConsumer consumer (TopicPartition. "quickstart-events" 0))
  (.groupMetadata consumer)
  (.position consumer (TopicPartition. "quickstart-events" 0))

  )
