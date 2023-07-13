(ns mybank-web-api.devops.aula-7
  (:require [minio-clj.core :as minio]))

(def conn (minio/connect "http://127.0.0.1:9000"
                         "minioadmin""minioadmin"))  ;;; returns demo connection to public minio server https://play.minio.io:9000


(minio/put-object conn "minioaula7" "canto-chars.txt") ; uploads a file, returns map of {:keys [bucket name]}

(minio/list-objects conn "minioaula7")

(minio/remove-bucket! conn "minioaula7")

(minio/remove-object! conn "minioaula7" "20230713-0946_19c5bc0d-388a-4f99-953f-a16ca85253ce_canto-chars.txt")

(minio/get-download-url conn "minioaula7" "20230712-1302_37d2f7eb-28c2-491c-8eda-fabce2b78a93_canto.txt")


;
(def file (minio/get-object conn "minioaula7" "20230713-0946_19c5bc0d-388a-4f99-953f-a16ca85253ce_canto-chars.txt")) ; returns Clojure  IBufferedReader.







;
;;; so you can use it with spit/copy and other Clojure functions that take readers.
;
(slurp file)













(def conn (minio/connect "https://play.min.io:9000"
                         "Q3AM3UQ867SPQQA43P2F""zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG"))  ;;; returns demo connection to public minio server https://play.minio.io:9000
(def bucket-name "nubootminioaula7")
(minio/make-bucket conn  bucket-name)  ;; creates a bucket, returns bucket name
(minio/put-object conn bucket-name "canto-chars.txt") ; uploads a file, returns map of {:keys [bucket name]}





"
Preparar uma demonstração do MinIO com as seguintes atividades.
1 – Criar Bucket via GUI e outro via API
2 – Upload de Arquivo txt via GUI e via API
3 – Imprimir conteudo de arquivo via API e Clojure
4 – Listar arquivos em todos os buckets de um server via API
5 – Gerar URL via API e baixar arquivo via Browser
6 – Criar endpoint para receber string com usuario e senha e salvar arquivo com a senha HASHEADA
"