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
 
 #### Running the jar files
 Either jar can be run using `java -jar client/target/client-x.x.jar` 
 or `java -jar server/target/server-x.x.jar` respectively. 
 To run using a local configuration (both parts running on one machine), start the server jar, then the client once the server has fully loaded.
 to run using the remote server, run the client using the included `start.sh` script. You may need to run `chmod +x ./start.sh` to make it executable.
 Please note that this script can be modified to use any server.
 
 ##### command line arguments
 all command line arguments must be preceeded with `--`.
 
 Some example recognised commands are: `--server.websocket.url=ws://url.goes/here` or `--server.url=http://url.goes/here`
 
 #### Default logins
 An Internet connection is required to connect to the database, otherwise the application will fail to run smoothly.
 
 The login for the default clinician is:  
 **Staff ID**: 0  
 **Password**: admin  
 
 The login for the default admin is:  
 **Username**: default  
 **Password**: admin
 
 #### Using the CLI
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

To enter multiple words for one command option you must use an underscore _ instead of spaces.
E.g. to add the country New Zealand, you  would use -co=New_Zealand (where -co is the flag for countries).



# Storage
All files used by the program will be stored in the .organs folder of your home directory, and under C:/Users/<Your User>/ for Windows. This contains 3 key items:
1. LOGS
2. CACHE
3. TEMP
 

