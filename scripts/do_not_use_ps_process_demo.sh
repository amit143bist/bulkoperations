#!/bin/bash 

# This script will process and generate csv files in the specified csvpath. 
# This will be scheduled by the Customer and a sample cron is shown below
# crontab - example daily job scheduled at 2am
# * 2 * * * [psdir]/ps_process.sh

# script parameters
demoToken="[demo token output from gk-token script]"
excludedAccountGUIDs="[db account guids comma separated]"
env="demo"
jarpath="[psdir]/proServUtilities-1.0.jar"
csvpath="[csvpath]"
narid="[90895-1 | 90895-2]"
groupNameContains="[group-name-here]"
proxyHost="[proxyHost]"
proxyPort="[proxyPort]"

#print parameters
echo Generating reports with narid $narid. Files will be placed at $csvpath.

#execute
java -jar $jarpath ps_process_demo --demoToken $demoToken --excludedAccountGUIDs $excludedAccountGUIDs --env $env --csvpath $csvpath --narid $narid --groupNamecontains $groupNameContains --proxyHost $proxyHost --proxyPort $proxyPort