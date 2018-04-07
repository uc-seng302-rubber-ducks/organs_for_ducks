"""
DONOR GENERATOR
creates a large number of donors for testing purposes. output file goes 
to folder script is run in
"""

#TODO make list of blood types and set to i % len
import json
from collections import OrderedDict
donors = []
def generate(num):
    num = int(num)
    outputFile = open("donors.json", "w")
    for i in range(0, num):
        donor = {
            "is Deceased":False,
            "Last Modified":"2018-03-21T14:33:35.975+13:00",
            "Organs":["KIDNEY","SKIN","LUNG"],
            "Misc":["glasses"],
            "Gender":"U",
            "Weight":0.0,
            "Name":"donor #{}".format(i),
            "Current Address":"{} big st".format(i),
            "Blood Type": "O-",
            "DOB":"Thu Jan 01 00:00:00 NZST 1970","DOD":"null",
            "Region":"erehwon",
            "Height":1.76,
            "Time Created":"2018-03-21T14:32:21.206+13:00"}
        donors.append(donor)
    
    json_donors = json.dumps(donors)
    outputFile.write(json_donors)
    outputFile.close()
    
generate(input("num of donors to generate?: "))