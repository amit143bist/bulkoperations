#!/bin/bash

# This script will process and generate csv files in the specified csvpath. 
# This will be scheduled by the Customer and a sample cron is shown below
# crontab - example daily job scheduled at 2am
# * 2 * * * [psdir]/ps_oauth_process_prod.bat

# script parameters
utilityPath=/Users/pedro.barroso/dev/envcreateissue
userIds="73aba21e-d941-411e-888e-a367570a1575"
integratorKey="ab36dc42-9f87-49fc-955e-939d625f2f2d"
scope="signature"
tokenExpiryLimit="3600"
privatePemPath="$utilityPath/config/ds-private.key"
publicPemPath="$utilityPath/config/ds-public.key"
env="demo"
jarpath="$utilityPath/proServUtilities-1.0.jar"
inDirpath="$utilityPath/input"
outDirpath="$utilityPath/output"
operationNames="ENVELOPECREATEASYNC"
proxyHost=""
proxyPort=""
logfilePath="$utilityPath/logs/proServUtilities.log"
loglevel="info"
logfileAppendFlag="true"
logfileAppendDatePattern="'_'yyyy-MM-dd-HH"
appMaxThreadPoolSize="3"
appCoreThreadPoolSize="2"
appReminderAllowed="false"
appExpirationAllowed="false"
validAccountGuids="2ca82827-fdeb-484a-bd70-1a74a7d4cff3"

# print parameters
echo Starting the job. Output files will be placed at $outDirpath.

# execute
java -Dexternal.application.properties=file:///$utilityPathconfig/application.properties -Dlogfile.name=$logfilePath -Dloglevel=$loglevel -DlogfileAppendFlag=$logfileAppendFlag -DlogfileAppendDatePattern=$logfileAppendDatePattern -jar $jarpath ps_bulkops_process --userIds $userIds --integratorKey $integratorKey --scope $scope --tokenExpiryLimit $tokenExpiryLimit --privatePemPath $privatePemPath --publicPemPath $publicPemPath --env $env --inDirpath $inDirpath --proxyHost $proxyHost --proxyPort $proxyPort --outDirpath $outDirpath --operationNames $operationNames --appMaxThreadPoolSize $appMaxThreadPoolSize --appCoreThreadPoolSize $appCoreThreadPoolSize --appReminderAllowed $appReminderAllowed --appExpirationAllowed $appExpirationAllowed --validAccountGuids $validAccountGuids