(ns theproject.core
  (:gen-class)
  (:require [cheshire.core :refer :all])
  (:require [datomic.api :as d]))

(defn read-json-data [archivepath] 
  (parse-string (slurp archivepath) true))

(def datareceived (vec (read-json-data "resource/teste.json")))

(defn get-agents [coll] (filter #(:new_agent %) coll))
(defn get-jobs [coll] (filter #(:new_job %) coll))
(defn get-jobs-requested [coll] (filter #(:job_request %) coll))

(defn get-jobs-unu [coll boll] 
  (filter #(= (:urgent (:new_job %)) boll) coll))

(defn select-available-agent [coll_jobs_requested coll_agents agents_list] 
  (cond 
    (or (empty? coll_jobs_requested) (empty? coll_agents)) 
      agents_list
    (= (str (:agent_id (:job_request (first coll_jobs_requested)))) (:id (:new_agent (first coll_agents)))) 
      (do
        (println (= (:agent_id (:job_request (first coll_jobs_requested)) (:id (:new_agent (first coll_agents))))))
        (println " entrou" (:agent_id (:job_request (first coll_jobs_requested))))
        (println "segundo" (:id (:new_agent (first coll_agents))))
        (recur (rest coll_jobs_requested) (rest coll_agents) (conj agents_list (first coll_agents)))
        )
       
    :else 
      (recur coll_jobs_requested (rest coll_agents) agents_list)))

(def jobs (into (get-jobs-unu datareceived false) (get-jobs-unu datareceived true)))
(def agents (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) []))

(defn have-skill? [skill agt-skilllist] 
  (some? (some #{skill} agt-skilllist)))

(defn filter-agent[job agents skilllv] 
  (filter #(have-skill? (:type (:new_job  job)) (skilllv (:new_agent  %))) agents))    

(defn remove-agent [agent agentlist] 
  (remove #(= agent %) agentlist))    

(defn mountjob_assigned [job agent] 
  (assoc-in {} [:job_assigned] 
    {:job_id (:id (:new_job job)) :agent_id (:id (:new_agent agent))}))

(defn dequeue [coll_jobs coll_agts]
  (loop [jobs coll_jobs agts coll_agts result []]
    (if (or (empty? jobs) (empty? agts)) 
      result
      (let [primary (filter-agent (first jobs) agts :primary_skillset) secondary (filter-agent (first jobs) agts :secondary_skillset)] 
        (cond 
          (not (empty? primary)) 
            (recur (rest jobs) (remove-agent (first primary) agts) (conj result (mountjob_assigned (first jobs) (first primary))))
          (and (empty? primary) (not (empty? primary)))
            (recur (rest jobs) (remove-agent (first secondary) agts) (conj result (mountjob_assigned (first jobs) (first primary))))
          :else
            (recur (rest jobs)  agts result))))))

(println (dequeue jobs agents))
(defn -main
  [& args] 

  ; (let [in (slurp *in*)]
  ;   (println (parse-string in)))

  ;; Reciving data, done!
  ;(def data-readed (parse-string (slurp *in*)))
)
;;Don't use concat https://stuartsierra.com/2015/04/26/clojure-donts-concat


