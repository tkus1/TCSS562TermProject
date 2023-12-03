#!/bin/bash

API_URL="https://h5vmuam4i8.execute-api.us-east-2.amazonaws.com/testDeploy"
JSON_FILE_PATH="dataLoaderInput.json" 

RESPONSE=$(curl -X POST -H "Content-Type: application/json" --data-binary "@${JSON_FILE_PATH}" "${API_URL}")

echo "Response:"
echo "${RESPONSE}" | jq .
