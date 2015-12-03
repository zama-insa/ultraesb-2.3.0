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
    ULTRA_CONF=$ULTRA_HOME/conf
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
    echo "UltraESB is currently certified against Java Development Kit version 1.7.x, although it may work well with future versions as well"
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

for f in $ULTRA_HOME/lib/test/*.jar
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

# ----- Uncomment the following line to enalbe the SSL debug options ----------
# ULTRA_OPTS="-Djavax.net.debug=all"

while [ $# -ge 1 ]; do

if [ "$1" = "-xdebug" ]; then
    XDEBUG="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,address=8000"
    shift

elif [ "$1" = "-jetty" ]; then
    COMMAND="-jetty"
    shift

elif [ "$1" = "-echo" ]; then
    COMMAND="-echo"
    shift

elif [ "$1" = "-h" ]; then
    echo "Usage: toolbox.sh ( commands ... )"
    echo "commands:"
    echo "  -xdebug            Start UltraESB ToolBox under JPDA debugger"
    shift
    exit 0

  else
    echo "Error: unknown command:$1"
    echo "For help: toolbox.sh -h"
    shift
    exit 1
  fi

done

# ----- Execute The Requested Command -----------------------------------------

cd $ULTRA_HOME
echo "Starting AdroitLogic UltraESB ToolBox ..."
echo "Using JAVA_HOME  : $JAVA_HOME"
echo "Using ULTRA_HOME: $ULTRA_HOME"

$JAVA_HOME/bin/java -server -Xms512M -Xmx512M \
    $XDEBUG \
    $ULTRA_OPTS \
    -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XMLGrammarCachingConfiguration \
    -Djava.endorsed.dirs=$ULTRA_ENDORSED \
    -Djava.io.tmpdir=$ULTRA_HOME/tmp \
    -classpath $ULTRA_CLASSPATH \
    org.adroitlogic.toolbox.ToolBoxApp $COMMAND