chcp 65001
@echo off
set path=E:\DEV\java\jdk\jdk8\bin
set jarName=ucloudmpc-0.0.1-SNAPSHOT.jar
set binPath=%~dp0
cd ..
set projectPath=%cd%
set confPath=%projectPath%\config
set jarPath=%projectPath%\%jarName%
set logPath=%projectPath%\log\ucloudmpc.log
%path%\java -Dfile.encoding=UTF-8  -jar %jarPath% --spring.config.location=file:./config/
pause