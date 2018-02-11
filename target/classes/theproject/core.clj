(ns theproject.core
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [cheshire.core :refer :all])
)

;;hard way
;;  (use 'clojure.java.io)
;;  (with-open [wrtr (writer "/tmp/test.json")]
;;     (.write wrtr (json/write-str {:key1 "val1" :key2 "val2"})))
;;lein dep && lein compile

;;> (spit "/tmp/test.json" (json/write-str {:key1 "val1" :key2 "val2"}))

;;(defn read-json [path] (json/read-str (read-archive path)) :value-fn keyword)
;;(map #(cheshire/parse-string % true)
;;(line-seq (clojure.java.io/reader "data/json_data")))

(def archivepath "/Users/josevictorpereiracosta/Documents/secret/jobqueue/resource/sample-input.json")

(defn read-archive [path] (slurp path))

(defn read-json-data [archivepath] (parse-string (read-archive archivepath) true)) 

;;(map #(parse-string % true)
;;     (line-seq (clojure.java.io/reader "/Users/josevictorpereiracosta/Documents/secret/jobqueue/resource/jason_data.json")))

;;(defn teste [archivepath] (parse-string (slurp archivepath) true))

(println (read-json-data archivepath))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

