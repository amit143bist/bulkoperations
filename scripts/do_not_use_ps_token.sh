#!/bin/bash 

# This will be executed on-demand to generate an oauth token. 
# The output token will need to be copied to update script parameter named 'na1Token', 'na2Token', 'na3Token' in ps_process.sh script.

# script parameters
na1User="[dsna1User]"
na1Pwd="[dsna1Pwd]"
na2User="[dsna2User]"
na2Pwd="[dsna2Pwd]"
na3User="[dsna3User]"
na3Pwd="[dsna3Pwd]"
env="prod"
jarpath="[psdir]/proServUtilities-1.0.jar"
integratorKey="[integratorKey]"
proxyHost="[proxyHost]"
proxyPort="[proxyPort]"

#print parameters
echo Generating OAuth token! Copy the output tokens to update script parameter named 'na1Token', 'na2Token', 'na3Token' in ps_process.sh script.

#execute
java -jar $jarpath ps_token --na1User $na1User --na1Pwd $na1Pwd --na2User $na2User --na2Pwd $na2Pwd --na3User $na3User --na3Pwd $na3Pwd --env $env --integratorKey $integratorKey --proxyHost $proxyHost --proxyPort $proxyPort