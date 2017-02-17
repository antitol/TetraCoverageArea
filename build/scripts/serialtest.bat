@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  serialtest startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and SERIALTEST_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
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
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\serialtest-1.0-SNAPSHOT.jar;%APP_HOME%\lib\jogl-all-natives-windows-amd64.jar;%APP_HOME%\lib\jogl-all-natives-linux-amd64.jar;%APP_HOME%\lib\jogl-all-natives-linux-i586.jar;%APP_HOME%\lib\libTUIO.jar;%APP_HOME%\lib\gluegen-rt-natives-linux-armv6hf.jar;%APP_HOME%\lib\unfolding.0.9.6.jar;%APP_HOME%\lib\jogl-all-natives-macosx-universal.jar;%APP_HOME%\lib\gluegen-rt-natives-linux-i586.jar;%APP_HOME%\lib\jogl-all-natives-windows-i586.jar;%APP_HOME%\lib\log4j-1.2.15.jar;%APP_HOME%\lib\gluegen-rt-natives-windows-amd64.jar;%APP_HOME%\lib\json4processing.jar;%APP_HOME%\lib\core.jar;%APP_HOME%\lib\jogl-all.jar;%APP_HOME%\lib\jai_core-1.1.3.jar;%APP_HOME%\lib\gluegen-rt-natives-linux-amd64.jar;%APP_HOME%\lib\sqlite-jdbc-3.7.2.jar;%APP_HOME%\lib\gluegen-rt.jar;%APP_HOME%\lib\gluegen-rt-natives-macosx-universal.jar;%APP_HOME%\lib\jogl-all-natives-linux-armv6hf.jar;%APP_HOME%\lib\gluegen-rt-natives-windows-i586.jar;%APP_HOME%\lib\saaj-impl-1.3.jar;%APP_HOME%\lib\miglayout-3.7.4.jar;%APP_HOME%\lib\jssc-2.8.0.jar;%APP_HOME%\lib\jdbi-2.78.jar;%APP_HOME%\lib\mysql-connector-java-5.1.38.jar;%APP_HOME%\lib\marineapi-0.9.0.jar;%APP_HOME%\lib\postgresql-9.4.1212.jre7.jar;%APP_HOME%\lib\commons-math3-3.6.1.jar;%APP_HOME%\lib\spatial4j-0.5.jar;%APP_HOME%\lib\saaj-api-1.3.jar;%APP_HOME%\lib\activation-1.0.2.jar

@rem Execute serialtest
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %SERIALTEST_OPTS%  -classpath "%CLASSPATH%" Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable SERIALTEST_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%SERIALTEST_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
