(ns theproject.core
  (:gen-class)
  (:require [cheshire.core :refer :all]))   
;Reading of the resource directory:  
;(defn read-json-data [archivepath] (parse-string (slurp archivepath) true))
;(def datareceived (vec (read-json-data "resource/teste.json")))

(defn get-agents [coll] (filter #(:new_agent %) coll))
(defn get-jobs [coll] (filter #(:new_job %) coll))
(defn get-jobs-requested [coll] (filter #(:job_request %) coll))
(defn get-jobs-unu [coll boll] (filter #(= (:urgent (:new_job %)) boll) coll))

(defn select-available-agent [coll_jobs_requested coll_agents agents_list] 
  (cond 
    (or (empty? coll_jobs_requested) (empty? coll_agents)) 
      agents_list
    (= (:agent_id (:job_request (first coll_jobs_requested))) (:id (:new_agent (first coll_agents)))) 
      (recur (rest coll_jobs_requested) (rest coll_agents) (conj agents_list (first coll_agents))) 
    :else 
      (recur coll_jobs_requested (rest coll_agents) agents_list)))

(defn have-skill? [skill agt_skilllist] (some? (some #{skill} agt_skilllist)))        
(defn mountjob_assigned [job agent] (assoc-in {} [:job_assigned] {:job_id (:id (:new_job job)) :agent_id (:id (:new_agent agent))}))
(defn remove-job [job joblist] (remove #(= job %) joblist))
(defn filter-job [agt jobs skilllv] (filter #(have-skill? (:type (:new_job  %)) (skilllv (:new_agent  agt))) jobs))
;asdf
(defn dequeue [coll_jobs coll_agts]
  (loop [jobs coll_jobs agts coll_agts result []]
    (if (or (empty? jobs) (empty? agts)) 
      result
      (let [primary (filter-job (first agts) jobs :primary_skillset) secondary (filter-job (first agts) jobs :secondary_skillset)] 
        (cond 
          (= (empty? primary) false) 
            (recur (remove-job (first primary) jobs) (rest agts) (conj result (mountjob_assigned (first primary) (first agts))))
          (and (= (empty? primary) true) (= (empty? secondary) false))
            (recur (remove-job (first secondary) jobs) (rest agts) (conj result (mountjob_assigned (first secondary) (first agts))))
          :else
            (recur jobs (rest agts) result))))))

(defn -main
  [& args] 
  (def datareceived (parse-string (slurp *in*) true))
  (def jobs (into (get-jobs-unu datareceived false) (get-jobs-unu datareceived true)))
  (def agents (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) []))
  (def result (dequeue jobs agents))
  (println (generate-string result))
  (generate-stream result (clojure.java.io/writer "/Users/josevictorpereiracosta/Documents/secret/jobqueue/resource/result.json")))