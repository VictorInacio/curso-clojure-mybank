(ns mybank-web-api.clojure-language.aula2)

(comment
  (def atomo-nil (atom nil))
  @atomo-nil

  (def atomo (atom "valor"))
  @atomo


  (let [atomo (atom "valor1")]
    @atomo)

  (let [atomo (atom "valor")]
    (deref atomo))
  ;; => "valor"

  (let [atomo (atom "valor")]
    @atomo)
  ;; => "valor"


  (def i (atom 0))
  (swap! i (fn [i] (inc i)))
  (swap! i inc)
  (swap! i + 9)

  (+ @i 9)

  (deref i)

  (reset! i 0)

  (let [atomo (atom "atomo")]
    (swap! atomo str "!")
    @atomo)

  (reset! atomo "nova string")

  (let [atomo (atom "atomo")]
    (reset! atomo "nova string"))

  (def atom-n (atom 0))

  (reset-vals! atom-n 999)

  (swap-vals! atom-n inc)


  (def atomo (atom "atomo"))
  ;; => #'user/atomo

  (string? nil)
  (string? "")
  (string? "asdasddsa")
  (string? 0)
  (set-validator! atomo #(string? %))

  (reset! atomo "123")
  (reset-vals! atomo "999")

  (set-validator! atomo #(or (string? %)
                             (vector? %)))
  ;; => nil

  (swap! atomo str "!")
  ;; => "atomo!"

  (clojure.string/split "aaaaaaobbbbb" #"o")
  (clojure.string/split "aaaaaa  bbbbb " #" ")
  (swap! atomo clojure.string/split #"o")


  ;; Long Branch Tree

  (def long-tree (atom {:node {:leaf 0}}))

  (deref long-tree)
  (defn grow! [tree]
    (swap! tree (fn [tree]
                  (assoc tree :branch tree))))

  (grow! long-tree)

  )

(comment
  (def clock-atom (atom 0))

  (swap! clock-atom inc)
  (swap-vals! clock-atom inc)

  (do
    (Thread/sleep 2000)
    (swap! clock-atom inc)
    )

  (deref clock-atom)

  (def on-off (atom true))

  (defn tic [clock on-off]
    (Thread/sleep 1000)
    (swap! clock-atom inc)
    (println @clock)
    (recur clock on-off))

  (tic clock-atom on-off)

  (reset! on-off false)

  (defn tic! [clock on-off]
    (when @on-off
      (Thread/sleep 1000)
      (swap! clock-atom inc)
      (println @clock)
      (recur clock on-off)))

  (def f (future (tic! clock-atom on-off)))

  (reset! on-off true)
  (reset! on-off false)

  (tic! clock-atom on-off)
  (reset! clock-atom 0)

  ;(promise)

  (comment
    "Exercicio 1 - Construir um relogio que imprime human readable."
    "Exercicio 2 - Construir um relogio que permite tic em segundos ou milisegundos."
    "Exercicio 3 - Construir um relogio que permite tic configuravel."
    "Exercicio 4 - Construir um arvore que as folhas sejam unicas."
    ))


;(ns mybank-web-api.aula-2)

(defn display-tic [clock type]
  (case type
    "sec" (println "segundos:" @clock)
    "ms" (println "milisegundos:" (* 1000 @clock))))

(defn display-tic [clock type]
  (case type
    :sec (println "segundos:" @clock)
    :ms (println "milisegundos:" (* 1000 @clock))))

()

(defn tic! [clock type]
  (let [_           (Thread/sleep 1000)
        selected-fn (-> {:sec #(println "segundos:" @clock)
                         :ms  #(println "milisegundos:" (* 1000 @clock))}
                        type)]
    (swap! clock-atom inc)
    (selected-fn)
    (recur clock type)))

(def clock-atom (atom 0))
(tic! clock-atom :ms)




(defn tic! [clock {:keys [type interval] :as config}]
  (let [
        selected-fn (-> {:sec #(println "segundos:" @clock)
                         :ms  #(println "milisegundos:" (* 1000 @clock))}
                        type)]

    (Thread/sleep interval)
    (swap! clock-atom inc)
    (selected-fn)
    (recur clock config)))

(def f (future (tic! clock-atom {:type     :ms
                                 :interval 2500})))


(def f
  (future (tic! clock-atom {:type     :ms
                            :interval 2500})))

