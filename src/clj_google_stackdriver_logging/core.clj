(ns clj-google-stackdriver-logging.core
  (:require [clj-http.client :as http]
            [cemerick.url :refer [url url-encode]]
            [clojure.data.json :as json]
            [clj-google.auth :refer [*access-token*]]
            [clj-google-stackdriver-logging.body :refer [request-body]]))

(def ^:private stackdriver-logging-base-url "https://logging.googleapis.com")
(def ^:private stackdriver-logging-api-version "v2")

(defn- stackdriver-logging-url
  [api-method]
  (str stackdriver-logging-base-url "/" stackdriver-logging-api-version "/entries:" (name api-method)))

(defn- make-data
  [resource log-entry]
  {:oauth-token  *access-token*
   :body         (json/json-str (request-body resource log-entry))
   :content-type :json
   :accept       :json})

(defn- json-data
  [http-fn request-url data]
  (if-let [response (http-fn request-url data)]
    (let [json-response (json/read-str (:body response) :key-fn keyword)]
      json-response)))

(defn make-project-resource
  [project-id]
  {:type   "gce_project"
   :labels {:project_id project-id}})

(defn make-log
  [resource log-name level data]
  {:severity                     (.toUpperCase (name level))
   :resource                     resource
   :logName                      (str "projects/"
                                      (-> resource :labels :project_id)
                                      "/logs/"
                                      (name log-name))
   (if (map? data) :jsonPayload
                   :textPayload) data})


(defn write-log
  [resource log-entry]
  (if-let [request-url (stackdriver-logging-url :write)]
    (let [data (make-data resource log-entry)]
      (if-let [response (http/post request-url data)]
        (= 200 (response :status))))))