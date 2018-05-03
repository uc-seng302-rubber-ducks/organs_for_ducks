package seng302.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import seng302.Directory;

/**
 * Json Handler to import and save data
 *
 * @author Josh Burt
 */
public final class JsonHandler {

    //Initialise attributes
    private BufferedWriter fileWriter;

    /**
     * save the current users in the system to the filename given
     * Based on: https://stackoverflow.com/questions/14996663/is-there-a-standard-implementation-for-a-gson-joda-time-serialiser
     * @param users List of users to save
     * @throws IOException when there is an error writing to the file.
     */
    public static void saveUsers(ArrayList<User> users) throws IOException {

        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory()+"/donors.json");

        if (outFile.exists()){
            outFile.delete(); //purge old data before writing new data in
        }

        outFile.createNewFile(); //creates new file if donors does not exist
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json))).setPrettyPrinting().create();
        FileWriter writer = new FileWriter(outFile);
        String usersString = gson.toJson(users);
        writer.write(usersString);
        writer.close();
    }

    /**
     * loads the users from a file and returns an Arraylist
     * @return list of donors present
     * @throws FileNotFoundException  when the file cannot be located.
     */

    public static ArrayList<User> loadUsers() throws FileNotFoundException {
        ArrayList<User> results = new ArrayList<>();
        File inFile = new File(Directory.JSON.directory() + "/donors.json");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Reader reader =new FileReader(inFile);
        User[] donors = gson.fromJson(reader, User[].class);
        results.addAll(Arrays.asList(donors));

        for (User result : results) {
            result.getReceiverDetails().setAttachedUser(result);
            result.getDonorDetails().setAttachedUser(result);
            if (result.getContact() != null) {
                result.getContact().setAttachedUser(result);
            }
            //TODO probably do the same with Receiver details
        }
        return results;

    }


    /**
     * Saves a list of clinicians in the clinicians Json file
     *
     * @param clinicians list of clinicians to save
     * @throws IOException thrown when file does not exist, can be ignored as file will be created
     */
    public static void saveClinicians(ArrayList<Clinician> clinicians) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory()+"/clinicians.json");

        if (outFile.exists()){
            outFile.delete(); //purge old data before writing new data in
        }

        outFile.createNewFile(); //creates new file if donors does not exist
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json)))
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        FileWriter writer = new FileWriter(outFile);
        String usersString = gson.toJson(clinicians);
        writer.write(usersString);
        writer.close();
    }


    /**
     * Loads the list of registered clinicians for the application
     * @return List of registered clinicians
     * @throws FileNotFoundException thrown if no clinicians exist
     */
    public static ArrayList<Clinician> loadClinicians() throws FileNotFoundException {
        ArrayList<Clinician> results = new ArrayList<>();
        File inFile = new File(Directory.JSON.directory() + "/clinicians.json");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Reader reader =new FileReader(inFile);
        Clinician[] clinicians = gson.fromJson(reader, Clinician[].class);
        results.addAll(Arrays.asList(clinicians));
        return results;
    }

    /**
     * Saves a personal changelog for each donor
     *
     * @param changes list of changes to be added top the change log
     * @param name User name to be changed must be passed in format firstname[_middlename(s)]_lastname
     * @throws IOException thrown if no file exists can be ignored as file is created
     */
    public static void saveChangelog(ArrayList<Change> changes, String name) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory()+"/"+name+"changelog.json");


        if (outFile.exists()){
            changes.addAll(importHistoryFromFile(name)); //dont worry about the position in the array JSON is not parsed in order anyway
        }

        outFile.createNewFile(); //creates new file if donors does not exist
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json))).create();
        FileWriter writer = new FileWriter(outFile);
        String usersString = gson.toJson(changes);
        writer.write(usersString);
        writer.close();
    }


    /**
     * imports the changelog data held for a passed in user
     *
     * @param name name of user whos change data is wanted. must be passed in format firstname[_middlename(s)]_lastname
     * @return List of changes that have occured on this users profile
     * @throws FileNotFoundException thrown if file is not found. Indicates no changes have been made to this user.
     */
    public static ArrayList<Change> importHistoryFromFile(String name) throws FileNotFoundException {
        ArrayList<Change> results = new ArrayList<>();

        File infile = new File(Directory.JSON.directory()+"/"+name+"changelog.json");
        if(!infile.exists()){
            return new ArrayList<>();
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Reader reader =new FileReader(infile);
        Change[] changes = gson.fromJson(reader, Change[].class);
        results.addAll(Arrays.asList(changes));
        return results;
    }




}

