(ns mybank-web-api.db)

(defonce contas- (atom {:1 {:saldo 100}
                        :2 {:saldo 200}
                        :3 {:saldo 300}}))
