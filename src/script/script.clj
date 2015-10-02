(ns script.script
  (:require [result.result :as r]
            [result.service :refer :all])
  (:import javax.script.ScriptEngineManager))

(def manager (atom nil))

(def engines (atom {}))

(def base-paths (atom {}))

(def max-wait (atom 30000))

(defaction script-action
  "engine-name|script-file(|args)*
   Starts up a JRS223 script engine, and runs the specified script.
   Scripts pass if they do not throw an exception."
  [identifier _ _ engine-name script & args]
  (let [engine (or (get @engines engine-name)
                   (get (swap! engines
                               (fn [m] (merge m [engine-name
                                                (.getEngineByName
                                                 @manager
                                                 engine-name)])))
                        engine-name))
        bindings (.createBindings engine)
        _ (.put bindings "args" args)
        execution (future (try
                            (.eval engine
                                   (clojure.java.io/reader (clojure.java.io/file
                                                            (get @base-paths engine-name)
                                                            script))
                                   bindings)
                            (catch Exception e e)))
        completed (deref execution @max-wait (Exception. (str "Did not complete within "
                                                              (/ @max-wait 1000)
                                                              " seconds.")))]
    (if (not (future-done? execution)) (future-cancel execution))
    (if (instance? Exception completed)
      (reify r/ResultProtocol
        (r/identifier [_] identifier)
        (r/expected [_] "Runs to completion")
        (r/result? [_] false)
        (r/actual [_] (str completed))
        (r/failures [_]
          (let [string-writer (java.io.StringWriter.)
                print-writer (java.io.PrintWriter. string-writer)]
            (.printStackTrace completed print-writer)
            (.close print-writer)
            (clojure.string/split (.toString string-writer) #"\n"))))
      (reify r/ResultProtocol
        (r/identifier [_] identifier)
        (r/expected [_] "Runs to completion")
        (r/result? [_] true)
        (r/actual [_] (str completed))
        (r/failures [_] nil)))))

(defn script-service [conf]
  (reify Service
    (initialize [_]
      (swap! manager (constantly (ScriptEngineManager.)))
      (if (not (nil? (get conf "max-wait")))
        (swap! max-wait (constantly (* 1000 (int (get conf "max-wait"))))))
      (if (not (nil? (get conf "base-paths")))
        (swap! base-paths (constantly (into {} (get conf "base-paths"))))))
    (service-name [_] "script")
    (services [_] {"run" script-action})
    (close [_] nil)))
