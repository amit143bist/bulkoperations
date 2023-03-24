@echo off

rem This script will process and generate csv files in the specified csvpath. 
rem This will be scheduled by the Customer and a sample cron is shown below
rem crontab - example daily job scheduled at 2am
rem * 2 * * * [psdir]/ps_process.bat

rem script parameters
set na1Token="[na1 token output from gk-token script]"
set na2Token="[na2 token output from gk-token script]"
set na3Token="[na3 token output from gk-token script]"
set excludedAccountGUIDs="[db account guids comma separated]"
set env="prod"
set jarpath="[psdir]/proServUtilities-1.0.jar"
set csvpath="[csvpath]"
set narid="[90895-1 | 90895-2 | 90895-3]"
set groupNameContains="[group-name-here]"
set proxyHost="[proxyHost]"
set proxyPort="[proxyPort]"

rem print parameters
echo Generating reports with narid %narid%. Files will be placed at %csvpath%.

rem execute
java -jar %jarpath% ps_process --na1Token %na1Token% --na2Token %na2Token% --na3Token %na3Token% --excludedAccountGUIDs %excludedAccountGUIDs% --env %env% --csvpath %csvpath% --narid %narid% --groupNamecontains %groupNameContains% --proxyHost %proxyHost% --proxyPort %proxyPort%