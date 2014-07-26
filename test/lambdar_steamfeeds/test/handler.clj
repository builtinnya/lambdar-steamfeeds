; (C) Copyright 2014 Naoto Yokoyama.
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;

(ns lambdar-steamfeeds.test.handler
  (:require [clojure.test :refer :all]
            [lambdar-steamfeeds.handler :refer :all]
            [ring.mock.request :as mock]))

(deftest test-app
  (testing "invalid URL route"
    (let [response (app (mock/request :get "/?url=http://example.com"))]
      (is (= (:status response) 400))
      (is (= (:body response) "Invalid URL"))))

  (testing "invalid format route"
    (let [response (app (mock/request :get "/?format=xyzzy&url=http://store.steampowered.com/search/"))]
      (is (= (:status response) 400))
      (is (= (:body response) "Invalid Format"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
