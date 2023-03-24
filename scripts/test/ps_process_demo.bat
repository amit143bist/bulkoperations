@echo off

rem This script will process and generate csv files in the specified csvpath. 
rem This will be scheduled by the Customer and a sample cron is shown below
rem crontab - example daily job scheduled at 2am
rem * 2 * * * [psdir]/ps_process_demo.bat

rem script parameters
set demoToken="9clmKmiVALRQT5unU96xQlhNymI="
set excludedAccountGUIDs=""
set env="demo"
set jarpath="C:\DocuApi\proServUtilities\target/proServUtilities-1.0.jar"
set csvpath="C:\Program Files (x86)\gatekeeper/testgc"
set narid="90895-3"
set groupNameContains=""
set proxyHost="abc.com"
set proxyPort="8080"

rem print parameters
echo Generating reports with narid %narid%. Files will be placed at %csvpath%.

rem execute
java -jar %jarpath% ps_process_demo --demoToken %demoToken% --excludedAccountGUIDs %excludedAccountGUIDs% --env %env% --csvpath %csvpath% --narid %narid% --groupNamecontains %groupNameContains% --proxyHost %proxyHost% --proxyPort %proxyPort%