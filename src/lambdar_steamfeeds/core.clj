; (C) Copyright 2014 Naoto Yokoyama.
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;

(ns lambdar-steamfeeds.core
  (:require [clojure.string :refer [trim]]
            [clojure.core.cache :as cache]
            [net.cgrand.enlive-html :as html]
            [net.cgrand.jsoup]
            [hiccup.core :refer :all]
            [clj-rss.core :as rss]
            [url-normalizer.core :as url]
            [cheshire.core :as json]))

(defn- parse-page [url]
  (html/html-resource (java.net.URL. url)
                      ;; For parsing <a><div>...</div></a>
                      {:parser net.cgrand.jsoup/parser}))

(defn- channel-title [node]
  (->> (html/select node [:h2.results_description])
       first
       html/text
       trim
       (str "Steam - ")))

(defn- search-results [node]
  (html/select node [:a.search_result_row]))

(defn- node->image
  "Takes the root node of a search result and returns the item's image."
  [node]
  (let [img-tag (first (html/select node [:div.search_capsule :> :img]))]
    (:attrs img-tag)))

(defn- node->item
  "Takes the root node of a search result and returns an extracted item."
  [node]
  (let [link     (-> node :attrs :href)
        title    (first (html/select node [:div.search_name :> :h4]))
        tags     (first (html/select node [:div.search_name :> :p]))
        price    (first (html/select node [:div.search_price]))
        texts    (map (comp trim html/text) [link title tags price])
        result   (zipmap [:link :title :tags :price] texts)]
    (assoc result
      :image (node->image node))))

(defn- item->date
  "Takes an extracted item and returns a Date object. Requires the tags line
  because an item's fully-specified release date may be contained only in the
  tags line."
  [item]
  (if-let [released (-> (re-find #"Released: (.+)$" (:tags item)) (get 1))]
    (java.util.Date. released)))

(defn- item->image
  "Takes an extracted item and returns an HTML snippet for the item's image."
  [item]
  (if-let [node (:image item)]
    (html [:img {:src    (:src node)
                 :alt    (:alt node)
                 :width  (:width node)
                 :height (:height node)}])))

(defn- wrap-cdata [& args]
  (str "<![CDATA[<html><body>" (apply str args) "</body></html>]]>"))

(defn- item->description
  "Takes an extracted item and constructs a content of description."
  [item]
  (wrap-cdata
   (html [:p (:price item) ", " (:tags item)]
         (if-let [image (item->image item)]
           [:p image]))))

(defn- item->rss-ready
  "Takes an extracted item and converts into an RSS-ready item."
  [item]
  {:title       (:title item)
   :link        (:link item)
   :pubDate     (item->date item)
   :description (item->description item)})

(defn valid-url?
  "Takes a URL and returns true if and only if it is a valid URL of Steam search
  results."
  [url]
  (and url (.startsWith url "http://store.steampowered.com/search")))

(def ^:private feed-cache (atom (cache/ttl-cache-factory {} :ttl 300000)))

(defn- canonicalize-url [url]
  (str (url/normalize url {:sort-query-keys? true})))

(defn generate-feed
  "Takes the URL of a Steam search results page and returns the feed as map.
  With an additional argument, it is used as the channel URL."
  ([src-url]
     (generate-feed src-url nil))
  ([src-url ch-url]
     (let [src-url (canonicalize-url src-url)]
       (if-let [cached (cache/lookup @feed-cache src-url)]
         cached
         (let [node  (parse-page src-url)
               title (channel-title node)
               items (map (comp item->rss-ready node->item)
                          (search-results node))
               feed {:title title :link ch-url :description title :items items}]
           (swap! feed-cache cache/miss src-url feed)
           feed)))))

(defn generate-rss
  "Takes the URL of a Steam search results page and returns the RSS feed.
  With an additional argument, it is used as the channel URL."
  ([src-url]
     (generate-rss src-url nil))
  ([src-url ch-url]
     (let [feed (generate-feed src-url ch-url)]
       (rss/channel-xml (select-keys feed [:title :link :description])
                        (:items feed)))))

(defn generate-json
  "Takes the URL of a Steam search results page and returns the feed as JSON.
  With an additional argument, it is used as the channel URL."
  ([src-url]
     (generate-json src-url nil))
  ([src-url ch-url]
     (json/generate-string (generate-feed src-url ch-url)
                           {:pretty true})))
