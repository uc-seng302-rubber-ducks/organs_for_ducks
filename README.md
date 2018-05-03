#Creating a runnable JAR

This project is scaffolded by Maven and uses its directory stucture.
The file POM.xml contains the build instructions for Maven.

To use this, Maven must be installed on the machine.
This can be checked by running the command `mvn -version` on Linux.
If it is not installed, it should be installed (see the section on this below).

A tested, runnable jar can be built by navigating into the directory where the project has been downloaded to
and using the command `mvn clean package -DskipTests`. As tests take upwards of 6 minutes to complete removing the `-DskipTests` will run the tests
This will create the directory target with the JAR in it (and remove any previous 
Maven builds for this project).
The project can then by run with the command 
`java -jar target/team-100-Rubber-Duckies-0.0.jar`

or by navigating to the target directory and running the command without `target/`

The program will create a hidden directory structure to hold the data files
in the home directory of the user.


#Installing Maven

On Linux: run the command `sudo apt-get install maven`

On OSX: using Homebrew, run the command `brew install maven`

On Windows: download Maven from http://maven.apache.org/
and unzip it to the desired destination (preferably C:\usr\bin).
Using Windows System Properties, configure the path for maven to point to the 
unzipped folder.


#Importing the project into Intellij

To import the source code, clone the git repository
with the command `git clone git@eng-git.canterbury.ac.nz:seng302-2018/team-100.git`


On the IntelliJ landing screen, select the option Import Project.
Select the directory it is saved in.
Select Maven and click import.


The project should now be open. 

# Basic Project Structure
 - `src/` Your application source
 - `doc/` User and design documentation
 - `doc/examples/` Demo example files for use with your application
 
# Using the Application
 
 The Command Line Interface(CLI) can be operated by typing the appropriate commands and using flags to assign 
 inputs to the proper variables inside the command. All commands save the state on successful
 execution. If data is corrupted exiting the command interface by closing the terminal window
 will preserve the state from the last successful operation. 
 
 The following commands are avaliable in the application:
   - `view`
   - `register`
   - `update`
   - `delete`
   - `help`
   - `quit`
    
Entering the command and then help will provide more details regarding the usage and avaliable flags.
`help` and `quit` have no flags.


# Command Details
- view 
 ```
    Usage: view [-a] [] [-dob=<dobString>] [-f=<firstName>] [-id=<id>]
                   [-l=<lastName>]
                   
       View all currently registered donors based on set parameters.
         -h, help              display a help message
         -a, all, -all
             -dob = <dobString>
         -f, -n, -name, -fname = <firstName>
             -id=<id>
         -l, -lname = <lastName>
  ```
         
 - register
 ```
 Usage: register [-h] [-a=<currentAddress>] [-b=<bloodType>] [-dod=<dodString>]
                 [-g=<gender>] [-he=<height>] [-r=\<region>] [-w=<weight>]
                 <firstName>\<lastName> <dobString>
 first name, last name, and dob are required. 
 All other are optional and must be tagged
       <firstName>
       <lastName>
       <dobString>             format 'yyyy-mm-dd'
   -a, -addr, -currentAddress=<currentAddress> Current address (Address line 1)
   -b, -bloodType=<bloodType>  blood type
   -dod=<dodString>        Date of death. same formatting as dob
   -g, -gender=<gender>        gender.
   -h, help                    display a help message
   -he, -height=<height>   height in m. e.g. 1.85
   -r, -region=<region>        Region (Address line 2)
   -w, -weight=<weight>        weight in kg e.g. 87.3
  ```
   
 - update
 ```
 Usage: update [-h]
 Update details of a single donor
 Use 'update add' or 'update remove'to add or remove organs
   -h, help, -help
 Commands:
   add
   remove
   details  Use -id to identify the the donor. All other tags will update values

```

- update add
 ```
    Usage: add [-d=<dobString>] [-f=<fname>] [-id=<id>] [-l=<lname>] [<organs>]...
          [<organs>]...           List of organs to be added. Use underscores for
                                    multi-word organs (e.g. bone_marrow)
      -d, -dob=<dobString>        Donor's date of birth in format yyyy-MM-dd
      -f, -n, -name, -fname=<fname> Donor's first name. If their name is a single
                                    word, it can be entered here
          -id=<id>                ID number of the donor to be updated
      -l, -lname=<lname>          Donor's surname
```
    
 - update remove
 ```
 Usage: remove [-d=<dobString>] [-f=<fname>] [-id=<id>] [-l=<lname>]
               [<organs>]...
       [<organs>]...           List of organs to be added. Use underscores for
                                 multi-word organs (e.g. bone_marrow)
   -d, -dob=<dobString>        Donor's date of birth in format yyyy-MM-dd
   -f, -n, -name, -fname=<fname> Donor's first name. If their name is a single
                                 word, it can be entered here
       -id=<id>                ID number of the donor to be updated
   -l, -lname=<lname>          Donor's surname
```

- delete
```
Usage: delete [-h] <firstName> <lastName> <dobString>
first name, lastname, DOB. Required will locate donor and prompt for deletion
      <firstName>
      <lastName>
      <dobString>             format 'yyyy-mm-dd'
  -h, help                    display a help message
```

- quit

    quit will exit the application and save the current state.
