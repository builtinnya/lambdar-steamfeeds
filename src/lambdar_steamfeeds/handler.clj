; (C) Copyright 2014 Naoto Yokoyama.
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;

(ns lambdar-steamfeeds.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [content-type response status]]
            [lambdar-steamfeeds.core :refer :all]
            [lambdar-steamfeeds.util :refer [request-url]]))

(defn- serve-rss [src-url ch-url]
  (-> (generate-rss src-url ch-url)
      response
      (content-type "application/rss+xml; charset=utf-8")))

(defn- serve-json [src-url ch-url]
  (-> (generate-json src-url ch-url)
      response
      (content-type "application/json; charset=utf-8")))

(defn- error-response [body]
  (-> (response body)
      ;; Bad Request
      (status 400)))

(defn- invalid-format [format]
  (error-response "Invalid Format"))

(defn- invalid-url [url]
  (error-response "Invalid URL"))

(defroutes app-routes
  (GET "/" [url format :as r]
       (if (valid-url? url)
         (let [ch-url (request-url r)]
           (case format
             (nil "rss") (serve-rss url ch-url)
             "json"      (serve-json url ch-url)
             (invalid-format format)))
         (invalid-url url)))
  (route/not-found "Not Found"))

(def app
  (handler/api app-routes))
