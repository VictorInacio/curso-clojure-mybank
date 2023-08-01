(ns mybank-web-api.devops.aula-8
  (:require [minio-clj.core :as minio]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn data-path [file]
  (let [file-name (str "/tmp/data/" file)]
    (io/make-parents file-name)
    file-name))

(def payment-facts
  [{:payment-id 1 :date "2023-07-01" :amount 100.50 :customer-id 101 :product-id 201}
   {:payment-id 2 :date "2023-07-02" :amount 200.25 :customer-id 102 :product-id 202}
   {:payment-id 3 :date "2023-07-03" :amount 300.75 :customer-id 103 :product-id 201}
   ])

(def customer-dimensions
  [{:customer-id 101 :name "John Doe" :age 35 :address "123 Main St"}
   {:customer-id 102 :name "Jane Smith" :age 28 :address "456 Elm St"}
   {:customer-id 103 :name "David Johnson" :age 42 :address "789 Oak St"}
   ])

(def product-dimensions
  [{:product-id 201 :name "Savings Account" :type "Account" :currency "USD"}
   {:product-id 202 :name "Credit Card" :type "Card" :currency "USD"}
   {:product-id 203 :name "Checking Account" :type "Account" :currency "USD"}
   ])

(defn write-csv [data file]
  (with-open [writer (io/writer file)]
    (csv/write-csv writer data)))


(defn generate-table-csv [data-lines]
  (let [header (->> data-lines
                    first
                    keys
                    (map name)
                    vec)]
    (concat [header] (for [line data-lines]
                       (vec (vals line))))))

(comment

  (->> payment-facts
       first
       keys
       (map name))

  (generate-table-csv payment-facts)

  (write-csv payment-facts (data-path "payments_map.csv"))
  (write-csv payment-facts (data-path "payments_map.csv"))
  (write-csv (generate-table-csv payment-facts) (data-path "fact_table.csv"))
  (write-csv (generate-table-csv customer-dimensions) (data-path "customer_table.csv"))
  (write-csv (generate-table-csv product-dimensions) (data-path "products_table.csv")))


(comment
  ;; Generation
  (defn generate-payment-fact []
    {:payment-id  (rand-int 1000)
     :date        (str (rand-int 30) "/" (rand-int 12) "/2023")
     :amount      (rand 1000)
     :customer-id (rand-int 100)
     :product-id  (rand-int 100)})

  (defn generate-customer-dimension []
    {:customer-id (rand-int 100)
     :name        (rand-nth ["John Doe" "Jane Smith" "David Johnson" "Sarah Williams"])
     :age         (rand-int 80)
     :address     (str (rand-int 1000) " Main St")})

  (defn generate-product-dimension []
    {:product-id (rand-int 100)
     :name       (rand-nth ["Savings Account" "Credit Card" "Checking Account" "Loan"])
     :type       (rand-nth ["Account" "Card" "Loan"])
     :currency   (rand-nth ["USD" "EUR" "GBP"])})

  (generate-payment-fact)
  (generate-customer-dimension)
  (generate-product-dimension)

  ;; Fact table generation function

  (def payments (atom []))
  (def customers (atom []))
  (def products (atom []))

  (defn generate-fact-table-csv [num-rows]
    (doseq [_ (range num-rows)
            :let [payment (generate-payment-fact)]
            :let [customer (generate-customer-dimension)]
            :let [product (generate-product-dimension)]]
      (swap! payments #(conj % (assoc payment :customer-id (:customer-id customer)
                                              :product-id (:product-id product))))
      (swap! customers #(conj % customer))
      (swap! products #(conj % product))))

  (generate-fact-table-csv 100)
  (deref payments)
  (deref customers)
  (deref products)

  (write-csv (generate-table-csv @payments) (data-path "fact_table.csv"))
  (write-csv (generate-table-csv @customers) (data-path "customers_dimension.csv"))
  (write-csv (generate-table-csv @products) (data-path "products_dimension.csv"))


  ;; Upload
  (def conn (minio/connect "http://127.0.0.1:9000"
                           "minioadmin" "minioadmin"))
  (def bucket-name "aula-8-etl")
  (minio/make-bucket conn bucket-name)
  (minio/put-object conn bucket-name (data-path "fact_table.csv") (data-path "fact_table.csv"))
  (minio/put-object conn bucket-name (data-path "customers_dimension.csv") (data-path "customers_dimension.csv"))
  (minio/put-object conn bucket-name (data-path "products_dimension.csv") (data-path "products_dimension.csv"))

 )

(comment
  ;; Download
  (def fact-csv (minio/get-object conn bucket-name (data-path "fact_table.csv")))
  (def customer-csv (minio/get-object conn bucket-name (data-path "customers_dimension.csv")))
  (def product-csv (minio/get-object conn bucket-name (data-path "products_dimension.csv")))
  ;
  ;;; so you can use it with spit/copy and other Clojure functions that take readers.
  ;
  (spit "fact_table.csv" (slurp fact-csv))
  (spit "customers_dimension.csv" (slurp customer-csv))
  (spit "products_dimension.csv" (slurp product-csv))



  )