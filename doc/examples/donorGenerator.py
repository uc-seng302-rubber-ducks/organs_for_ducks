"""
DONOR GENERATOR
creates a large number of donors for testing purposes. output file goes 
to folder script is run in
"""

#TODO make the json object create in the correct order
import json
import random
from collections import OrderedDict
genders = ["m", "f", "u"]
birthGenders = ['Male', 'Female']
bloodTypes = ["AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-", "U"] 
alcohol = ["", "Normal", ""]
true = True
false = False
bool = [true, false]

users = []
def generate(num):
    num = int(num)
    outputFile = open("users.json", "w")
    for i in range(0, num):
        user = OrderedDict ({
            "nhi": "ZXC"+''.join(random.sample("0123456789", 4)),
            "firstName": "Donor",
            "preferredFirstName": "Don",
            "middleName": "or",
            "lastName": "#"+str(i),
            "dateOfBirth": {"year":1977+i%10, "month": i%12, "day": i%20},
            "dateOfDeath": {"year":2015+i%10, "month": i%12, "day": i%20},
            "genderIdentity": random.choice(genders),
            "birthGender": random.choice(birthGenders),
            "height": round(random.uniform(1,2), 2),
            "weight": round(random.uniform(50, 120), 2),
            "bloodType": random.choice(bloodTypes),
            "currentAddress": "42 wallaby way",
            "region": "Sydney",
            
            #copied from example donor, can't be bothered randomising this stuff
            "timeCreated":{"date":{"year":2018,"month":4,"day":10},
  "time":{"hour":16,"minute":3,"second":17,"nano":608000000}},
            "isDeceased": random.choice(bool),
            "alcoholConsumtpion": random.choice(alcohol),
            "smoker": random.choice(bool),
            "homePhone": random.sample("0123456789", 9),
            "cellPhone": random.sample("0123456789", 10),
            "email": "email@gmail.com",
            "contact": {
              "name": "Mother",
                "homePhoneNumber": random.sample("0123456789", 9),
                "cellPhoneNumber": random.sample("0123456789", 10),
                "address": "123 Home Road",
                "region": "Canterbury",
                "email": "mother@gmail.com",
                "relationship": "Mother"
            },
            "lastModified":{"date":{"year":2018,"month":4,"day":10},
              "time":{"hour":16,"minute":3,"second":17,"nano":609000000}},
              "miscAttributes":[],
            "updateHistory":{"2018-04-10T16:03:17.608":"Profile created."},
              "previousMedication":[],
              "currentMedication":[],
              "previousMedicationTimes":{},
              "currentMedicationTimes":{},
            "medicalProcedures": [],
              "changes":[],
              "donorDetails":{},
              "receiverDetails":{},
            "commonOrgans": [],
            "pastDiseases": [],
            "currentDiseases": []
              })
        
        users.append(user)
    
    json_users = json.dumps(users)
    outputFile.write(json_users)
    outputFile.close()
    
generate(input("num of users to generate?: "))