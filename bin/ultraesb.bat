@echo off

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

rem %~dp0 is expanded pathname of the current script under NT
set ULTRA_HOME=%~dps0..
set ULTRA_XML="%ULTRA_HOME%\conf\ultra-root.xml"

rem Slurp the command line arguments
:setupArgs
if ""%1""=="""" goto doneStart
if ""%1""==""-sample"" goto UltraSample
if ""%1""==""-serverName"" goto serverName
if ""%1""==""-installationName"" goto installationName
if ""%1""==""-xdebug"" goto xdebug
shift
goto setupArgs

:xdebug
set _XDEBUG="-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"
shift
goto setupArgs

:UltraSample
shift
set ULTRA_XML="--rootConf=%ULTRA_HOME%\samples\conf\ultra-sample-%1.xml"
shift
goto setupArgs

:serverName
shift
set _SERVER_NAME="--serverName=%1"
shift
goto setupArgs

:installationName
shift
set _SERVER_NAME="--installationName=%1"
shift
goto setupArgs

:doneStart
rem find ULTRA_HOME if it does not exist we cannot run
if exist "%ULTRA_HOME%\README.TXT" goto checkJava
echo ULTRA_HOME is set incorrectly or the UltraESB files could not be located.
goto end

:checkJava
if not defined JAVA_HOME goto findJavaHome
rem get rid of any double quotes in JAVA_HOME
set JAVA_HOME=%JAVA_HOME:"=%
if not exist "%JAVA_HOME%\bin\javac.exe" goto findJavaHome
goto checkVersion

:findJavaHome
set JAVA_HOME=
for %%P in (%PATH%) do if exist %%P\javac.exe set JAVA_HOME=%%~fP\..
rem get rid of any double quotes in JAVA_HOME
set JAVA_HOME=%JAVA_HOME:"=%

rem we MUST have JAVA_HOME now, else we must quit
if not defined JAVA_HOME goto noJavaHome
goto checkVersion

:noJavaHome
echo "You must have the JDK in the PATH or set the JAVA_HOME variable before running UltraESB"
goto end

:checkVersion
rem we REQUIRE a Java DEVELOPMENT Kit (JDK) of version 1.6.x (a JRE might not be adequate for all scenarios)
"%JAVA_HOME%\bin\javac" -version 2>&1 | findstr /r "1.[6|7]" >NUL
IF ERRORLEVEL 1 goto nojdk
goto runServer

:nojdk
echo "The UltraESB REQUIRES a Java DEVELOPMENT Kit (JDK) version 1.6.x or later, and Java MUST be available in the PATH. (A JRE may not be sufficient)"
goto end

:runServer
@rem @echo on
cd %ULTRA_HOME%
echo "Starting AdroitLogic UltraESB ..."
echo Using ULTRA_HOME:  %ULTRA_HOME%
echo Using JAVA_HOME:   %JAVA_HOME%

rem ----- Uncomment the following line to enable the SSL debug options ----------
rem set ULTRA_OPTS="-Djavax.net.debug=all"
set ULTRA_OPTS=%ULTRA_OPTS% -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XMLGrammarCachingConfiguration
set ULTRA_OPTS=%ULTRA_OPTS% -Djava.endorsed.dirs=%ULTRA_HOME%\lib\endorsed
set ULTRA_OPTS=%ULTRA_OPTS% -Dehcache.disk.store.dir=%ULTRA_HOME%\data
set ULTRA_OPTS=%ULTRA_OPTS% -Dorg.terracotta.quartz.skipUpdateCheck=true
set ULTRA_OPTS=%ULTRA_OPTS% -Dfile.encoding=UTF-8
set ULTRA_OPTS=%ULTRA_OPTS% -Dsun.jnu.encoding=UTF-8

set CLASSPATH=conf;"%JAVA_HOME%\lib\tools.jar";lib\patches;conf\mediation\classes;lib\patches\*;lib\*;lib\custom\*;lib\optional\*;lib\samples\*

set ULTRA_CONF="--confDir=%ULTRA_HOME%\conf"

"%JAVA_HOME%\bin\java" -server -Xms1024M -Xmx1024M %_XDEBUG% %ULTRA_OPTS% -cp %CLASSPATH% org.adroitlogic.ultraesb.UltraServer %ULTRA_CONF% %ULTRA_XML% %SERVER_NAME%

if not errorlevel 1 goto :end

:end
pause
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal