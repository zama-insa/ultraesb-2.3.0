@echo off

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

rem %~dp0 is expanded pathname of the current script under NT
set ULTRA_HOME=%~dps0..

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
rem we require Java version 1.6.x or later
"%JAVA_HOME%\bin\java" -version 2>&1 | findstr /r "1.[6|7]" >NUL
IF ERRORLEVEL 1 goto nojdk
goto runServer

:nojdk
echo "The UltraESB toolbox requires Java version 1.6.x or later and Java MUST be available in the PATH - Note: A JRE might not be sufficient"
goto end

:runServer
@rem @echo on
cd %ULTRA_HOME%
echo "Starting AdroitLogic ToolBox ..."
echo Using ULTRA_HOME:  %ULTRA_HOME%
echo Using JAVA_HOME:   %JAVA_HOME%

rem ----- Uncomment the following line to enalbe the SSL debug options ----------
rem set ULTRA_OPTS="-Djavax.net.debug=all"
set ULTRA_OPTS=%ULTRA_OPTS% -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XMLGrammarCachingConfiguration
set ULTRA_OPTS=%ULTRA_OPTS% -Djava.endorsed.dirs=%ULTRA_HOME%\lib\endorsed
set ULTRA_OPTS=%ULTRA_OPTS% -Djava.io.tmpdir=%ULTRA_HOME%\tmp
set ULTRA_OPTS=%ULTRA_OPTS% -Dfile.encoding=UTF-8
set ULTRA_OPTS=%ULTRA_OPTS% -Dsun.jnu.encoding=UTF-8

set CLASSPATH=conf;"%JAVA_HOME%\lib\tools.jar";lib\patches;conf\mediation\classes;lib\patches\*;lib\*;lib\custom\*;lib\optional\*;lib\samples\*;lib\test\*

set ULTRA_CONF=%ULTRA_HOME%\conf

"%JAVA_HOME%\bin\java" -server -Xms512M -Xmx512M %_XDEBUG% %ULTRA_OPTS% -cp %CLASSPATH% org.adroitlogic.toolbox.ToolBoxApp

if not errorlevel 1 goto :end
pause

:end
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal