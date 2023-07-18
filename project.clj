(defproject mybank-web-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.pedestal/pedestal.service "0.5.10"]
                 [io.pedestal/pedestal.route "0.5.10"]
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 [org.slf4j/slf4j-simple "1.7.28"]
                 [prismatic/schema "1.4.1"]
                 [aleph "0.6.2"]
                 [hato "0.9.0"]
                 [clj-http "3.12.0"]


                 [environ "1.1.0"]
                 [org.apache.kafka/kafka-clients "3.4.0"]
                 [org.apache.kafka/kafka_2.12 "3.4.0"]
                 [org.clojure/tools.logging "1.2.4"]
                 [org.slf4j/slf4j-log4j12 "2.0.7"]
                 [org.apache.logging.log4j/log4j-core "2.20.0"]
                 [org.testcontainers/testcontainers "1.15.3"]
                 [org.testcontainers/kafka "1.15.3"]
                 [net.java.dev.jna/jna "5.7.0"]
                 ;; Redis
                 [com.taoensso/carmine "3.2.0"]

                 ;;MinIO
                 [minio-clj "0.2.2"]

                 [com.amazonaws/aws-java-sdk-s3 "1.12.220"
                  :exclusions [com.fasterxml.jackson.dataformat/jackson-dataformat-cbor
                               commons-logging]]

                 [org.clojure/data.csv "1.0.1"]
                 [org.postgresql/postgresql "42.2.23"]
                 [org.clojure/java.jdbc "0.7.12"]

                 :main] ^:skip-aot mybank-web-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
