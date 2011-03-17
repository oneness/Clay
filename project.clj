(defproject clay "0.1.0"
  :description "Simple Web App Skeleton for Clojure"
  :dependencies [[hiccup "0.3.1"]
		 [ring "0.3.5"]
		 [org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [net.cgrand/moustache "1.0.0-SNAPSHOT"]
		 [enlive "1.0.0-SNAPSHOT"]
		 [com.lowagie/itext "2.0.8"]
		 [org.ccil.cowan.tagsoup/tagsoup "1.2"]
		 [org.xhtmlrenderer/core-renderer "R8"]]
  :repositories {"atlassian-contrib" "https://maven.atlassian.com/contrib/"
		 "snapshots" "http://mvn.getwoven.com/repos/woven-public-snapshots"
                  "releases" "http://mvn.getwoven.com/repos/woven-public-releases"
		 "maven2-repository.dev.java.net" "http://download.java.net/maven/2/"}
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]
		     [midje "1.1-alpha-1"]])
