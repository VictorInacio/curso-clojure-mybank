(ns mybank-web-api.database
  (:require [com.stuartsierra.component :as component]))

"Nossa aplicação até o momento inicia nosso banco de dados em um simples atomo com
 um map clojure"

(defonce contas (atom {:1 {:saldo 100}
                       :2 {:saldo 200}
                       :3 {:saldo 300}}))

"Dessa forma a inicialização e encerramento dele deve ser controlada por nós.
Vamos definir nosso banco em um component, nesse exemplo simples ainda pouco vemos
a vantagem nisso, porém quando esse banco tiver uma conexão utilizada em muitos momentos
por todo o código fica mais claro e bem definido onde é o ponto que o banco é acessivel no
sistema."

"Essa store que o menino ta falando vc entendeu o que faz e pra que serve?"
"Aqui no meu exemplo o contas é a mesma coisa, um map com chaves e valores, ai no
exemplo dele a chave é o uuid da tarefa, aqui é o numero da conta :1, :2 e :3.

O valor de cada chave no mapa dele é um outro mapa com o nome e status da tarefa.

No meu exemplo pra cada conta tem um mapa com a chave :saldo e o valor"

(defrecord Database []
  component/Lifecycle

  (start [this]
    (println "Iniciar o atomo contendo dados das contas bancárias inicialmente procurando um
    arquivo resources/contas.edn, caso não exista iniciar o map pré definido.")
    (let [arquivo (-> "resources/contas.edn"
                      slurp
                      read-string)]
      (if arquivo
        (assoc this :contas (atom arquivo))
        (assoc this :contas (atom {:1 {:saldo 100}
                                   :2 {:saldo 200}
                                   :3 {:saldo 300}})))))


  (stop [this]
    (println "Limpar as contas da memória e Salvar em disco para uso futuro.")
    (assoc this :store nil)))

(defn new-database []
  (->Database))


(comment

  (java.util.UUID/randomUUID)
  (def store {(java.util.UUID/randomUUID) {:tarefa "Estudar na Biblioteca"
                                           :status "Em progresso!"}})

  {#uuid"9ec3aebf-7356-4e48-845d-452cb17f5e12"
   {:tarefa "Estudar na Biblioteca"
    :status "Em progresso!"}}

  "Depois vou baixar a aula de ontem pra vc assistir,
  melhor que esse sonso ai"


  )

