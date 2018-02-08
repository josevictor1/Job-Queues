(ns theproject.core
  (:gen-class)
  (:require [clojure.data.json :as json])
)

;;hard way
;;  (use 'clojure.java.io)
;;  (with-open [wrtr (writer "/tmp/test.json")]
;;     (.write wrtr (json/write-str {:key1 "val1" :key2 "val2"})))


;;> (spit "/tmp/test.json" (json/write-str {:key1 "val1" :key2 "val2"}))

(defn string-keys-to-symbols [map]
  (reduce #(assoc %1 (-> (key %2) keyword) (val %2)) {} map))




  


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

