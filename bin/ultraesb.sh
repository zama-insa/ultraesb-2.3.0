#!/bin/sh

# Environment Variable Prerequisites
#
#   ULTRA_HOME     Home of the UltraESB installation. If not set will use the parent directory
#
#   JAVA_HOME      Must point at your Java Development Kit installation (i.e. > JDK 1.6.x)
#

#------------------- BEGIN STANDARD WRAPPER CODE ------------------------
# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
os400=false
mac=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
Darwin*) mac=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$ULTRA_HOME" ] && ULTRA_HOME=`cygpath --unix "$ULTRA_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi
#------------------- END STANDARD WRAPPER CODE ------------------------

# Only set ULTRA_HOME if not already set
[ -z "$ULTRA_HOME" ] && ULTRA_HOME=`cd "$PRGDIR/.." ; pwd`

if [ -z "$ULTRA_CONF" ]; then
    ULTRA_CONF="--confDir=$ULTRA_HOME/conf"
fi

if [ -z "$ULTRA_DATA" ]; then
    ULTRA_DATA=$ULTRA_HOME/data
fi

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable to a Java 1.6.x or higher JDK before running the UltraESB."
  exit 1
fi

# Check Java version
jdk_17=`$JAVA_HOME/bin/java -version 2>&1 | grep 1.7`

if [ -z "$jdk_17" ]; then
    echo "UltraESB is currently certified against the Java Development Kit version 1.7.x for production use. It may work well with later versions as well."
fi

# update classpath - add any patches first
ULTRA_CLASSPATH=$ULTRA_HOME/conf

if $mac; then
  	echo "adding os/x equivalent of tools.jar"
	ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$JAVA_HOME/../Classes/classes.jar
else
	ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$JAVA_HOME/lib/tools.jar
fi

ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$ULTRA_HOME/lib/patches:$ULTRA_HOME/conf/mediation/classes

for f in $ULTRA_HOME/lib/patches/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$ULTRA_HOME/lib
for f in $ULTRA_HOME/lib/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

for f in $ULTRA_HOME/lib/custom/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

for f in $ULTRA_HOME/lib/optional/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

for f in $ULTRA_HOME/lib/samples/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  ULTRA_HOME=`cygpath --absolute --windows "$ULTRA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi
# endorsed dir
ULTRA_ENDORSED=$ULTRA_HOME/lib/endorsed

# server name
SERVER_NAME=

# installation name
INSTALLATION_NAME=

# detect ulimit and set system property
ULTRA_OPTS=" -Dultra.ulimit=`ulimit -n`"

# ----- Uncomment the following line to enable the SSL debug options ----------
# ULTRA_OPTS="$ULTRA_OPTS -Djavax.net.debug=all"

# --- Uncomment the following line and set the IP address for remote JMX ------
#ULTRA_OPTS="$ULTRA_OPTS -Djava.rmi.server.hostname=<your-ip-address>"
JVM_OPTS="-Xms1024M -Xmx1024M"

while [ $# -ge 1 ]; do

if [ "$1" = "-xdebug" ]; then
    XDEBUG="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,address=8000"
    shift

  elif [ "$1" = "-sample" ]; then
    ULTRA_XML="--rootConf=$ULTRA_HOME/samples/conf/ultra-sample-$2.xml"
    if [ "$2" -ge 900 ] 2>/dev/null; then
        echo "Increasing heap memory to 2GB for performance test run..."
        JVM_OPTS="-Xms2048M -Xmx2048M"
    fi
    shift 2 # -sample and sample number

  elif [ "$1" = "-serverName" ]; then
    SERVER_NAME="--serverName=$2"
    shift 2 # -serverName and actual name

  elif [ "$1" = "-installationName" ]; then
    INSTALLATION_NAME="--installationName=$2"
    shift 2 # -installationName and actual name

elif [ "$1" = "-h" ]; then
    echo "Usage: ultraesb.sh ( commands ... )"
    echo "commands:"
    echo "  -xdebug                  Start UltraESB under JPDA debugger"
    echo "  -sample (number)         Start with sample UltraESB configuration of given number"
    echo "  -serverName <name>       Name of the UltraESB server instance"
    echo "  -installationName <name> Name of the installation of a set of related UltraESB server instances"
    shift
    exit 0

  else
    echo "Error: unknown command:$1"
    echo "For help: ultraesb.sh -h"
    shift
    exit 1
  fi

done

# ----- Execute The Requested Command -----------------------------------------

cd $ULTRA_HOME
echo "Starting AdroitLogic UltraESB ..."
echo "Using JAVA_HOME  : $JAVA_HOME"
echo "Using ULTRA_HOME: $ULTRA_HOME"

$JAVA_HOME/bin/java -server $JVM_OPTS \
    $XDEBUG \
    $ULTRA_OPTS \
    -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XMLGrammarCachingConfiguration \
    -Djava.endorsed.dirs=$ULTRA_ENDORSED \
    -Dehcache.disk.store.dir=$ULTRA_DATA \
    -Dorg.terracotta.quartz.skipUpdateCheck=true \
    -classpath $ULTRA_CLASSPATH \
    org.adroitlogic.ultraesb.UltraServer \
        $ULTRA_CONF $ULTRA_XML $SERVER_NAME $INSTALLATION_NAME