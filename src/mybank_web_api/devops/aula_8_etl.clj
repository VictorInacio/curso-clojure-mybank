(ns mybank-web-api.devops.aula-8-etl
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn data-path [file]
  (let [file-name (str "/tmp/data/" file)]
    (io/make-parents file-name)
    file-name))


;; Configure PostgreSQL connection
(def pg-conn {:dbtype   "postgresql"
              :dbname   "postgres"
              :user     "postgres"
              :password "postgres"
              :host     "localhost"
              :port     "5432"})

;; Execute an OLAP query using PostgreSQL
(defn execute-olap-query [query]
  (jdbc/with-db-connection
    [conn pg-conn]
    (jdbc/with-db-transaction [tx conn]
                              (jdbc/query tx [query]))))

(defn import-csv-to-postgres [table-name csv-file]
  (jdbc/with-db-connection
    [conn pg-conn]
    (jdbc/with-db-transaction
      [tx conn]
      (jdbc/db-do-commands tx
                           (jdbc/execute! tx (str "COPY " table-name " FROM '" csv-file "' DELIMITER ',' CSV HEADER;"))))))

(def bronze-fact-table "fact_table")
(def bronze-customer-dimension "customers_dimension")
(def bronze-product-dimension "products_dimension")

(comment

  ;; Define table names
  (import-csv-to-postgres bronze-fact-table "./fact_table.csv")

  ;; Import CSV files to bronze tables
  (defn import-csv-to-bronze-tables []

    (import-csv-to-postgres bronze-customer-dimension "s3://minio-bucket/customer_dimension.csv")
    (import-csv-to-postgres bronze-product-dimension "s3://minio-bucket/product_dimension.csv"))

  ;; Import CSV files to bronze tables
  (defn import-csv-to-bronze-tables []
    (import-csv-to-postgres bronze-fact-table "s3://minio-bucket/fact_table.csv")
    (import-csv-to-postgres bronze-customer-dimension "s3://minio-bucket/customer_dimension.csv")
    (import-csv-to-postgres bronze-product-dimension "s3://minio-bucket/product_dimension.csv"))

  ;; Import CSV files to bronze tables
  (import-csv-to-bronze-tables))


(comment
  ;; Example OLAP queries

  ;; Slicing: Retrieve payments for a specific date
  (defn slice-by-date [date]
    (execute-olap-query
      (str "SELECT * FROM fact_table WHERE date >= '" date "'")))

  (defn slice-by-date [date]
    (execute-olap-query
      (str "SELECT * FROM fact_table")))

  ;; Dicing: Retrieve payments for a specific customer and product
  (defn dice-by-customer-and-product [customer-id product-id]
    (execute-olap-query
      (str "SELECT * FROM fact_table WHERE \"customer-id\" = " customer-id " AND \"product-id\" = " product-id)))

 (defn dice-by-customer [customer-id]
    (execute-olap-query
      (str "SELECT * FROM fact_table WHERE \"customer-id\" = " customer-id)))

  ;; Aggregation: Calculate the total amount of payments by customer
  (defn aggregate-total-by-customer []
    (execute-olap-query
      "SELECT \"customer-id\", sum(amount) as total_amount, count(*) as ctn FROM fact_table GROUP BY \"customer-id\""))

  ;; Drill-down: Retrieve payments by customer and their associated dimensions
  (defn drill-down-by-customer [customer-id]
    (execute-olap-query
      (str "SELECT * FROM fact_table WHERE \"customer-id\" = " customer-id)))

(defn drill-down-by-customer [customer-id]
    (execute-olap-query
      (str "SELECT * FROM fact_table
      INNER JOIN customers_dimension ON fact_table.\"customer-id\" = customers_dimension.\"customer-id\"
      INNER JOIN products_dimension  ON fact_table.\"product-id\" = products_dimension.\"product-id\"
      WHERE fact_table.\"customer-id\" = " customer-id)))


  ;; Usage examples

  ;; Slice payments by a specific date
  (->> (slice-by-date "28/3/2023")
       (sort-by :date))

  ;; Dice payments by a specific customer and product
  (dice-by-customer-and-product 51 22)
  (dice-by-customer 51)

  ;; Aggregate total payment amounts by customer
  (aggregate-total-by-customer)

  ;; Drill down to retrieve payments by a specific customer and its associated dimensions
  (drill-down-by-customer 51)

  )


(comment
  ;docker pull postgres

  ;docker run -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres

  ;-- public.facts_table definition
  ;
  ;-- Drop table
  ;
  ;-- DROP TABLE public.facts_table;
  ;
  ;CREATE TABLE public.facts_table (
  ;                                  "payment-id" int4 NULL,
  ;                                  "date" varchar(50) NULL,
  ;                                  amount float4 NULL,
  ;                                  "customer-id" int4 NULL,
  ;                                  "product-id" int4 NULL
  ;                                  );
  )
