@echo off

rem This script will process and generate csv files in the specified csvpath. 
rem This will be scheduled by the Customer and a sample cron is shown below
rem crontab - example daily job scheduled at 2am
rem * 2 * * * [psdir]/ps_oauth_process_demo.bat

rem script parameters
set userIds="[UserIds]"
set integratorKey="[IntegratorKey]"
set scope="signature"
set tokenExpiryLimit="[TokenExpiryLimitInSeconds]"
set privatePemPath="[Private Pem Key Path]"
set publicPemPath="[Public Pem Key Path]"
set env="demo"
set jarpath="[psdir]/proServUtilities-1.0.jar"
set inDirpath="[CSV Input File Path]"
set outDirpath="[CSV OutPut File Path]"
set operationNames="[Operation Name like NOTIFICATIONCHANGES in uppercase]"
set proxyHost="[proxyHost]"
set proxyPort="[proxyPort]"
set propertyPath="[psdir]/profileDesc.properties"
set logfilePath="[psdir]/proServUtilities.log"
set loglevel="[off | info | debug | error]"
set logfileAppendFlag="[true | false]"
set logfileAppendDatePattern="['_'yyyy-MM-dd-HH | '_'yyyy-MM-dd]"
set appMaxThreadPoolSize="[Max Thread Pool Size]"
set appCoreThreadPoolSize="[Core Thread Pool Size]"
set appReminderAllowed="[true | false]"
set appExpirationAllowed="[true | false]"
set validAccountGuids="[AccountGuids comma separated]"

rem print parameters
echo Starting the job. Output files will be placed at %outDirpath%.

rem execute
java -DpropertyPath=%propertyPath% -Dlogfile.name=%logfilePath% -Dloglevel=%loglevel% -DlogfileAppendFlag=%logfileAppendFlag% -DlogfileAppendDatePattern=%logfileAppendDatePattern% -jar %jarpath% ps_bulkops_process --userIds %userIds% --integratorKey %integratorKey% --scope %scope% --tokenExpiryLimit %tokenExpiryLimit% --privatePemPath %privatePemPath% --publicPemPath %publicPemPath% --env %env% --inDirpath %inDirpath% --proxyHost %proxyHost% --proxyPort %proxyPort% --outDirpath %outDirpath% --operationNames %operationNames% --appMaxThreadPoolSize %appMaxThreadPoolSize% --appCoreThreadPoolSize %appCoreThreadPoolSize% --appReminderAllowed %appReminderAllowed% --appExpirationAllowed %appExpirationAllowed% --validAccountGuids %validAccountGuids%