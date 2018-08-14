import json
"""
go to /odms/doc to get the raw json text.
copy and paste that into a file and pipe it into this script. optionally pipe output to a json file

usage: python3 apiExtractor.py < raw > api.json
"""
json_obj = json.loads(input(""))

print(json_obj["value"])
