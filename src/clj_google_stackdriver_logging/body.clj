(ns clj-google-stackdriver-logging.body
  (:require [clojure.string :as string]))

(def ^:private order-by-field :timestamp)

(defn assoc-if-not-blank
  [dict key value]
  (if (not (string/blank? value))
    (assoc dict key value)
    dict))

(defn- stringify-filter
  [filter]
  (if (empty? filter)
    ""
    (let [fields (map (fn [field]
                        (string/join "=" (map name field))) filter)]
      (string/join " " fields))))

(defmulti request-body (fn [type _] (keyword type)))

(defmethod request-body
  :write-log [_ data]
  (let [[resource log-entry] data
        log-entry (if (vector? log-entry) log-entry [log-entry])]
    {:resource resource
     :entries  log-entry}))


(defmethod request-body
  :list-logs [_ data]
  (let [[resource-names filter order-by page-size page-token] data
        order-by (string/join " " (map name [order-by-field order-by]))
        filter (stringify-filter filter)
        default-body {:resourceNames resource-names
                      :orderBy       order-by
                      :pageSize      page-size}
        body (assoc-if-not-blank default-body :filter filter)
        body (assoc-if-not-blank body :pageToken page-token)]
    body))