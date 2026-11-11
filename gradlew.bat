@echo off
setlocal
set DIRNAME=%~dp0
if defined JAVA_HOME (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_EXE=java.exe"
)
if not exist "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" (
  echo Gradle Wrapper JAR eksik. Once SETUP-WINDOWS.ps1 calistirin.
  exit /b 1
)
"%JAVA_EXE%" -Dorg.gradle.appname=gradlew -classpath "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
endlocal
