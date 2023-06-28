(ns mybank-web-api.devops.aula-2-kstreams
  (:require [clojure.tools.logging :as log])
  (:import
    (java.util Properties)
    (org.apache.kafka.common.serialization Serdes)
    (org.apache.kafka.streams StreamsConfig KafkaStreams StreamsBuilder Topology)
    (org.apache.kafka.streams.kstream ValueMapper)))

(comment
(Serdes.)
(StreamsConfig)
  )