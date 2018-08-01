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
 
 The following commands are avaliable in the application:
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
    Usage: view [-a] [] [-dob=<dobString>] [-f=<firstName>] [-l=<lastName>]
            [-NHI=<NHI>]
View all currently registered users based on set parameters.
      , -h, help              display a help message
  -a, all, -all
      -dob=<dobString>
  -f, -n, -name, -fname=<firstName>

  -l, -lname=<lastName>
      -NHI, -nhi, -NHI=<NHI>
  ```
         
 - create

 ```
 Usage: create user [-h] [-a=<currentAddress>] [-b=<bloodType>] [-dod=<dodString>]
            [-g=<gender>] [-he=<height>] [-r=<region>] [-w=<weight>]
            <firstName> <lastName> <NHI> <dobString>
first name, last name, and dob are required. all other are optional and must be
tagged
      <firstName>
      <lastName>
      <NHI>                   NHI 'ABC1234'
      <dobString>             format 'yyyy-mm-dd'
  -a, -addr, -currentAddress=<currentAddress>
                              Current address (Address line 1)
  -b, -bloodType=<bloodType>  blood type
      -dod=<dodString>        Date of death. same formatting as dob
  -g, -gender=<gender>        gender.
  -h, help                    display a help message
      -he, -height=<height>   height in m. e.g. 1.85
  -r, -region=<region>        Region (Address line 2)
  -w, -weight=<weight>        weight in kg e.g. 87.3
  ```
  
  ```
  Usage: clinician [-h] <id> <firstName> <password> <region>
Allows the creation of a clinician. ot update use update clinician
      <id>
      <firstName>
      <password>
      <region>
  -h, help                    display a help message
  ```
 - update

 ```
 update user details [-h] [-a=<currentAddress>] [-b=<bloodType>] [-dob=<dobString>]
               [-dod=<dodString>] [-f=<firstName>] [-g=<gender>] [-he=<height>]
               -id=<NHI> [-l=<lastName>] [-newNHI=<newNHI>] [-r=<region>]
               [-w=<weight>]
Use -id to identify the the user. All other tags will update values
  -a, -addr, -currentAddress=<currentAddress>
                              Current address (Address line 1)
  -b, -bloodType=<bloodType>  blood type
      -dob=<dobString>        format 'yyyy-mm-dd'
      -dod=<dodString>        Date of death. same formatting as dob
  -f, -fname=<firstName>
  -g, -gender=<gender>        gender.
  -h, help                    display a help message
      -he, -height=<height>   height in m. e.g. 1.85
      -id, -nhi, -NHI=<NHI>
  -l, -lname=<lastName>
      -newNHI, -newnhi=<newNHI>

  -r, -region=<region>        Region (Address line 2)
  -w, -weight=<weight>        weight in kg e.g. 87.3

```

```
Usage: update user donate [-h] <nhi> [<rawOrgans>]...
Updates a user's organs to donate.
      <nhi>                   The NHI of the user to be updated
      [<rawOrgans>]...        A list of the organs to be updated separated by
                                spaces prefixed by + or /
                              e.g. +liver /bone_marrow would add liver and
                                remove bone marrow
  -h, help
```

```
Usage: update user receive [-h] <nhi> [<rawOrgans>]...
Updates a user's organs to donate.
      <nhi>                   The NHI of the user to be updated
      [<rawOrgans>]...        A list of the organs to be updated separated by
                                spaces prefixed by + or /
                              e.g. +liver /bone_marrow would add liver and
                                remove bone marrow
```

```
Usage: update clinician [-a=<address>] [-f=<firstName>] [-id=<newId>] [-l=<lastName>]
                 [-m=<middleName>] [-p=<password>] [-r=<region>] <originalId>
Allows the details for a clinician to be updated
      <originalId>
  -a, -address=<address>
  -f, -fname=<firstName>
      -id, -ID=<newId>
  -l, -lname=<lastName>
  -m, -mname=<middleName>
  -p, -password=<password>
  -r, -region=<region>
```


- delete

```
Usage: delete
 Command used to start the deletion process. Is required to reach the deletion
subcommands
Commands:
  user       first name, lastname, DOB. Required will locate user and prompt
               for deletion
  clinician  Allows a clinician to be deleted
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