#!/bin/bash

# This script will process and generate csv files in the specified csvpath. 
# This will be scheduled by the Customer and a sample cron is shown below
# crontab - example daily job scheduled at 2am
# * 2 * * * [psdir]/ps_oauth_process_demo.sh

# script parameters
userIds="[UserIds comma separated for each site]"
integratorKey="[Integrator Key]"
secretKey="[Secret Key]"
scope="signature"
privatePemPath="[Private Pem Key Path]"
publicPemPath="[Public Pem Key Path]"
excludedAccountGUIDs="[AccountGuids comma separated]"
env="demo"
jarpath="[psdir]/proServUtilities-1.0.jar"
csvpath="[csvpath]"
narid="[90895-1 | 90895-2 | 90895-3]"
groupNameContains="[group-name-here comma separated]"
proxyHost="[proxyHost]"
proxyPort="[proxyPort]"
accountIdEnvelopeCustomFieldName="[Account Level Envelope Custom Field Name]"
propertyPath="[psdir]/profileDesc.properties"
logfilePath="[psdir]/proServUtilities.log"
loglevel="[off | info | debug | error]"
logfileAppendFlag="[true | false]"
logfileAppendDatePattern="['_'yyyy-MM-dd-HH | '_'yyyy-MM-dd]"
mandatoryPropKeys="'DocuSign Viewer_Required,DocuSign Sender_Required,Account Administrator_Required'"
systemUserEmails="[Comma separated UserEmails, for which owner should be narid]"
includeInvalidUsersFlag="[true | false]"
csvFieldNames="rights"


# print parameters
echo Generating reports with narid $narid. Files will be placed at $csvpath.

# execute
java -DpropertyPath=$propertyPath -Dlogfile.name=$logfilePath -Dloglevel=$loglevel -DlogfileAppendFlag=$logfileAppendFlag -DlogfileAppendDatePattern=$logfileAppendDatePattern -jar $jarpath ps_oauth_process --userIds $userIds --integratorKey $integratorKey --secretKey $secretKey --scope $scope --privatePemPath $privatePemPath --publicPemPath $publicPemPath --excludedAccountGUIDs $excludedAccountGUIDs --env $env --csvpath $csvpath --narid $narid --groupNamecontains $groupNameContains --systemUserEmails $systemUserEmails --accountIdEnvelopeCustomFieldName $accountIdEnvelopeCustomFieldName --mandatoryPropKeys $mandatoryPropKeys --includeInvalidUsersFlag $includeInvalidUsersFlag --csvFieldNames $csvFieldNames --proxyHost $proxyHost --proxyPort $proxyPort