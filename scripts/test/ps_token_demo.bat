@echo off

rem This will be executed on-demand to generate an oauth token.

rem The output token will need to be copied to update script parameter named 'token' in ps_process_demo.bat script.

rem script parameters

set demoUser="docusign.sso+ssomanager9@gmail.com"
set demoPwd="testing1"
set env="demo"
set jarpath="C:\DocuApi\proServUtilities\target/proServUtilities-1.0.jar"
set integratorKey="16f81d9e-e9ee-408d-bc60-d6e1aecd9756"
set proxyHost=""
set proxyPort=""

rem print parameters
echo Generating OAuth token! Copy the output token to update script parameter named 'demoToken' in ps_process_demo.bat script.

rem execute
java -jar %jarpath% ps_token_demo --demoUser %demoUser% --demoPwd %demoPwd% --env %env% --integratorKey %integratorKey% --proxyHost %proxyHost% --proxyPort %proxyPort%