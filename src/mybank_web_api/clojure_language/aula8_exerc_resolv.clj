(ns mybank-web-api.clojure-language.aula8-exerc-resolv)


(def replace-map {\( ""
                  \) ""})

(clojure.string/escape "O a (homem) que 'diz sou não é" replace-map)
