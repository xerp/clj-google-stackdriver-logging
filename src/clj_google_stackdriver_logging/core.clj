(ns clj-google-stackdriver-logging.core
  (:require [clj-http.client :as http]
            [cemerick.url :refer [url url-encode]]
            [clojure.data.json :as json]
            [clj-google-stackdriver-logging.factory :refer [make-request]]))


(defn- json-data
  [http-fn request-url data]
  (if-let [response (http-fn request-url data)]
    (let [json-response (json/read-str (:body response) :key-fn keyword)]
      json-response)))

(defn write-log
  [resource log-entry]
  (if-let [[request-url data] (make-request :write :write-log [resource log-entry])]
    (if-let [response (http/post request-url data)]
      (= 200 (response :status)))))

(defn list-logs
  [resource-names & {:keys [filter order-by page-size page-token]
                     :or   {filter [] order-by :asc page-size 50 page-token ""}}]
  (if-let [[request-url data] (make-request :list :list-logs
                                            [resource-names filter order-by page-size page-token])]
    (if-let [json-response (json-data http/post request-url data)]
      json-response)))

