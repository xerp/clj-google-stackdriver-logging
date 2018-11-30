(ns clj-google-stackdriver-logging.body
  (:require [clojure.string :as string]))

(def ^:private order-by-field :timestamp)

(defmacro assoc-if-not
  [not-check-fn dict key value]
  `(if (not (~not-check-fn ~value))
     (assoc ~dict ~key ~value)
     ~dict))

(defn assoc-if-not-blank
  [dict key value]
  (assoc-if-not string/blank? dict key value))

(defn assoc-if-not-nil
  [dict key value]
  (assoc-if-not nil? dict key value))

(defn- stringify-filter
  [filter]
  (if (empty? filter)
    ""
    (let [fields (map (fn [[field value]]
                        (string/join "=" (map name [field value]))) filter)]
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
                      :orderBy       order-by}
        body (assoc-if-not-nil default-body :pageSize page-size)
        body (assoc-if-not-blank body :filter filter)
        body (assoc-if-not-blank body :pageToken page-token)]
    body))