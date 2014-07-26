; (C) Copyright 2014 Naoto Yokoyama.
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;

(ns lambdar-steamfeeds.test.core
  (:require [clojure.test :refer :all]
            [lambdar-steamfeeds.core :refer :all]))

(deftest test-core
  (testing "URL checker"
    (is (= (valid-url? nil) nil))
    (is (= (valid-url? "xyzzy") false))
    (is (= (valid-url? "http://example.com/") false))
    (is (= (valid-url? "http://store.steampowered.com/search/?sort_by=Released") true))))
