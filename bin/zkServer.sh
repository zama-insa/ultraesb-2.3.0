#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
# If this scripted is run out of /usr/bin or some other system bin directory
# it should be linked to and not copied. Things like java jar files are found
# relative to the canonical path of this script.
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

REL_PIDFILE=zookeeper_server.pid
while [ $# -gt 0 ] ; do
    case "$1" in
        '-piddir')
            shift
            PIDDIR=$1
            shift
            ;;
        '-pidfile')
            shift
            REL_PIDFILE=$1
            shift
            ;;
        '-configdir')
            shift
            ZOOCFGDIR=$1
            shift
            ;;
        '-configfile')
            shift
            ZOOCFG=$1
            shift
            ;;
        '-logdir')
            shift
            ZOO_LOG_DIR=$1
            shift
            ;;
        *)
            if [ "X$COMMAND" = "X" ]; then
                COMMAND=$1
            else
                COMMAND="$COMMAND $1"
            fi
            shift
            ;;
    esac
done
set -- $COMMAND

if [ "X$ZOOCFGDIR" != "X" ]; then
    case $ZOOCFGDIR in
        /*)
            ;;
        *)
            ZOOCFGDIR=$ULTRA_HOME/$ZOOCFGDIR
            ;;
    esac
fi

# See the following page for extensive details on setting
# up the JVM to accept JMX remote management:
# http://java.sun.com/javase/6/docs/technotes/guides/management/agent.html
# by default we allow local JMX connections
if [ "x$JMXLOCALONLY" = "x" ]
then
    JMXLOCALONLY=false
fi

if [ "x$JMXDISABLE" = "x" ]
then
    echo "JMX enabled by default" >&2
    # for some reason these two options are necessary on jdk6 on Ubuntu
    #   accord to the docs they are not necessary, but otw jconsole cannot
    #   do a local attach
    ZOOMAIN="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=$JMXLOCALONLY org.apache.zookeeper.server.quorum.QuorumPeerMain"
else
    echo "JMX disabled by user request" >&2
    ZOOMAIN="org.apache.zookeeper.server.quorum.QuorumPeerMain"
fi

# use POSTIX interface, symlink is followed automatically
ZOOBIN="${BASH_SOURCE-$0}"
ZOOBIN=`dirname ${ZOOBIN}`
ZOOBINDIR=`cd ${ZOOBIN}; pwd`

if [ -e "$ZOOBIN/../libexec/zkEnv.sh" ]; then
  . "$ZOOBINDIR"/../libexec/zkEnv.sh
else
  . "$ZOOBINDIR"/zkEnv.sh
fi

if [ "x$SERVER_JVMFLAGS"  != "x" ]
then
    JVMFLAGS="$SERVER_JVMFLAGS $JVMFLAGS"
fi

if [ "x$2" != "x" ]
then
    ZOOCFG="$ZOOCFGDIR/$2"
fi

# if we give a more complicated path to the config, don't screw around in $ZOOCFGDIR
if [ "x`dirname $ZOOCFG`" != "x$ZOOCFGDIR" ]
then
    ZOOCFG="$2"
fi

if $cygwin
then
    ZOOCFG=`cygpath -wp "$ZOOCFG"`
    # cygwin has a "kill" in the shell itself, gets confused
    KILL=/bin/kill
else
    KILL=kill
fi

echo "Using config: $ZOOCFG" >&2

if [ -z $ZOOPIDFILE ]; then
    if [ -z $PIDDIR ]; then
        ZOO_DATADIR=$(grep "^[[:space:]]*dataDir" "$ZOOCFG" | sed -e 's/.*=//')

        case $ZOO_DATADIR in
            /*)
                ;;
            *)
                ZOO_DATADIR=$ULTRA_HOME/$ZOO_DATADIR
                ;;
        esac

        if [ ! -d "$ZOO_DATADIR" ]; then
            mkdir -p "$ZOO_DATADIR"
        fi
        ZOOPIDFILE="$ZOO_DATADIR/$REL_PIDFILE"
    else
        ZOOPIDFILE="$PIDDIR/$REL_PIDFILE"
    fi
else
    # ensure it exists, otw stop will fail
    mkdir -p $(dirname "$ZOOPIDFILE")
fi

if [ ! -w "$ZOO_LOG_DIR" ] ; then
mkdir -p "$ZOO_LOG_DIR"
fi

_ZOO_DAEMON_OUT="$ZOO_LOG_DIR/zk-server.out"

cd $ULTRA_HOME

case $1 in
start)
    echo  -n "Starting zookeeper ... "
    if [ -f $ZOOPIDFILE ]; then
      if kill -0 `cat $ZOOPIDFILE` > /dev/null 2>&1; then
         echo $command already running as process `cat $ZOOPIDFILE`. 
         exit 0
      fi
    fi
    nohup $JAVA "-Dlog4j.configuration=file://$ZOOCFGDIR/log4j_zk.properties" "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.logger=${ZOO_LOG4J_PROP}" \
    -cp "$CLASSPATH" $JVMFLAGS $ZOOMAIN "$ZOOCFG" > "$_ZOO_DAEMON_OUT" 2>&1 < /dev/null &
    if [ $? -eq 0 ]
    then
      if /bin/echo -n $! > "$ZOOPIDFILE"
      then
        sleep 1
        echo STARTED
      else
        echo FAILED TO WRITE PID
        exit 1
      fi
    else
      echo SERVER DID NOT START
      exit 1
    fi
    ;;
start-foreground)
    ZOO_CMD="exec $JAVA"
    if [ "${ZOO_NOEXEC}" != "" ]; then
      ZOO_CMD="$JAVA"
    fi
    $ZOO_CMD "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.logger=${ZOO_LOG4J_PROP}" \
    -cp "$CLASSPATH" $JVMFLAGS $ZOOMAIN "$ZOOCFG"
    ;;
print-cmd)
    echo "$JAVA -Dzookeeper.log.dir=\"${ZOO_LOG_DIR}\" -Dzookeeper.root.logger=\"${ZOO_LOG4J_PROP}\" -cp \"$CLASSPATH\" $JVMFLAGS $ZOOMAIN \"$ZOOCFG\" > \"$_ZOO_DAEMON_OUT\" 2>&1 < /dev/null"
    ;;
stop)
    echo -n "Stopping zookeeper ... "
    if [ ! -f "$ZOOPIDFILE" ]
    then
      echo "no zookeeper to stop (could not find file $ZOOPIDFILE)"
    else
      $KILL -9 $(cat "$ZOOPIDFILE")
      rm "$ZOOPIDFILE"
      echo STOPPED
    fi
    exit 0
    ;;
upgrade)
    shift
    echo "upgrading the servers to 3.*"
    $JAVA "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.logger=${ZOO_LOG4J_PROP}" \
    -cp "$CLASSPATH" $JVMFLAGS org.apache.zookeeper.server.upgrade.UpgradeMain ${@}
    echo "Upgrading ... "
    ;;
restart)
    shift
    "$0" stop ${@}
    sleep 3
    "$0" start ${@}
    ;;
status)
    # -q is necessary on some versions of linux where nc returns too quickly, and no stat result is output
    STAT=`$JAVA "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.logger=${ZOO_LOG4J_PROP}" \
             -cp "$CLASSPATH" $JVMFLAGS org.apache.zookeeper.client.FourLetterWordMain localhost \
             $(grep "^[[:space:]]*clientPort" "$ZOOCFG" | sed -e 's/.*=//') srvr 2> /dev/null    \
          | grep Mode`
    if [ "x$STAT" = "x" ]
    then
        echo "Error contacting service. It is probably not running."
        exit 1
    else
        echo $STAT
        exit 0
    fi
    ;;
*)
    echo "Usage: $0 {start|start-foreground|stop|restart|status|upgrade|print-cmd}" >&2

esac