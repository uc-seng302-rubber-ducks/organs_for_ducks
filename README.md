# Creating a runnable JAR

This project is scaffolded by Maven and uses its directory stucture.
The file POM.xml contains the build instructions for Maven.

To use this, Maven must be installed on the machine.
This can be checked by running the command `mvn -version` on Linux.
If it is not installed, it should be installed (see the section on this below).

A tested, runnable jar can be built by navigating into the directory where the project has been downloaded to
and using the command `mvn clean package`.
This will create the directory target with multiple JARs (and remove any previous 
Maven builds for this project).
The server can be run with:
`java -jar target/server/server-X.X.jar` (or similar) 
and the client can be run with:
`java -jar target/client/client-X.X.jar` (or similar)

or by navigating to the target directory and running the command without `target/`

The program will create a hidden directory structure to hold the data files
in the home directory of the user.


# Installing Maven

On Linux: run the command `sudo apt-get install maven`

On OSX: using Homebrew, run the command `brew install maven`

On Windows: download Maven from http://maven.apache.org/
and unzip it to the desired destination (preferably C:\usr\bin).
Using Windows System Properties, configure the path for maven to point to the 
unzipped folder.


# Importing the project into Intellij

To import the source code, clone the git repository
with the command `git clone git@eng-git.canterbury.ac.nz:seng302-2018/team-100.git`


On the IntelliJ landing screen, select the option Import Project.
Select the directory it is saved in.
Select Maven and click import.


The project should now be open. 

# Basic Project Structure
 - `client/src/` Application source code for the client
 - `commons/src/` Application source code for classes common both client and server
 - `server/src/` Application source code for the server
 
# Using the Application
 
 The Command Line Interface(CLI) can be operated by opening the app and logging in as an admin. A CLI tab will visible, which contains and embedded command line. All commands save the state on successful
 execution. If data is corrupted exiting the command interface by closing the terminal window
 will preserve the state from the last successful operation. 
 
 The following commands are available in the application:
   - `view`
   - `create`
   - `update`
   - `delete`
   - `help`
   - `sql`
    
Entering the command and then `help` or `-h` will provide more details regarding the usage and avaliable flags.
`help` and `quit` have no flags.


# Command Details
- view 

 ```
  Usage:        view [-h] <id> -u
  Alternative:  view <id> -c
  View a single user, clinician or admin profile. 
  The id is required to select the profile to view. All others are optional and must be tagged.
    
   <id>                NHI for users
                       staffID for clinicians
                       username for admins
                        
    -h, help           display a help message
    -u, -user          search for the specified user
    -c, -clinician     search for the specified clinician
    -a, -admin         search for the specififed admin
  ```
          
 ```
  Useage: view all [-h] [-c=<count>] [-i=<index>] [-n=<name>] [-r=<region>] [-g=<gender>]
  View all currently registered users based on set parameters.
    
  All fields are optional
   -c, -count=<count>                  How many results to return. Default = 10
   -i, -index, -startIndex=<index>     Where in the set to start returning results from. Default = 0
   -n, -name=<name>                    First, middle or last name to search by
   -r, -region=<region>                Region to filter the search results by
   -g, -gender=<gender>                Birth gender to filter the search results by
 ```
         
 - create

 ```            
 Usage: create user [-h] <NHI> <firstName> <dobString> [-m=<middleName>] [-l=<lastName>]
                [-dod=<dodString>] [-b=<bloodType>] [-g=<birthGender>] [-gi=<genderIdentity>] 
                [-he=<height>] [-w=<weight>] [-n=<streetNumber>] [-s=<streetName>] [-c=<city>] 
                [-r=<region>] [-ne=<neighborhood>] [-z=<zipCode>] [-co=<country>]
  NHI, first name and dob are required.
  All others are optional and must be tagged
  
   -h, help                                 Display a help message
   <NHI>                                    NHI 'ABC1234'
   <firstName>
   <dobString>                              Date of birth. Format 'yyyy-mm-dd'
   
   -m,   -mname=<middleName>
   -l,   -lname=<lastName>
   -pn,  -preferredName=<preferredName>
     
   -dod=<dodString>                          Date of death. Same formatting as dob
   -he,  -height=<height>                    height in m. e.g. 1.85
   -w,   -weight=<weight>                    weight in kg e.g. 87.3
   -b,   -bloodType=<bloodType>
   -g,   -gender=<birthgender>               Gender which the user was born with
   -gi,  -genderIdentity=<genderIdentity>    Gender which the user identifies as
   -smo, -smoker=<number>                    Flag to specify the user as a smoker.
                                             0 for false, 1 for true
   -ac, -alcoholConsumption=<number>         Alcohol consumption level for the user.
                                             0 for None, 1 for Low, 2 for Normal, 3 for High
     
   -n,   -streetNumber=<streetNumber>
   -s,   -streetName=<streetName>
   -ne,  -neighborhood=<neighborhood>
   -c,   -city=<city>
   -r,   -region=<region>
   -z,   -zipCode=<zipCode>
   -co,  -country=<country> 
  ```
  
  ```
  Usage: create clinician [-h] <id> <firstName> <password> <region>
  Allows the creation of a clinician.
    
  <id>              staffID
  <firstName>
  <password>
  <region>
  -h, help          Display a help message
  ```
 - update

 ```
 update user details [-h] <NHI> [-newNHI=<newNHI>] [-f=<firstName>] [-m=<middleName>] [-l=<lastName>]
               [-dob=<dobString>] [-dod=<dodString>] [-b=<bloodType>] [-g=<birthGender>] [-gi=<genderIdentity>] 
               [-he=<height>] [-w=<weight>] [-n=<streetNumber>] [-s=<streetName>] [-c=<city>] [-r=<region>] 
               [-ne=<neighborhood>] [-z=<zipCode>] [-co=<country>]
               
  NHI is required, all other fields are optional.
  -h, help                                  Display a help message
  

  <NHI>                                     Used to identify the user to update
  -id,  -nhi, -NHI, -newNHI, -newnhi        nhi to replace the current nhi
  -f,   -fname=<firstName>
  -m,   -mname=<middleName>
  -l,   -lname=<lastName>
  
  -dob=<dobString>                          Date of birth. Format 'yyyy-mm-dd'
  -dod=<dodString>                          Date of death. Same formatting as dob
  -he,  -height=<height>                    height in m. e.g. 1.85
  -w,   -weight=<weight>                    weight in kg e.g. 87.3
  -b,   -bloodType=<bloodType>
  -g,   -gender=<birthgender>               Gender which the user was born with
  -gi,  -genderIdentity=<genderIdentity>    Gender which the user identifies as
  -smo, -smoker=<number>                    Flag to specify the user as a smoker.
                                            0 for false, 1 for true
  -ac, -alcoholConsumption=<number>         Alcohol consumption level for the user.
                                            0 for None, 1 for Low, 2 for Normal, 3 for High
  
  -n,   -streetNumber=<streetNumber>
  -s,   -streetName=<streetName>
  -ne,  -neighborhood=<neighborhood>
  -c,   -city=<city>
  -r,   -region=<region>
  -z,   -zipCode=<zipCode>
  -co,  -country=<country> 

```

```
Usage: update user donate [-h] <nhi> [<rawOrgans>]...
Updates a user's organs to donate.
      <nhi>                   The NHI of the user to be updated
      [<rawOrgans>]...        A list of the organs to be updated separated by
                                spaces prefixed by + or /
                              e.g. +liver /bone_marrow would add liver and
                                remove bone marrow
```

```
Usage: update user receive [-h] <nhi> [<rawOrgans>]...
Updates a user's organs to receive.
      <nhi>                   The NHI of the user to be updated
      [<rawOrgans>]...        A list of the organs to be updated separated by
                                spaces prefixed by + or /
                              e.g. +liver /bone_marrow would add liver and
                                remove bone marrow
```

```
Usage: update clinician [-h] <originalId> [-id=<newId>] [-f=<firstName>] [-m=<middleName>] [-l=<lastName>]
                  [-p=<password>] [-n=<streetNumber>] [-s=<streetName>] [-ne=<neighborhood>] 
                  [-c=<city>] [-r=<region>] [-z=<zipCode>] [-co=<country>]
                  
  Allows the details for a clinician to be updated
  
  <originalId>                               staffID to identify the clinician to udpate
  -id,  -ID, -newId, -newID=<newStaffID>     new staffID to replace the current staffID
  -f,   -fname=<firstName>
  -l,   -lname=<lastName>
  -m,   -mname=<middleName>
  -p,   -password=<password>
  
  -n,   -streetNumber=<streetNumber>
  -s,   -streetName=<streetName>
  -ne,  -neighborhood=<neighborhood>
  -c,   -city=<city>
  -r,   -region=<region>
  -z,   -zipCode=<zipCode>
  -co,  -country=<country>
```


- delete

```
Usage:          delete user <nhi>
Alternative:    delete clinician <staffID>
One of user/clinician fields as well as their corresponding id are required.
Delete either a user or a clinician by using the following subcommands
  
  <nhi>         nhi of the user to be deleted
  <staffID>     staffID of the clinician to be deleted
```

- sql
```
Usage: sql <query>
Command to send a read only sql query to the connected database
```

# Storage
All files used by the program will be stored in the .organs folder of your home directory, and under C:/Users/<Your User>/ for Windows. This contains 4 key items:
1. LOGS
2. CACHE


These files store data for each corresponding role 

# Usage
An Internet connection is required to connect to the database, otherwise the application will fail to run smoothly.

The login for the clinicians are:  
**Staff ID**: 0  
**Password**: admin  

The default login for the default admin is:  
**Username**: default  
**Password**: admin