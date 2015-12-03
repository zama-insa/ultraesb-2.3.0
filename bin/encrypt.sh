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

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable to Java 1.6.x or later before running the UltraESB encryption tool."
  exit 1
fi

ULTRA_CLASSPATH=$ULTRA_HOME/lib/patches

for f in $ULTRA_HOME/lib/patches/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

for f in $ULTRA_HOME/lib/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

for f in $ULTRA_HOME/lib/optional/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$ULTRA_HOME/uconsole/WEB-INF/classes
for f in $ULTRA_HOME/uconsole/WEB-INF/lib/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  ULTRA_HOME=`cygpath --absolute --windows "$ULTRA_HOME"`
  ULTRA_CLASSPATH=`cygpath --path --windows "$ULTRA_CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi
# endorsed dir
ULTRA_ENDORSED=$ULTRA_HOME/lib/endorsed

MAINCLASS="org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI encrypt.sh"
COMMAND=''

while [ $# -ge 1 ]; do

  if [ "$1" = "-uconsole" ]; then
    MAINCLASS="org.adroitlogic.ultraesb.admin.util.PasswordCipherTool"
  else 
    if [ "$1" = "-secure" ]; then
        MAINCLASS="org.adroitlogic.ultraesb.util.encrypt.SecurePropertyManager"
    else
        if [ "$COMMAND" = "" ]; then
            COMMAND=$1;
        else
            COMMAND="$COMMAND $1"
        fi
    fi
  fi
  shift

done

java -classpath $ULTRA_CLASSPATH $MAINCLASS $COMMAND $@