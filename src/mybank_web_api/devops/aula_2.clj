(ns mybank-web-api.devops.aula-2
  (:gen-class)
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as jio]
    )
  (:import
    (java.util Properties)
    (org.apache.kafka.common.serialization Serdes)
    (org.apache.kafka.clients.admin AdminClient NewTopic)
    (org.apache.kafka.clients.producer Callback KafkaProducer ProducerConfig ProducerRecord)
    (org.apache.kafka.common.errors TopicExistsException)

    (org.apache.kafka.common.serialization Serdes)
    (org.apache.kafka.streams StreamsConfig KafkaStreams StreamsBuilder Topology)
    (org.apache.kafka.streams.kstream ValueMapper)

    ))

"
https://docs.confluent.io/platform/current/clients/examples/clojure.html
"


(defn- build-properties [config-fname]
  (with-open [config (jio/reader config-fname)]
    (doto (Properties.)
      (.putAll
        {ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG   "org.apache.kafka.common.serialization.StringSerializer"
         ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringSerializer"})
      (.load config))))

(defn- create-topic! [topic partitions replication cloud-config]
  (let [ac (AdminClient/create cloud-config)]
    (try
      (.createTopics ac [(NewTopic. ^String topic  (int partitions) (short replication))])
      ;; Ignore TopicExistsException, which would get thrown if the topic was previously created
      (catch TopicExistsException e nil)
      (finally
        (.close ac)))))

(defn producer! [config-fname topic]
  (let [props          (build-properties config-fname)
        print-ex       (comp println (partial str "Failed to deliver message: "))
        print-metadata #(printf "Produced record to topic %s partition [%d] @ offest %d\n"
                                (.topic %)
                                (.partition %)
                                (.offset %))
        create-msg     #(let [k "alice"
                              v (json/write-str {:count %})]
                          (printf "Producing record: %s\t%s\n" k v)
                          (ProducerRecord. topic k v))]
    (with-open [producer (KafkaProducer. props)]
      (create-topic! topic 1 3 props)
      (let [;; We can use callbacks to handle the result of a send, like this:
            callback (reify Callback
                       (onCompletion [this metadata exception]
                         (if exception
                           (print-ex exception)
                           (print-metadata metadata))))]
        (doseq [i (range 5)]
          (.send producer (create-msg i) callback))
        (.flush producer)
        ;; Or we could wait for the returned futures to resolve, like this:
        (let [futures (doall (map #(.send producer (create-msg %)) (range 5 10)))]
          (.flush producer)
          (while (not-every? future-done? futures)
            (Thread/sleep 50))
          (doseq [fut futures]
            (try
              (let [metadata (deref fut)]
                (print-metadata metadata))
              (catch Exception e
                (print-ex e))))))
      (printf "10 messages were produced to topic %s!\n" topic))))

(defn -main [& args]
  (apply producer! args))


"https://github.com/perkss/clojure-kafka-examples"


(defn create-properties [map]
  (let [properties (Properties.)]
    (doseq [[key value] map]
      (.setProperty properties (str key) (str value)))
    properties))



;; Output: "value1"

(defn to-uppercase-topology [input-topic output-topic]
  (let [builder (StreamsBuilder.)]
    (->
      (.stream builder input-topic)                         ;; Create the source node of the stream
      (.mapValues (reify
                    ValueMapper
                    (apply [_ v]
                      (clojure.string/upper-case v))))      ;; map the strings to uppercase
      (.to output-topic))
    builder))

(comment


  (let [config-map {StreamsConfig/APPLICATION_ID_CONFIG,            "uppercase-processing-application"
                    StreamsConfig/BOOTSTRAP_SERVERS_CONFIG,         "localhost:9092"
                    StreamsConfig/DEFAULT_KEY_SERDE_CLASS_CONFIG,   (.getName (.getClass (Serdes/String)))
                    StreamsConfig/DEFAULT_VALUE_SERDE_CLASS_CONFIG, (.getName (.getClass (Serdes/String)))}
        config-map-j (create-properties config-map)
        input-topic  "plaintext-input"
        output-topic "uppercase"
        topology     (.build (to-uppercase-topology input-topic output-topic))]
    (def streams      (KafkaStreams. ^Topology topology config-map-j))
    (future (.start streams)))

  (.state streams)
  (.stop streams)



  (def m-str (str {:a 1}))
  (read-string m-str)
  )