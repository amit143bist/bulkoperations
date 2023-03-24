#!/bin/bash

# This script will process and generate csv files in the specified csvpath. 
# This will be scheduled by the Customer and a sample cron is shown below
# crontab - example daily job scheduled at 2am
# * 2 * * * [psdir]/ps_oauth_process_demo.sh

# script parameters
userIds="[UserIds]"
integratorKey="[IntegratorKey]"
scope="signature"
tokenExpiryLimit="[TokenExpiryLimitInSeconds]"
privatePemPath="[Private Pem Key Path]"
publicPemPath="[Public Pem Key Path]"
env="prod"
jarpath="[psdir]/proServUtilities-1.0.jar"
inDirpath="[CSV Input File Path]"
outDirpath="[CSV OutPut File Path]"
operationNames="[Operation Name like NOTIFICATIONCHANGES in uppercase]"
proxyHost="[proxyHost]"
proxyPort="[proxyPort]"
propertyPath="[psdir]/profileDesc.properties"
logfilePath="[psdir]/proServUtilities.log"
loglevel="[off | info | debug | error]"
logfileAppendFlag="[true | false]"
logfileAppendDatePattern="['_'yyyy-MM-dd-HH | '_'yyyy-MM-dd]"
appMaxThreadPoolSize="[Max Thread Pool Size]"
appCoreThreadPoolSize="[Core Thread Pool Size]"
appReminderAllowed="[true | false]"
appExpirationAllowed="[true | false]"
validAccountGuids="[AccountGuids comma separated]"

# print parameters
echo Starting the job. Output files will be placed at %outDirpath%.

# execute
java -DpropertyPath=$propertyPath -Dlogfile.name=$logfilePath -Dloglevel=$loglevel -DlogfileAppendFlag=$logfileAppendFlag -DlogfileAppendDatePattern=$logfileAppendDatePattern -jar $jarpath ps_bulkops_process --userIds $userIds --integratorKey $integratorKey --scope $scope --tokenExpiryLimit $tokenExpiryLimit --privatePemPath $privatePemPath --publicPemPath $publicPemPath --env $env --inDirpath $inDirpath --proxyHost $proxyHost --proxyPort $proxyPort --outDirpath $outDirpath --operationNames $operationNames --appMaxThreadPoolSize $appMaxThreadPoolSize --appCoreThreadPoolSize $appCoreThreadPoolSize --appReminderAllowed $appReminderAllowed --appExpirationAllowed $appExpirationAllowed --validAccountGuids $validAccountGuids