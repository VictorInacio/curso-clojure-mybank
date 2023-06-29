(ns mybank-web-api.devops.kafka-client
  (:import [org.apache.kafka.clients.admin AdminClient]
           [java.util.concurrent TimeUnit]
           [org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
           [org.apache.kafka.clients.consumer KafkaConsumer ConsumerRecords ConsumerRecord]
           [org.apache.kafka.clients.admin Admin NewTopic AdminClientConfig]
           (org.apache.kafka.common TopicPartition)
           [org.apache.kafka.common.serialization Serdes]
           [org.apache.kafka.clients.producer.internals DefaultPartitioner]))

(set! *warn-on-reflection* false)

(defn create-producer []
  (let [props {"bootstrap.servers" "localhost:9092"
               "key.serializer"    "org.apache.kafka.common.serialization.StringSerializer"
               "value.serializer"  "org.apache.kafka.common.serialization.StringSerializer"}]
    (KafkaProducer. props)))

"
Constructor Method call
KafkaProducer prod = new KafkaProducer(props)
"

(defn produce-message [producer topic key value]
  (.send ^KafkaProducer producer (ProducerRecord. topic key value)))

"
Instance Method
prod.send(.. ... ..)

"

(defn create-consumer []
  (let [props {"bootstrap.servers"  "localhost:9092"
               "group.id"           "my-consumer-group"
               "key.deserializer"   "org.apache.kafka.common.serialization.StringDeserializer"
               "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"}]
    (KafkaConsumer. props)))

(defn consume-messages [consumer topic]
  (.subscribe ^KafkaConsumer consumer [topic])
  (while true
    (let [records (.poll ^KafkaConsumer consumer 1000)]
      (doseq [^ConsumerRecord record (seq records)]
        (println "Received message: "
                 (.key record)
                 (.value record)
                 (.offset record))))))

(defn create-admin []
  (let [props {"bootstrap.servers"  "localhost:9092"}]
    (Admin/create props)))

"
Admin.create(props)
(Thread/sleep)
"
(defn create-topics!
  "Create the topic "
  [admin topics ^Integer partitions ^Short replication]
  (let [new-topics  (map (fn [^String topic-name]
                           (NewTopic. topic-name partitions replication)) topics)]
    (.createTopics admin new-topics)))

(defn delete-topics!
  "Create the topic "
  [admin deleting-topics]
  (.deleteTopics admin deleting-topics))



(comment
  ;;;;;;;;;; Admin API ;;;;;;;;;
  (def admin  (create-admin))
  (.close admin 1000 TimeUnit/MILLISECONDS)
  (def tlist (.listTopics admin))
  (.names tlist)

  (doto (create-admin)
    (.names tlist)
    (.close 1000 TimeUnit/MILLISECONDS))

  (create-topics! admin ["topic1" "topic1"] 1 1)
  (delete-topics! admin ["generic-messages"])


  ;;;;;;;;  Producer API ;;;;;;;;;;;
  (def producer (create-producer))
  (produce-message producer "turma-async" "chave" (str "Mensagem do Clojure 20230629 " 222))
  (produce-message producer "plaintext-input" "k1" (str "Mensagem do Clojure 20230626 " 202))

  (for [i (range 100)]
    (future
      (dotimes [n 100]
        (produce-message producer "plaintext-input2"
                         (str (* i n))
                         (str "Mensagem do Clojure SEQ " (* i n))))))

  ;;;;;;;;  Consumer API ;;;;;;;;;;;

  (def consumer (create-consumer))
  (.subscribe consumer ["turma-async"])
  (.subscription consumer)

  (def result (.poll consumer 1000))
  (def result-seq (seq result))

  (def record (first result-seq))
  (.key record)
  (.value record)
  (.offset record)
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
  (.position consumer (TopicPartition. "quickstart-events" 0)))
