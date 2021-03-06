(ns serverless.functions
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [<!]]
            [promesa.core :as p]))


(nodejs/enable-util-print!)
(defonce moment (nodejs/require "moment"))
(defonce mongoose (nodejs/require "mongoose"))

(defn hello [_event ctx cb]
  (println ctx)
  (cb nil (clj->js
           {:statusCode 200
            :headers    {"Content-Type" "text/html"}
            :body       "<h1>Hello, World!</h1>"})))


(defn now [_event ctx cb]
  (println ctx)
  (cb nil (clj->js
           {:statusCode 200
            :headers    {"Content-Type" "text/html"}
            :body       (str "<h1>" (.format (moment.) "LLLL") "</h1>")}))) ; call nodejs package

(defn example [_event _ctx cb]
  (println "happening! example")
  (let [connection (. mongoose connect "mongodb://127.0.0.1:27017/local")
        thenny (p/thenable? connection)]
    (prn (str " thenable? " thenny))
    (prn (str "connection is ", connection))

  ; Trying to use core.async channels...
  ; (go (let [connection (<! (. mongoose connect "mongodb://127.0.0.1:27017/local"))]
            ; the-glorious-data  (->> response
                                    ; (:body)
                                    ; (clj->js)
                                    ; (.stringify js/JSON))]

    (cb nil (clj->js
             {:statusCode 200
              :headers    {"Content-Type" "application/json"}
              :body       "ok"}))))
; )

(set! (.-exports js/module) #js
                             {:hello hello
                              :now now
                              :example example})