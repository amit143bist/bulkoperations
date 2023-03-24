@echo off

rem This script will process and generate csv files in the specified csvpath. 
rem This will be scheduled by the Customer and a sample cron is shown below
rem crontab - example daily job scheduled at 2am
rem * 2 * * * [psdir]/ps_oauth_process_demo.bat

rem https://account-d.docusign.com/oauth/auth?response_type=code&scope=impersonation%20signature&client_id=79c1d803-8ff2-466a-aefb-21b5246b6260&redirect_uri=https%3A%2F%2Fwww.docusign.com

rem script parameters
rem update utilityPath in below location
set utilityPath=C:/Users/amit.bist/git/proServBulkUpdateUtilities/deploy
set userIds="87b00103-461d-487b-8928-1991dfdb8d19"
set integratorKey="79c1d803-8ff2-466a-aefb-21b5246b6260"
set scope="signature"
set tokenExpiryLimit="3600"
set privatePemPath="%utilityPath%/config/ds-private.key"
set publicPemPath="%utilityPath%/config/ds-public.key"
set env="demo"
rem demo/prod
set jarpath="%utilityPath%/proServUtilities-1.0.jar"
set inDirpath="%utilityPath%/input"
set outDirpath="%utilityPath%/output"
set operationNames="ENVELOPECREATEASYNC"
set proxyHost=""
set proxyPort=""
set logfilePath="%utilityPath%/proServUtilities.log"
set loglevel="info"
set logfileAppendFlag="true"
set logfileAppendDatePattern="'_'yyyy-MM-dd-HH"
set appMaxThreadPoolSize="2"
set appCoreThreadPoolSize="2"
set validAccountGuids="e6043e37-08a9-4892-9e12-75b4d8bfef2a"

rem print parameters
echo Starting the job. Output files will be placed at %outDirpath%.

rem execute
java -Dexternal.application.properties=file:///%utilityPath%/config/application.properties -Dlogfile.name=%logfilePath% -Dloglevel=%loglevel% -DlogfileAppendFlag=%logfileAppendFlag% -DlogfileAppendDatePattern=%logfileAppendDatePattern% -jar %jarpath% ps_bulkops_process --userIds %userIds% --integratorKey %integratorKey% --scope %scope% --tokenExpiryLimit %tokenExpiryLimit% --privatePemPath %privatePemPath% --publicPemPath %publicPemPath% --env %env% --inDirpath %inDirpath% --proxyHost %proxyHost% --proxyPort %proxyPort% --outDirpath %outDirpath% --operationNames %operationNames% --appMaxThreadPoolSize %appMaxThreadPoolSize% --appCoreThreadPoolSize %appCoreThreadPoolSize% --appReminderAllowed %appReminderAllowed% --appExpirationAllowed %appExpirationAllowed% --validAccountGuids %validAccountGuids%