import json
import requests
import csv

def post_json_data(json_file_path, url):
    print("Invoking Transform Step:")

    with open(json_file_path, 'r') as json_file:
        json_data = json.load(json_file)

    response = requests.post(url, json=json_data)

    print("Response Status Code:", response.status_code)
    print("Response Content:", response.text)
##    for idx, item in enumerate(json_data):
##        single_item_data = [item]
##
##        response = requests.post(url, json=single_item_data)

##        print(f"POST #{idx + 1} - HTTP Status Code: {response.status_code}")
##        print("Response Content:", response.text)
##        print()

def csv_to_json(csv_file_path, json_file_path):
    with open(csv_file_path, 'r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        json_data = json.dumps(list(csv_reader), indent=2)

    with open(json_file_path, 'w') as json_file:
        json_file.write(json_data)

csv_file_path = '10000SalesRecords.csv'
json_file_path = '10000SalesRecords.json'
csv_to_json(csv_file_path, json_file_path)
    
url = 'https://2sfqufzf56.execute-api.us-east-2.amazonaws.com/testDeploy'
post_json_data(json_file_path, url)

