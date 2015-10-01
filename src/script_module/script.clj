(ns script.script
  (:require [result.result :as r]
            [result.service :refer :all])
  (:import javax.script.ScriptEngineManager))

(def *manager* (atom nil))

(def *engines* (atom {}))

(def *base-path* (atom ""))

(defaction script-action
  "engine-name|script-file(|args)*
   Starts up a JRS223 script engine, and runs the specified script.
   Scripts pass if they do not throw an exception."
  [identifier _ _ engine-name script & args]
  (let [engine (or (get @*engines* engine-name)
                   (swap! *engines*
                          (fn [m] (merge m [engine-name
                                           (.getEngineByName
                                            @*manager*
                                            engine-name)]))))
        bindings (.createBindings engine)
        _ (.put bindings "args" args)
        execution (future (.eval @engine
                                 (reader (clojure.java.io/file
                                          @*base-path*
                                          script))
                                 bindings))]

    ))

(defn script-service [conf]
  (reify Service
    (initialize [_] (swap! *manager* (ScriptEngineManager.)))
    (service-name [_] "script")
    (services [_] {"run" script-action})
    (close [_] nil)))
