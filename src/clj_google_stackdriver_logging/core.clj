(ns clj-google-stackdriver-logging.core
  (:require [clj-http.client :as http]
            [cemerick.url :refer [url url-encode]]
            [clojure.data.json :as json]
            [clj-google.auth :refer [*access-token*]]
            [clj-google-stackdriver-logging.body :refer [request-body]]))

(def ^:private stackdriver-logging-base-url "https://logging.googleapis.com")
(def ^:private stackdriver-logging-api-version "v1")

(defn- stackdriver-logging-url
  [api-method]
  (str stackdriver-logging-base-url "/" stackdriver-logging-api-version "/entries:" (name api-method)))

(defn- json-data
  [http-fn request-url data]
  (if-let [response (http-fn request-url data)]
    (let [json-response (json/read-str (:body response) :key-fn keyword)]
      json-response)))

(defn- make-data
  [resource log-entry]
  {:oauth-token  *access-token*
   :body         (json/json-str (request-body resource log-entry))
   :content-type :json
   :accept       :json})

(defn write-log
  [resource log-entry]
  (if-let [request-url (stackdriver-logging-url :write)]
    (let [data (make-data resource log-entry)]
      (if-let [response (http/post request-url data)]
        (= 200 (:status response))))))