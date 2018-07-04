package seng302.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import seng302.model.*;
import seng302.model._enum.Directory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Json Handler to import and save data
 */
public final class JsonHandler extends DataHandler {

    /**
     * save the current users in the system to the filename given Based on:
     * https://stackoverflow.com/questions/14996663/is-there-a-standard-implementation-for-a-gson-joda-time-serialiser
     *
     * @param users List of users to save
     * @throws IOException when there is an error writing to the file.
     */
    public boolean saveUsers(Collection<User> users) throws IOException {
        // filters the users collection to one where it doesn't contain deleted users
        users = users.stream().filter(user -> !user.isDeleted()).collect(Collectors.toList());
        removeDeletedUserItems(users);
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
        return true;
    }

    /**
     * Removes all the deleted stuff from each user
     *
     * @param users Collection of users from the application
     */
    private void removeDeletedUserItems(Collection<User> users) {
        for (User user : users) {
            try {
                for (MedicalProcedure p : user.getMedicalProcedures()) {
                    if (p.isDeleted()) {
                        user.getMedicalProcedures().remove(p);
                    }
                }
                for (Disease d : user.getCurrentDiseases()) {
                    if (d.isDeleted()) {
                        user.getCurrentDiseases().remove(d);
                    }
                }
                for (Disease d : user.getPastDiseases()) {
                    if (d.isDeleted()) {
                        user.getPastDiseases().remove(d);
                    }
                }
            } catch (ConcurrentModificationException ex) {
                //Catches the CME that arises from removing an item while the list is 'open' in JFX
                Log.warning("Concurrent modification of user list", ex);
            }
        }
    }

    /**
     * loads the users from a file and returns an list
     *
     * @return list of users present
     * @throws FileNotFoundException when the file cannot be located.
     */

    public List<User> loadUsers(String filename) throws FileNotFoundException {
        try {
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
        } catch (FileNotFoundException e) {
            Log.severe("JsonHandler loadUsers file not found", e);
            throw e;
        } catch (RuntimeException e) {
            Log.severe("JsonHandler loadUsers runtime error", e);
            throw e;
        }
    }


    /**
     * Saves a list of clinicians in the clinicians Json file
     *
     * @param clinicians list of clinicians to save
     * @throws IOException thrown when file does not exist, can be ignored as file will be created
     */
    public boolean saveClinicians(Collection<Clinician> clinicians) throws IOException {
        clinicians = clinicians.stream().filter(clinician -> !clinician.isDeleted()).collect(Collectors.toList());
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
        return true;
    }


    /**
     * Loads the list of registered clinicians for the application
     *
     * @return List of registered clinicians
     * @throws FileNotFoundException thrown if no clinicians exist
     */
    public List<Clinician> loadClinicians(String filename) throws FileNotFoundException {
        try {
            File inFile = new File(filename);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
            Reader reader = new FileReader(inFile);
            Clinician[] clinicians = gson.fromJson(reader, Clinician[].class);
            return new ArrayList<>(Arrays.asList(clinicians));
        } catch (FileNotFoundException e) {
            Log.severe("JsonHandler loadClinicians file not found", e);
            throw e;
        } catch (RuntimeException e) {
            Log.severe("JsonHandler loadClinicians runtime error", e);
            throw e;
        }
    }


    /**
     * Saves the current list of administrators to a JSON file
     *
     * @param admins list of administrators to be saved
     * @throws IOException thrown when file does not exist, can be ignored as file will be created
     */
    public boolean saveAdmins(Collection<Administrator> admins) throws IOException {
        admins = admins.stream().filter(administrator -> !administrator.isDeleted()).collect(Collectors.toList());
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
        return true;
    }

    /**
     * Loads a list of administrators from a JSON file into the current session
     *
     * @return List of administrator accounts
     * @throws FileNotFoundException thrown if the JSON file of administrators does not exist
     */
    public Collection<Administrator> loadAdmins(String filename) throws FileNotFoundException {
        try {
            File inFile = new File(filename);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            Reader reader = new FileReader(inFile);
            Administrator[] administrators = gson.fromJson(reader, Administrator[].class);
            return new ArrayList<>(Arrays.asList(administrators));
        } catch (FileNotFoundException e) {
            Log.severe("JsonHandler Administrator loaded file not found", e);
            throw e;
        } catch (RuntimeException e) {
            Log.severe("JsonHandler loadAdmins runtime error", e);
            throw e;
        }
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

