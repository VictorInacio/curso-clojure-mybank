(ns mybank-web-api.interceptors.html
  (:require [hiccup.core :as hiccup]))

(comment
  (defn get-users []
    [:div
     [:p "Usr1"]
     [:p "Usr2"]
     [:p "Usr3"]])

  (hiccup/html [:html
                [:body
                 [:div
                  (get-users)
                  [:span "Spam!"]]]]))


(defn home-page
  [context]
  (let [nome (get-in context [:request :query-params :nome])]
    (assoc context :response
                   {:status  200
                    :headers {"Content-Type" "text/html"}
                    :body    (hiccup/html [:html
                                           [:body
                                            [:div
                                             [:span {:style "background-color:powderblue;text-decoration: blink;"} "Spam!"]]
                                            [:div [:p (str "Nome Cliente: " nome)]]]])})))
#_#_(def js-string
      "
    var eventSource = new EventSource(\"http://localhost:8080/counter\");
    eventSource.addEventListener(\"counter\", function(e) {
      console.log(e);
      var counterEl = document.getElementById(\"counter\");
      counter.innerHTML = e.data;
    });
    ")

        (hiccup/html [:html
                      [:head
                       [:script {:type "text/javascript"}
                        js-string]]
                      [:body
                       [:div
                        [:span "Counter: "]
                        [:span#counter]]]])