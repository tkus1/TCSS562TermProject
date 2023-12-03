#!/bin/bash

# JSONデータを含むファイルのパス
json_file="queryFormColFilter.json"

# 送信先のURL
url="https://9ctd95p3c0.execute-api.us-east-2.amazonaws.com/DeployVersion1"

# curlコマンドを使用してHTTP POSTリクエストを送信
response=$(curl -s -X POST -H "Content-Type: application/json" --data @"${json_file}" "${url}")

# レスポンスを出力
echo "Response:"
echo "${response}"

parsed_response=$(echo "${response}" | jq '.')

# パースしたレスポンスを出力
echo "Parsed Response:"
echo "${parsed_response}"
