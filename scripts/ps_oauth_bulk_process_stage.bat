@echo off

rem This script will process and generate csv files in the specified csvpath. 
rem This will be scheduled by the Customer and a sample cron is shown below
rem crontab - example daily job scheduled at 2am
rem * 2 * * * [psdir]/ps_oauth_process_prod.bat

rem script parameters
set utilityPath=C:\\devwork\\unvoidissue
set userIds="5151013d-4df5-45e5-8d00-0d56c03623f9"
set integratorKey="4a35a93e-2fd3-4e93-89a5-2bc37a98533b"
set scope="signature"
set tokenExpiryLimit="3600"
set privatePemPath="%utilityPath%\ds-private-stage.key"
set publicPemPath="%utilityPath%\ds-public-stage.key"
set env="stage"
set jarpath="%utilityPath%/proServUtilities-1.0.jar"
set inDirpath="%utilityPath%\input"
set outDirpath="%utilityPath%\output"
set operationNames="ENVELOPERESUME"
set proxyHost=""
set proxyPort=""
set logfilePath="%utilityPath%/proServUtilities.log"
set loglevel="debug"
set logfileAppendFlag="true"
set logfileAppendDatePattern="'_'yyyy-MM-dd-HH"
set appMaxThreadPoolSize="1"
set appCoreThreadPoolSize="1"
set validAccountGuids="540bbb0a-48a8-4c24-b602-cff629975e0b"

rem print parameters
echo Starting the job. Output files will be placed at %outDirpath%.

rem execute
java -Dlogfile.name=%logfilePath% -Dloglevel=%loglevel% -DlogfileAppendFlag=%logfileAppendFlag% -DlogfileAppendDatePattern=%logfileAppendDatePattern% -jar %jarpath% ps_bulkops_process --userIds %userIds% --integratorKey %integratorKey% --scope %scope% --tokenExpiryLimit %tokenExpiryLimit% --privatePemPath %privatePemPath% --publicPemPath %publicPemPath% --env %env% --inDirpath %inDirpath% --proxyHost %proxyHost% --proxyPort %proxyPort% --outDirpath %outDirpath% --operationNames %operationNames% --appMaxThreadPoolSize %appMaxThreadPoolSize% --appCoreThreadPoolSize %appCoreThreadPoolSize% --appReminderAllowed %appReminderAllowed% --appExpirationAllowed %appExpirationAllowed% --validAccountGuids %validAccountGuids%