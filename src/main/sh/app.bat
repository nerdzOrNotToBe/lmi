@if "%DEBUG%" == "" @echo off
setlocal EnableDelayedExpansion
@rem ##########################################################################
@rem
@rem  vertx startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and VERTX_OPTS to pass JVM options to this script.
@rem You can configure any property on VertxOptions or DeploymentOptions by setting system properties e.g.
@rem set VERTX_OPTS=-Dvertx.options.eventLoopPoolSize=26 -Dvertx.options.deployment.worker=true

@rem To enable vert.x sync agent, set the "ENABLE_VERTX_SYNC_AGENT" environment variable to "true". Be aware that you
@rem need to install vert.x sync in the $VERTX_HOME/lib directory before.

set JVM_OPTS=-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory
@rem To enable JMX uncomment the following
@rem set JMX_OPTS=-Dcom.sun.management.jmxremote -Dhazelcast.jmx=true -Dvertx.options.jmxEnabled=true

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set VERTX_HOME=%DIRNAME%..

@rem Find java.exe
@rem if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
set APPLICATION_JAR=lmIngenierie-1.0-SNAPSHOT-fat.jar
set VERTX_OPTS=run org.test.App -conf ./config.json
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init

@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%CLASSPATH%;%VERTX_HOME%\conf;%VERTX_HOME%\lib\*

@rem Execute vertx
echo "%JAVA_EXE%"   %JVM_OPTS% %JMX_OPTS% %JAVA_OPTS% -jar %APPLICATION_JAR%   %VERTX_OPTS%
"%JAVA_EXE%"   %JVM_OPTS% %JMX_OPTS% %JAVA_OPTS% -jar %APPLICATION_JAR%   %VERTX_OPTS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable VERTX_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%VERTX_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega