
# Term Project TCSS 462/562

  

  

  

## How to use

  

### Service 1
Under /java_template/test, call:
- python3 postCSVData.py

- time python3 postCSVData.py

under /java_template/test
  
This python script load 100SalesRecords.csv and convert it to 100SalesRecords.json and POST the json data one line at a time. This script call TramsformAndStore in AWS Lambda, transforming data and storing the transformed data to a csv file in S3. Bucket name and file path are specified in the source code of TransformAndStore.

  

### Service 2

Under /java_template/test, call:

- ./loadData.sh
- time ./loadData.sh

under /java_template/test

  

This bash script call DataLoader in AWS to load csv data to database (mySQL in RDS. Table name is SalesRecordTab).

  

### Service 3

Under /java_template/test, call:

- ./queryCol.sh
- ./queryColAgg.sh
- ./queryColFilter.sh

(or with "time" to measure running time)

These bash scripts POST HTTP request with query conditions and invoke QueryHandler in Lambda. This Lambda funciton process queries for the database (mySQL in RDS. Table name is SalesRecordTab).



### Run TLQ Pipeline and Collect Data
Under /java_template/test, call*:
- ./execPipeline.sh

This command contributes to the building of a CSV file that contains the runtime metrics, hot/cold runtime, and CPU architecture. Inside the bash scripts is a loop that is initalized to run from 0 to 1. However, it can be increased to do large scale testing. These two functions also make sure not to crowd the console which is done by rerouting the console output to a null directory.
