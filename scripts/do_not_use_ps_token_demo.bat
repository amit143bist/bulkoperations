@echo off

rem This will be executed on-demand to generate an oauth token. 

rem The output token will need to be copied to update script parameter named 'token' in ps_process_demo.bat script.

rem script parameters

set demoUser="[dsuser]"
set demoPwd="[dspwd]"
set env="demo"
set jarpath="[psdir]/proServUtilities-1.0.jar"
set integratorKey="[integratorKey]"
set proxyHost="[proxyHost]"
set proxyPort="[proxyPort]"

rem print parameters
echo Generating OAuth token! Copy the output token to update script parameter named 'demoToken' in ps_process_demo.bat script.

rem execute
java -jar %jarpath% ps_token_demo --demoUser %demoUser% --demoPwd %demoPwd% --env %env% --integratorKey %integratorKey% --proxyHost %proxyHost% --proxyPort %proxyPort%