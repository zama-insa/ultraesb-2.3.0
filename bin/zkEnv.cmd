@echo off
REM Licensed to the Apache Software Foundation (ASF) under one or more
REM contributor license agreements.  See the NOTICE file distributed with
REM this work for additional information regarding copyright ownership.
REM The ASF licenses this file to You under the Apache License, Version 2.0
REM (the "License"); you may not use this file except in compliance with
REM the License.  You may obtain a copy of the License at
REM
REM     http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

set ZOOCFGDIR=%~dp0%..\conf
set ZOO_LOG_DIR=%~dp0%..
set ZOO_LOG4J_PROP=INFO,CONSOLE

REM for sanity sake assume Java 1.6
REM see: http://java.sun.com/javase/6/docs/technotes/tools/windows/java.html

REM add the zoocfg dir to classpath
set CLASSPATH=%ZOOCFGDIR%

SET CLASSPATH=%~dp0..\*;%~dp0..\lib\zookeeper-3.4.5.jar;%CLASSPATH%
SET CLASSPATH=%~dp0..\*;%~dp0..\lib\jline-0.9.94.jar;%CLASSPATH%
SET CLASSPATH=%~dp0..\*;%~dp0..\lib\log4j-1.2.16.jar;%CLASSPATH%
SET CLASSPATH=%~dp0..\*;%~dp0..\lib\slf4j-log4j12-1.7.2.jar;%CLASSPATH%
SET CLASSPATH=%~dp0..\*;%~dp0..\lib\slf4j-api-1.7.2.jar;%CLASSPATH%

set ZOOCFG=%ZOOCFGDIR%\zoo.cfg

:checkJava
if not defined JAVA_HOME goto findJavaHome
if not exist %JAVA_HOME%\bin\javac.exe goto findJavaHome
goto prepareJavaHome

:findJavaHome
set JAVA_HOME=
for %%P in (%PATH%) do if exist %%P\javac.exe set JAVA_HOME=%%~fP\..

:prepareJavaHome
rem we MUST have JAVA_HOME now, else we must quit
if not defined JAVA_HOME goto noJavaHome
rem get rid of any double quotes in JAVA_HOME
set JAVA_HOME=%JAVA_HOME:"=%
goto end

:noJavaHome
echo "You must have the JDK in the PATH or set the JAVA_HOME variable before running UltraESB"

:end
