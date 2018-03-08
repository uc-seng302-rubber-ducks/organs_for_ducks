# User Manual

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
