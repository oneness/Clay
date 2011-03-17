(ns clay.core
  (:use net.cgrand.moustache
	ring.adapter.jetty
	ring.util.response
	ring.middleware.file
	ring.middleware.params
	ring.middleware.stacktrace
	net.cgrand.enlive-html
	[hiccup.core :only [html]])
  (:import (java.net URL InetAddress NetworkInterface)
	   (java.io File FileOutputStream OutputStreamWriter
		    BufferedReader InputStreamReader)
	   (org.ccil.cowan.tagsoup Parser XMLWriter)
	   (org.xhtmlrenderer.pdf ITextRenderer)
	   (com.lowagie.text DocumentException)))

;;Static Data and Utils
(def colon ":")
(def slash "/")
(def port "8080")
(def http "http")
(def pdf-dir "pdfs/")
(def static-dir "src/clay/resources/")
(def page-not-found "Oops! Page not found. ")
(def file-ready-msg "Your file is ready. Click here to download!")

;;Helper functions
(defn href-text
  [href text]
  (html [:a {:href href} text]))

(defn local-addresses []
  (->> (NetworkInterface/getNetworkInterfaces)
       enumeration-seq
       (map bean)
       (filter (complement :loopback))
       (mapcat :interfaceAddresses)
       (map #(.. % (getAddress) (getHostAddress)))))

(def ip
     (last (local-addresses)))

(def href-home (str http colon slash slash ip colon port slash))
(def href-pdf (str href-home pdf-dir))

(defn pad-protocol
  [webaddr protocol]
  (let [exists? (re-find protocol webaddr)]
    (if exists?
      webaddr
      (str protocol webaddr))))

;; Business Logic
(defn url-to-xmlstr
  [url]
  (let [sw (java.io.StringWriter.)
	xw (XMLWriter. sw)
	parser (Parser.)]
    (doto parser
      (.setContentHandler xw)
      (.parse url))
    (.toString sw)))

(defn url-to-pdf
  [url pdf-name]
  (let [xmlstr (url-to-xmlstr url)
	renderer (ITextRenderer.)
	os (FileOutputStream. pdf-name)]
    (doto renderer
	(.setDocumentFromString xmlstr) (.layout) (.createPDF os))
    (.close os)))

(defn mk-name-url
  [url]
  (let [url (URL. url)
	name (str (.getHost url) (.getFile url))]
    (-> name (.trim)
	(.replaceAll "\\." "-")
	(.replaceAll "/" "-"))))

;;Templates
(def index-template "clay/resources/index.html")

(deftemplate content-with index-template
  [c]
  [:div#file](content c))

;;Routes dispatch
(defn multi-dispatcher
  [params]
  (let [action (params "action")]
    (keyword action)))

(defmulti clay-controller multi-dispatcher)

(defmethod clay-controller :topdf
  [params]
  (let [url-str (params "url")
	purl (pad-protocol url-str #"http://")
	pdf-name (str (mk-name-url purl) ".pdf")
	pdf-on-sever (str static-dir pdf-dir pdf-name)]
    (url-to-pdf purl pdf-on-sever)
    (response (href-text (str href-pdf pdf-name) file-ready-msg))))

(defmethod clay-controller :totext
  [params]
  (response "This feature is coming... Stay tuned or you can implement it now"))

(defn clay-dispatch
  [{params :params}]
  (clay-controller params))

;;Clay routes
(def clay-handlers
     (app ["convert"] clay-dispatch
	  [&] (response (content-with page-not-found))))

;;Middlewares
(def clay-app
     (-> #'clay-handlers
	 (wrap-params)
	 (wrap-stacktrace)
	 (wrap-file static-dir)))

;;Dev helper functions
(defonce server
  (run-jetty clay-app {:port (Integer/parseInt port) :join? false}))