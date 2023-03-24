@echo off

rem This will be executed on-demand to generate an oauth token. 
rem The output token will need to be copied to update script parameter named 'na1Token', 'na2Token', 'na3Token' in ps_process.bat script.

rem script parameters
set na1User="[dsna1User]"
set na1Pwd="[dsna1Pwd]"
set na2User="[dsna2User]"
set na2Pwd="[dsna2Pwd]"
set na3User="[dsna3User]"
set na3Pwd="[dsna3Pwd]"
set env="prod"
set jarpath="[psdir]/proServUtilities-1.0.jar"
set integratorKey="[integratorKey]"
set proxyHost="[proxyHost]"
set proxyPort="[proxyPort]"

rem print parameters
echo Generating OAuth token! Copy the output tokens to update script parameter named 'na1Token', 'na2Token', 'na3Token' in ps_process.bat script.

rem execute
java -jar %jarpath% ps_token --na1User %na1User% --na1Pwd %na1Pwd% --na2User %na2User% --na2Pwd %na2Pwd% --na3User %na3User% --na3Pwd %na3Pwd% --env %env% --integratorKey %integratorKey% --proxyHost %proxyHost% --proxyPort %proxyPort%