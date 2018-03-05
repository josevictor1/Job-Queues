(def teste [1 2 3 4 5])

(def teste1 [6 7 8 9 10])

(defn withloop [coll coll1] 
    (loop [thelist coll i coll1]
        (if (empty? i) 
            thelist
            (recur (conj thelist (first i)) (rest i)))))

(withloop teste teste1)

(def teste [{:elemento {:id 1 :conteudo "teste" }} {:elemento {:id 2 :conteudo "test" }}{:elemento {:id 3 :conteudo "tes" }}{:elemento {:id 4 :conteudo "te" }}])


(defn remove-elemn [coll elem] 
    (remove #(= elem %) coll))

(println (remove-elemn teste1 6
    ))