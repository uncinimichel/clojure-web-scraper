(defproject clojure-web-scraper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-time "0.13.0"]
                 [org.clojure/clojure "1.8.0"]
                 [http-kit "2.2.0"]
                 [org.clojure/data.json "0.2.6"]
                 [hickory "0.7.0"]
                 [org.clojure/core.async "0.2.395"]
                 [com.amazonaws/aws-lambda-java-core "1.1.0"]
                 [com.amazonaws/aws-lambda-java-events "1.3.0"]
                 [com.amazonaws/aws-lambda-java-log4j "1.0.0"]
                 [com.amazonaws/aws-java-sdk-s3 "1.11.125"]]

  :profiles {:uberjar {:aot :all}}
  :uberjar-name "lambda-cj-web-scrapper.jar")
