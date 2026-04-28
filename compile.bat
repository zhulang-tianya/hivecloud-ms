@echo off
set JAVA_HOME=C:\work\java\jdk-17\jdk-17.0.9
set PATH=%JAVA_HOME%\bin;C:\work\apache-maven-3.9.6\bin;%PATH%
cd /d %~dp0
mvn clean install -DskipTests
pause
