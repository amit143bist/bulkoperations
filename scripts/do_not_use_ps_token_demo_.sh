#!/bin/bash 

# This will be executed on-demand to generate an oauth token. 
# The output token will need to be copied to update script parameter named 'token' in ps_process.sh script.

# script parameters
demoUser="[dsuser]"
demoPwd="[dspwd]"
env="demo"
jarpath="[psdir]/proServUtilities-1.0.jar"
integratorKey="[integratorKey]"
proxyHost="[proxyHost]"
proxyPort="[proxyPort]"

#print parameters
echo Generating OAuth token! Copy the output token to update script parameter named 'demoToken' in ps_process.sh script.

#execute
java -jar $jarpath ps_token_demo --demoUser $demoUser --demoPwd $demoPwd --env $env --integratorKey $integratorKey --proxyHost $proxyHost --proxyPort $proxyPort