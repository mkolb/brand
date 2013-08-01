(ns brand.core
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.middleware.stacktrace :as trace]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.basic-authentication :as basic]
            [cemerick.drawbridge :as drawbridge]
            [cheshire.core :refer :all]
            [incanter.core :as incanter]
            [incanter.stats :as stats]
            [environ.core :refer [env]]))

(def db (vector
         ;; example1
         (hash-map
          :name "Fish Eye Cat Dog, for all your worst mashup needs"
          :url "http://fisheye-catdog.example.com"
          :headerrgb "#B40D10"
          :logoxone "http://fisheye-catdog.example.com/logo.png"
          :logoxtwo "http://fisheye-catdog.example.com/logo@2x.png")

         ;; example2
         (hash-map
          :name "Larry Livermore Lives!"
          :url "http://larry-livermore.example.com"
          :headerrgb "#A80300"
          :logoxone "http://larry-livermore.example.com/logo.png"
          :logoxtwo "http://larry-livermore.example.com/logo@2x.png")

))

(defn normalize-data [s]
  (clojure.string/replace s #"(?:http[s]?://)?([a-z0-9.\-]+)/?.*" "$1"))

(defn fuzz [m n]
  (let [p (normalize-data m)
        q (normalize-data n)
        dl (/ (stats/damerau-levenshtein-distance p q) (/ (+ (count p) (count q)) 2.0))]
    (if (> (/ 1 8.9) dl) ; HACK
      dl
      nil)))

(defn lookup [url db]
  (if (= 0 (count db))
    nil
    (if (fuzz (:url (first db)) url)
      (first db)
      (lookup url (rest db)))))


(defn- authenticated? [user pass]
  (= [user pass] [(env :repl-user false) (env :repl-password false)]))

(def ^:private drawbridge
  (-> (drawbridge/ring-handler)
      (session/wrap-session)
      (basic/wrap-basic-authentication authenticated?)))

(defn brand [site]
  (generate-string (lookup site db)))

(defroutes app
  (ANY "/repl" {:as req}
       (drawbridge req))
  (GET "/" []
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body (pr-str "Branding Service")})
  (GET "/site/:site" [site] (brand site))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))
  

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))
        store (cookie/cookie-store {:key (env :session-secret)})]
    (jetty/run-jetty (-> #'app
                         ((if (env :production)
                            wrap-error-page
                            trace/wrap-stacktrace))
                         (site {:session {:store store}}))
                     {:port port :join? false})))
