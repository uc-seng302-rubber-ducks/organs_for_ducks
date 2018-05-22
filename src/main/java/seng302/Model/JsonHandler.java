package seng302.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
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
import java.util.Collection;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import seng302.Directory;
import seng302.Service.Log;

/**
 * Json Handler to import and save data
 *
 * @author Josh Burt
 */
public final class JsonHandler {


    /**
     * save the current users in the system to the filename given Based on:
     * https://stackoverflow.com/questions/14996663/is-there-a-standard-implementation-for-a-gson-joda-time-serialiser
     *
     * @param users List of users to save
     * @throws IOException when there is an error writing to the file.
     */
    public static void saveUsers(Collection<User> users) throws IOException {

        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory() + "/users.json");

        if (outFile.exists()) {
            outFile.delete(); //purge old data before writing new data in
        }

        outFile.createNewFile(); //creates new file if users does not exist
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json)))
                .setPrettyPrinting().create();
        FileWriter writer = new FileWriter(outFile);
        String usersString = gson.toJson(users);
        writer.write(usersString);
        writer.close();
        Log.info("Handler: successfully wrote user to file");
    }

    /**
     * loads the users from a file and returns an list
     *
     * @return list of users present
     * @throws FileNotFoundException when the file cannot be located.
     */

    public static List<User> loadUsers(String filename) throws FileNotFoundException {
        File inFile = new File(filename);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Reader reader = new FileReader(inFile);
        User[] users = gson.fromJson(reader, User[].class);
        List<User> results = new ArrayList<>(Arrays.asList(users));

        for (User result : results) {
            result.getReceiverDetails().setAttachedUser(result);
            result.getDonorDetails().setAttachedUser(result);
            if (result.getContact() != null) {
                result.getContact().setAttachedUser(result);
            }
        }
        Log.info("successfully loaded user from file");
        return results;

    }


    /**
     * Saves a list of clinicians in the clinicians Json file
     *
     * @param clinicians list of clinicians to save
     * @throws IOException thrown when file does not exist, can be ignored as file will be created
     */
    public static void saveClinicians(Collection<Clinician> clinicians) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory() + "/clinicians.json");

        if (outFile.exists()) {
            outFile.delete(); //purge old data before writing new data in
        }

        outFile.createNewFile(); //creates new file
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json)))
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        FileWriter writer = new FileWriter(outFile);
        String usersString = gson.toJson(clinicians);
        writer.write(usersString);
        writer.close();
        Log.info("successfully wrote clinicians to file");
    }


    /**
     * Loads the list of registered clinicians for the application
     *
     * @return List of registered clinicians
     * @throws FileNotFoundException thrown if no clinicians exist
     */
    public static List<Clinician> loadClinicians(String filename) throws FileNotFoundException {
        File inFile = new File(filename);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Reader reader = new FileReader(inFile);
        Clinician[] clinicians = gson.fromJson(reader, Clinician[].class);
        return new ArrayList<>(Arrays.asList(clinicians));
    }


    /**
     * Saves the current list of administrators to a JSON file
     *
     * @param admins list of administrators to be saved
     * @throws IOException thrown when file does not exist, can be ignored as file will be created
     */
    public static void saveAdmins(Collection<Administrator> admins) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory() + "/administrators.json");

        if (outFile.exists()) {
            outFile.delete(); // purge old data to get a clean file to write new data to
        }

        outFile.createNewFile(); // creates new file
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json)))
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        FileWriter writer = new FileWriter(outFile);
        String adminsString = gson.toJson(admins);
        writer.write(adminsString);
        writer.close();
    }

    /**
     * Loads a list of administrators from a JSON file into the current session
     *
     * @return List of administrator accounts
     * @throws FileNotFoundException thrown if the JSON file of administrators does not exist
     */
    public static Collection<Administrator> loadAdmins() throws FileNotFoundException {

        File inFile = new File(Directory.JSON.directory() + "/administrators.json");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Reader reader = new FileReader(inFile);
        Administrator[] administrators = gson.fromJson(reader, Administrator[].class);
        return new ArrayList<>(Arrays.asList(administrators));
    }

    /**
     * Saves a personal changelog for each user
     *
     * @param changes list of changes to be added top the change log
     * @param name    User name to be changed must be passed in format firstname[_middlename(s)]_lastname
     * @throws IOException thrown if no file exists can be ignored as file is created
     */
    public static void saveChangelog(ArrayList<Change> changes, String name) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory() + "/" + name + "changelog.json");


        if (outFile.exists()) {
            changes.addAll(importHistoryFromFile(name)); //don't worry about the position in the array JSON is not parsed in order anyway
        }

        outFile.createNewFile(); //creates new file if users does not exist
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json)))
                .create();
        FileWriter writer = new FileWriter(outFile);
        String usersString = gson.toJson(changes);
        writer.write(usersString);
        writer.close();
        Log.info("successfully wrote changelog to file");
    }


    /**
     * imports the changelog data held for a passed in user
     *
     * @param name name of user whos change data is wanted. must be passed in format
     *             firstname[_middlename(s)]_lastname
     * @return List of changes that have occured on this users profile
     * @throws FileNotFoundException thrown if file is not found. Indicates no changes have been made
     *                               to this user.
     */
    public static List<Change> importHistoryFromFile(String name) throws FileNotFoundException {

        File infile = new File(Directory.JSON.directory() + "/" + name + "changelog.json");
        if (!infile.exists()) {
            return new ArrayList<>();
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Reader reader = new FileReader(infile);
        Change[] changes = gson.fromJson(reader, Change[].class);
        Log.info("successfully loaded changelog from file");
        return new ArrayList<>(Arrays.asList(changes));

    }


}

