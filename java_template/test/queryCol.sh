#!/bin/bash                                                                                                                                                                                    

# path of json file that include request content                                                                                                                                                               
json_file="queryFormCol.json"

# API URL for the function                                                                                                                                                                      
url="https://9ctd95p3c0.execute-api.us-east-2.amazonaws.com/DeployVersion1"

# HTTP POST using curl                                                                                                                                              
response=$(curl -s -X POST -H "Content-Type: application/json" --data @"${json_file}" "${url}")

# echo the response from the function                                                                                                                                                                             
echo "Response:"
echo "${response}"

parsed_response=$(echo "${response}" | jq '.')

# output parsed response in JSON                                                                                                                                               
echo "Parsed Response:"
echo "${parsed_response}"

