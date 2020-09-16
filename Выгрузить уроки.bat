@echo off

rem construct classpath
setlocal EnableDelayedExpansion 
set CP=.
for %%L in (.\lib\*.jar ) do set CP=!CP!;%%L
set CLASSPATH=%CP%;%CLASSPATH%

chcp 866
rem start application
java  schedule.Main "asc.xml" -get_lessons%*
pause;