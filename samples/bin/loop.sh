#/bin/sh

#------------------- BEGIN STANDARD WRAPPER CODE ------------------------
# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
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
[ -z "$ULTRA_HOME" ] && ULTRA_HOME=`cd "$PRGDIR/../.." ; pwd`

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable to a Java 1.6.x or higher JDK before running the UltraESB."
  exit 1
fi

for f in $ULTRA_HOME/lib/patches/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$ULTRA_HOME/lib
for f in $ULTRA_HOME/lib/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$ULTRA_HOME/lib
for f in $ULTRA_HOME/lib/samples/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

count=$1
shift

file=$1
shift
n=$1
shift
c=$1
shift
action=$1
shift
url=$1
shift

i=0
while [ $i != $count ]; do
  echo "Running iteration $i"
  java -Xms1024M -Xmx1024M -server -XX:+UseParallelGC -cp $ULTRA_CLASSPATH org.adroitlogic.toolbox.javabench.JavaBench -p $file -n $n -c $c -k -t 180000 -H "SOAPAction: \"$action\"","routing: xadmin;server1;community#1.0##" -T "text/xml; charset=UTF-8" $url
  i=`expr $i + 1`
done


