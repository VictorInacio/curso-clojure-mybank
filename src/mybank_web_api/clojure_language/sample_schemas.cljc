(ns backend.domain.person
  (:require
    [clojure.string :as string]
    [plumbing.core :refer [defnk]]
    [schema.core :as s]
    [backend.domain.entity :refer [create] :as entity]
    [datomic.api :as d]
    [backend.domain.common :as common]
    [common.util :as cu]
    [backend.db.utils :as du]
    [backend.domain.audit :as audit]
    [backend.domain.core :as c]
    [backend.db.conn :as db-conn]
    [backend.background.task-queue :as task-queue]
    [backend.permissions.create-permissions :as create-permissions :refer [assert-create-access!]]
    [backend.permissions.request-access :as request-access]
    [backend.error :as error]
    [common.roles :as roles]
    [common.time :as c-time]))

(defmethod assert-create-access! :entity.type/person
  [ctx]
  (let [{:keys [entity-data tenant account account-current-permissions conn db role]} ctx]
    (if-not (roles/has-access? role :role/user)
      (error/access-denied! "No permissions")
      true)))

(defmethod create :entity.type/person
  [data ctx]
  (let [new-name (when-not (or (:person/first-name data)
                               (:person/last-name data)
                               (:person/universal-email-address data))
                   (:data data))
        tenant (:tenant ctx)
        temp-id (or (:db/id data) (str "new-person-" new-name))
        email (:person/universal-email-address data)
        create-datom (->> (dissoc data :data)
                          (merge
                            {:person/contact-owner (:db/id (:account ctx))
                             :db/id temp-id}
                            (when (string? email)
                              {:common/lower-case-str (-> email
                                                          string/trim
                                                          string/lower-case)})
                            (when new-name
                              {:person/first-name new-name})))]
    (concat [create-datom]
            (when (string? email)
              [[:common/assert-unique-tenant-value (:db/id tenant) temp-id :entity.type/person
                (string/trim email)]]))))


;;
;; Search persons with string:
;; ====================================================================
;;
;; TODO remove and use generic create-entity instead
(defn persist
  [{:keys [body account tenant conn] :as ctx}]
  (let [{:keys [person/first-name person/last-name
                person/universal-title
                person/universal-email-address person/universal-phone-number
                person/notes person/linkedin-url person/twitter
                person/facebook person/gender person/avatar-url
                person/website person/location-city person/location-country]}
        body
        id "new-person"
        now (System/currentTimeMillis)
        tx-data (->> (concat (c/person first-name
                                       last-name
                                       universal-email-address
                                       tenant
                                       account
                                       {:temp-id          id
                                        :now              now
                                        :phone            universal-phone-number
                                        :notes            notes
                                        :twitter          twitter
                                        :title            universal-title
                                        :facebook         facebook
                                        :gender           gender
                                        :avatar-url       avatar-url
                                        :website          website
                                        :location-city    location-city
                                        :location-country location-country
                                        :linkedin-url     linkedin-url})
                             (common/creation-attributes id account now)
                             (when universal-email-address
                               [[:db/add id :common/lower-case-str
                                 (-> universal-email-address
                                     string/trim
                                     string/lower-case)]
                                [:common/assert-unique-tenant-value (:db/id tenant) nil
                                 :entity.type/person (string/trim universal-email-address)]])
                             (audit/audit ctx :op.person/create id [tenant]))
                     (db-conn/transact)
                     (du/extract-tx))
        {:strs [new-person]} tx-data
        db-after (:db-after tx-data)]
    (future
      (task-queue/push-task ctx
                            {:background-task/type :background-task.type/intelligence-sync-if-needed
                             :tenant-id (:db/id tenant)
                             :entity-id (:db/id new-person)}))
    new-person))

(s/defschema Emails {:emails [s/Str]})
(s/defschema EmailIdMappings {s/Str s/Int})

(defn create-persons-from-emails
  [emails account ctx]
  (let [tenant (:tenant ctx)
        tx-data (->> emails
                     (mapcat (fn [email]
                               (let [new-entity-data {:entity/type :entity.type/email
                                                      :person/universal-email-address email
                                                      :person/contact-owner (:db/id account)}]
                                 (create-permissions/assert-create-access!
                                   (assoc ctx :entity-data new-entity-data))
                                 (concat (entity/create-entity-txn (assoc new-entity-data
                                                                     :db/id
                                                                     email)
                                                                   ctx)
                                         (audit/audit ctx :op.person/create email [tenant])))))
                     (db-conn/transact)
                     (du/extract-tx))
        temp-ids (-> tx-data
                     (get "tx")
                     :tempids)]
    (select-keys temp-ids emails)))

(defnk create-person-many
       "Create a new person."
       {:in Emails
        :out EmailIdMappings
        :required-role :role/guest-sell-side
        :allowed-for-read-only-user? false
        :access-check-fn (fn [{:keys [db body account tenant] :as request}])}
       [body account :as ctx]
       (let [emails (:emails body)
             email-id-mappings (create-persons-from-emails emails account ctx)]
         {:body email-id-mappings}))

;;
;; Get all emails belonging to a person
;; ==================================================================
;;

(s/defschema MassActionRequest
  {:action     s/Keyword
   :person-ids [s/Int]})

(s/defschema MassActionResponse {:status s/Bool})

(defnk mass-action
       "Mass action for number of persons"
       {:in MassActionRequest
        :out MassActionResponse
        :required-role :role/user
        :allowed-for-read-only-user? false
        :access-check-fn (fn [{:keys [db conn account tenant body] :as request}]
                           (doseq [person-id (:person-ids body)]
                             (request-access/assert-can-write-entity-id!
                               (assoc request :entity-id person-id))))}
       [body account tenant db conn :as ctx]
       (let [action (:action body)
             ids (:person-ids body)
             ctx (assoc ctx
                   :now (c-time/now-millis)
                   :entity-type :entity.type/person)]
         (case action
           :delete-contacts (->> ids
                                 (map (partial common/disable-entity-type-datoms ctx))
                                 (apply concat (audit/audit ctx :op.person/disable tenant ids))
                                 (db-conn/transact)
                                 (deref)))
         {:body {:status true}}))

(defn find-by-email
  [email db tenant]
  (->> (d/q '[:find [?e ...]
              :in $ ?name ?tenant-id
              :where
              [?e :common/lower-case-str ?name]
              [?e :entity/type :entity.type/person]
              [?e :common/tenant ?tenant-id]
              (not [?e :common/disabled? true])]
            db
            (cu/str-trim-lcase email)
            (:db/id tenant))
       (first)
       (d/entity db)))


;;
;; Event handlers:
;;

(def handlers
  {:person/create-many #'create-person-many
   :person/mass-action #'mass-action
   })


(s/defschema NewTask
  (-> {:task/assignee common/EID
       :task/assigner common/EID
       :task/associated-assets #{common/EID}
       :task/associated-commitments #{common/EID}
       :task/associated-deals #{common/EID}
       :task/associated-fund-commitments #{common/EID}
       :task/associated-fundraising-opportunities #{common/EID}
       :task/associated-funds #{common/EID}
       :task/associated-holdings #{common/EID}
       :task/associated-investors #{common/EID}
       :task/associated-persons #{common/EID}
       :task/completed? s/Bool
       :task/created s/Int
       :task/data s/Str
       :task/due s/Int
       :task/message s/Str
       :task/relates #{common/EID}
       :task/resolve-status s/Keyword
       :task/resolved-data s/Str
       :task/source s/Str
       :task/target common/EID
       :task/title s/Str
       :task/type s/Keyword}
      (st/assoc :common/related-entities #{s/Int})
      (st/assoc :common/team-members #{s/Int})
      (st/optional-keys)
      (st/required-keys [:task/assignee :task/message])))