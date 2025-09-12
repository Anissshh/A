@ECHO OFF

SET DIR=%~dp0
SET APP_HOME=%DIR%
SET APP_BASE_NAME=%~n0
SET APP_NAME=Gradle

SET CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

java %DEFAULT_JVM_OPTS% -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
