(ns mybank-web-api.core-test
  (:require [clojure.test :refer [deftest testing is] :as test]
            [mybank-web-api.devops.aula-3 :refer [build-consumer build-producer run-application consumer-subscribe]])
  (:import (org.testcontainers.containers KafkaContainer)
           (org.apache.kafka.clients.producer ProducerRecord)
           (org.testcontainers.utility DockerImageName)))


(deftest example-kafka-integration-test
  (testing "Fire up test containers Kafka and then send and consume message"
    (let
      [kafka-container  (KafkaContainer. (DockerImageName/parse "confluentinc/cp-kafka:5.5.3"))
       _                (.start kafka-container)
       bootstrap-server (.getBootstrapServers kafka-container)
       _                (println "bootstrap-server ->" bootstrap-server)
       test-producer    (build-producer bootstrap-server)
       _                (future (run-application bootstrap-server)) ; execute application in separate thread
       producer-topic   "example-consumer-topic"
       test-consumer    (build-consumer bootstrap-server)
       _                (consumer-subscribe test-consumer "example-produced-topic")
       input-data       "hello"
       sent-result      (.get (.send test-producer (ProducerRecord. producer-topic input-data)))
       records          (.poll test-consumer 10000)]
      (is (= producer-topic (.topic sent-result)))
      (doseq [record records]
        (is (= (format "ERRRRRR Processed Value: %s" input-data) (.value record)))))))


(comment
  (test/run-all-tests))