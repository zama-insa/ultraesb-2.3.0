@echo off

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

rem %~dp0 is expanded pathname of the current script under NT
set ULTRA_HOME=%~dps0..

set EXECUTABLE_CLASS="org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI"
set ARGS=encrypt.bat

rem Slurp the command line arguments
:setupArgs
if ""%1""=="""" goto doneStart
if ""%1""==""-uconsole"" goto uconsole
if ""%1""==""-secure"" goto secure
if ""%1""==""-xdebug"" goto xdebug
set ARGS=%ARGS% %1=%2
shift
shift
goto setupArgs

:xdebug
set _XDEBUG="-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"
shift
goto setupArgs

:uconsole
set EXECUTABLE_CLASS=org.adroitlogic.ultraesb.admin.util.PasswordCipherTool
set ARGS=%2
goto doneStart

:secure
set EXECUTABLE_CLASS=org.adroitlogic.ultraesb.util.encrypt.SecurePropertyManager
set ARGS=%2
goto doneStart

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
echo "The UltraESB encryption tool requires Java v1.6 or later and Java MUST be available in the PATH"
goto end

:runServer
@rem @echo on
cd %ULTRA_HOME%

set CLASSPATH="%JAVA_HOME%\lib\tools.jar";lib\patches;lib\patches\*;lib\*;lib\custom\*;lib\optional\*;uconsole\WEB-INF\classes;uconsole\WEB-INF\lib\*

"%JAVA_HOME%\bin\java" -server -Xms1024M -Xmx1024M %_XDEBUG% -cp %CLASSPATH% %EXECUTABLE_CLASS% %ARGS%

if not errorlevel 1 goto :end

:end
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
