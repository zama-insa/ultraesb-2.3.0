This directory is to facilitate the deployment of the UltraESB onto a JEE application server.

** NOTE: **
The UltraESB uses its high performance HTTP/S transports using Java NIO, Zero-Copy and Memory mapped file IO and thus
a JEE application server deployment will yield nothing in terms of HTTP/S performance via the use of an Application
Server Servlet container.

Deployment on a JEE server is only meaningful when JTA support of an Application Server is being used (e.g. to tap into
JTA transaction suspend/resume support over different threads - e.g. JTA Specification sec 3.2.3 - which may not be
supported with non-JEE application server transaction managers)

Thus for JTA transactional reasons or for operational deployment reasons, if the UltraESB is expected to be deployed
over an existing Application Server, deploy this exploded WAR directory as applicable.

Note:
1. In the following examples, replace <ULTRA_HOME> with the absolute path to your UltraESB installation root directory
2. Any application server containers will need to run with a JDK 1.6.x version

Deployment on Tomcat
--------------------
1. Create a file <TOMCAT_HOME>/conf/Catalina/localhost/ultraesb.xml with the following line of text
<Context path="/ultraesb" docBase="<ULTRA_HOME>/webapp"  reloadable="true" privileged="true"/>

2. Copy all JAR files from <ULTRA_HOME>/lib [and its sub-directories if/as necessary] into the
<ULTRA_HOME>webapp/WEB-INF/lib except for the servlet-api-*.jar file and lib/endorsed/*.jar files

JBoss deployment (JBoss AS 5.1.0 GA)
------------------------------------
1. Set a system property 'ultra.home' to point to the ULTRA_HOME root directory.
You could set this on the jboss-5.1.0.GA/bin/run.sh file by adding the following line just before the
"JBoss Bootstrap Environment" section.

Note. The 'ultra.home' system property is only used to locate the classpath required to dynamically compile any
sequences - if Java source/fragment or JSR 233 script sequences are used.

JAVA_OPTS="$JAVA_OPTS -Dultra.home=<ULTRA_HOME>"

2. Copy all JAR files from <ULTRA_HOME>/lib [and its sub-directories if/as necessary] into the
<ULTRA_HOME>webapp/WEB-INF/lib except for the servlet-api-*.jar file and lib/endorsed/*.jar files

3. Copy <ULTRA_HOME>/webapp folder into the JBoss deploy directory
e.g. As jboss-5.1.0.GA/server/default/deploy/ultraesb.war

Other Application Servers
-------------------------
Please request for support from http://adroitlogic.org
