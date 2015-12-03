#/bin/sh

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

# Only set RUN_DIR if not already set
[ -z "$RUN_DIR" ] && RUN_DIR=`cd "$PRGDIR/../.." ; pwd`

RECREATION_PROPS=$RUN_DIR/samples/bin/sec_req_recreation.properties

ULTRA_CLASSPATH=$RUN_DIR/lib/patches

for f in $RUN_DIR/lib/patches/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

for f in $RUN_DIR/lib/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

for f in $RUN_DIR/lib/optional/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

for f in $RUN_DIR/lib/endorsed/*.jar
do
  ULTRA_CLASSPATH=$ULTRA_CLASSPATH:$f
done

while [ $# -ge 1 ]; do

  if [ "$1" = "-props" ]; then
    RECREATION_PROPS=$2
  fi

done

cd $RUN_DIR
java -classpath $ULTRA_CLASSPATH samples.client.SecureRequestGenerator $RECREATION_PROPS