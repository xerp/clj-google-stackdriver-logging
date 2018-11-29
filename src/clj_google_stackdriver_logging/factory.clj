(ns clj-google-stackdriver-logging.factory
  (:require [clojure.string :as string]
            [clj-google.auth :refer [*access-token*]]
            [clojure.data.json :as json]
            [clj-google-stackdriver-logging.body :refer [request-body]]))

(def ^:private stackdriver-logging-base-url "https://logging.googleapis.com")
(def ^:private stackdriver-logging-api-version "v2")

(defn- stackdriver-logging-url
  [api-method]
  (string/join "/" [stackdriver-logging-base-url stackdriver-logging-api-version "entries:" (name api-method)]))

(defmulti make-log-name (fn [type _ _] (keyword type)))

(defmethod make-log-name
  :projects [type resource log-name]
  (let [project-id (-> resource :labels :project_id)
        log-name (name log-name)]
    (string/join "/" [(name type) project-id "logs" log-name])))


(defmulti make-resource (fn [type _] (keyword type)))

(defmethod make-resource
  :projects [_ project-id]
  {:type   "gce_project"
   :labels {:project_id project-id}})

(defmulti make-resource-name (fn [type _] (keyword type)))

(defmethod make-resource-name
  :projects [type project-id]
  (string/join "/" [(name type) project-id]))


(defn- make-data
  [data-type body-data]
  {:oauth-token  *access-token*
   :body         (json/json-str (request-body data-type body-data))
   :content-type :json
   :accept       :json})


(defn make-request
  [api-method request-type request-data]
  (if-let [request-url (stackdriver-logging-url api-method)]
    (let [data (make-data request-type request-data)]
      [request-url data])))


(defn make-log
  [resource log-name level data]
  {:severity                     (.toUpperCase (name level))
   :logName                      (make-log-name :project resource log-name)
   :resource                     resource
   (if (map? data) :jsonPayload
                   :textPayload) data})
