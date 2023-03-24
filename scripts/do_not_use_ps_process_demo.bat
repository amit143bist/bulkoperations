@echo off

rem This script will process and generate csv files in the specified csvpath. 
rem This will be scheduled by the Customer and a sample cron is shown below
rem crontab - example daily job scheduled at 2am
rem * 2 * * * [psdir]/ps_process_demo.bat

rem script parameters
set demoToken="[demo token output from gk-token script]"
set excludedAccountGUIDs="[db account guids comma separated]"
set env="demo"
set jarpath="[psdir]/proServUtilities-1.0.jar"
set csvpath="[csvpath]"
set narid="[90895-1 | 90895-2 | 90895-3]"
set groupNameContains="[group-name-here]"
set proxyHost="[proxyHost]"
set proxyPort="[proxyPort]"

rem print parameters
echo Generating reports with narid %narid%. Files will be placed at %csvpath%.

rem execute
java -jar %jarpath% ps_process_demo --demoToken %demoToken% --excludedAccountGUIDs %excludedAccountGUIDs% --env %env% --csvpath %csvpath% --narid %narid% --groupNamecontains %groupNameContains% --proxyHost %proxyHost% --proxyPort %proxyPort%