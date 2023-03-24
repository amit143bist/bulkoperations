@echo off

rem This script will process and generate csv files in the specified csvpath. 
rem This will be scheduled by the Customer and a sample cron is shown below
rem crontab - example daily job scheduled at 2am
rem * 2 * * * [psdir]/ps_oauth_process.bat

rem script parameters
set userIds="[UserIds comma separated for each site]"
set integratorKey="[Integrator Key]"
set secretKey="[Secret Key]"
set scope="signature"
set privatePemPath="[Private Pem Key Path]"
set publicPemPath="[Public Pem Key Path]"
set excludedAccountGUIDs="[AccountGuids comma separated]"
set env="prod"
set jarpath="[psdir]/proServUtilities-1.0.jar"
set csvpath="[csvpath]"
set narid="[90895-1 | 90895-2 | 90895-3]"
set groupNameContains="[group-name-here comma separated]"
set proxyHost="[proxyHost]"
set proxyPort="[proxyPort]"
set accountIdEnvelopeCustomFieldName="[Account Level Envelope Custom Field Name]"
set propertyPath="[psdir]/profileDesc.properties"
set logfilePath="[psdir]/proServUtilities.log"
set loglevel="[off | info | debug | error]"
set logfileAppendFlag="[true | false]"
set logfileAppendDatePattern="['_'yyyy-MM-dd-HH | '_'yyyy-MM-dd]"
set mandatoryPropKeys="'DocuSign Viewer_Required,DocuSign Sender_Required,Account Administrator_Required'"
set systemUserEmails="[Comma separated UserEmails, for which owner should be narid]"
set includeInvalidUsersFlag="[true | false]"
set csvFieldNames="rights"

rem print parameters
echo Generating reports with narid %narid%. Files will be placed at %csvpath%.

rem execute
java -DpropertyPath=%propertyPath% -Dlogfile.name=%logfilePath% -Dloglevel=%loglevel% -DlogfileAppendFlag=%logfileAppendFlag% -DlogfileAppendDatePattern=%logfileAppendDatePattern% -jar %jarpath% ps_oauth_process --userIds %userIds% --integratorKey %integratorKey% --secretKey %secretKey% --scope %scope% --privatePemPath %privatePemPath% --publicPemPath %publicPemPath% --excludedAccountGUIDs %excludedAccountGUIDs% --env %env% --csvpath %csvpath% --narid %narid% --groupNamecontains %groupNameContains% --systemUserEmails %systemUserEmails% --accountIdEnvelopeCustomFieldName %accountIdEnvelopeCustomFieldName% --mandatoryPropKeys %mandatoryPropKeys% --includeInvalidUsersFlag %includeInvalidUsersFlag% --csvFieldNames %csvFieldNames% --proxyHost %proxyHost% --proxyPort %proxyPort%