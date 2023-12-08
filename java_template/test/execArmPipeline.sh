#!/bin/bash

# Offset to turn nanoseconds into milliseconds.
offset=1000000

# CHANGE THIS VALUE TO CONTROL NUMBER OF LOOP EXECUTIONS
execNtimes=1

# This is a sentinel value that denotes a hot/cold container.
isHot=false

# Check if the CSV for intel testing exists, if not make it and begin by adding row headers.
if [ -f ./cpuResults.csv ]; then
	echo # Do nothing.
else
	touch cpuResults.csv
	echo "CPU Architecture,Is Container Hot?,Service 1 Runtime (ms),Service 2 Runtime (ms),Query 1 Runtime (ms),Query 2 Runtime (ms),Query 3 Runtime (ms),Total Runtime (ms)" > cpuResults.csv
fi


# Change the middle value to run the program multiple times 
# (this is for collecting large amounts of data!)
for (( i=0 ; i<$execNtimes ; i++ )); 
do
	# Service 1
	start=`date +%s%N`
	python3 postCSVData.py> /dev/null 2>&1
	end=`date +%s%N`
	s1_time=$(( (end - start) / offset ))


	# Service 2
	start=`date +%s%N`
	sh ./loadData.sh> /dev/null 2>&1
	end=`date +%s%N`
	s2_time=$(( (end - start) / offset ))
	
	
	# Query 3a.
	start=`date +%s%N`
	sh ./queryCol.sh> /dev/null 2>&1
	end=`date +%s%N`
	s3a_time=$(( (end - start) / offset ))
	
	
	# Query 2b.
	start=`date +%s%N`
	sh ./queryColAgg.sh> /dev/null 2>&1
	end=`date +%s%N`
	s3b_time=$(( (end - start) / offset ))
	
	
	# Query 3c.
	start=`date +%s%N`
	sh ./queryColFilter.sh> /dev/null 2>&1
	end=`date +%s%N`
	s3c_time=$(( (end - start) / offset ))
	
	
	totalTime=$(($s1_time + $s2_time + $s3a_time + $s3b_time + $s3c_time))
	
	
	echo "ARM,$isHot,$s1_time,$s2_time,$s3a_time,$s3b_time,$s3c_time,$totalTime" >> cpuResults.csv
	
	
	isHot=true
done
