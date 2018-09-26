package odms.commons.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import odms.commons.model.*;
import odms.commons.model._enum.Directory;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.model.dto.LoginResponse;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Json Handler to import and save data
 */
public class JsonHandler extends DataHandler {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DELIMITER = "/";

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
        try (FileWriter writer = new FileWriter(outFile)) {
            String usersString = gson.toJson(users);
            writer.write(usersString);
        }
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
            Gson gson = new GsonBuilder().setDateFormat(YYYY_MM_DD_HH_MM_SS)
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
     * Takes a get user response and then decodes it into a valid user
     * <p>
     * The response passed must be a from the /getUsers/{nhi} endpoint
     *
     * @param response repsonse to be parsed
     * @return the user contained in the response
     * @throws IOException Thrown if response body cannot be read
     */
    public User decodeUser(Response response) throws IOException {
        Gson gson = new GsonBuilder().setDateFormat(YYYY_MM_DD_HH_MM_SS)
                .create();
        String responseBodyString = null;
        try {
            responseBodyString = response.body().string();
        } catch (NullPointerException e) {
            Log.severe("No response returned with body", e);
            return null;
        }
        if (responseBodyString != null) {
            User user = gson.fromJson(responseBodyString, User.class);
            user.getReceiverDetails().setAttachedUser(user);
            user.getDonorDetails().setAttachedUser(user);
            if (user.getContact() != null) {
                user.getContact().setAttachedUser(user);
            }
            return user;
        }
        return null;
    }

    public String decodeLogin(Response response) {
        Gson gson = new GsonBuilder().create();
        String responseBodyString;
        try {
            responseBodyString = response.body().string();
        } catch (IOException e) {
            Log.severe("Invalid HTTP response recieved", e);
            return null;
        }
        LoginResponse loginResponse = gson.fromJson(responseBodyString, LoginResponse.class);
        return loginResponse.getToken();
    }

    /**
     * Returns a decoded clinician from the response received from "/clinicians/{staffId}
     *
     * @param response response to be parsed
     * @return clinician contained in the Json
     * @throws IOException If the response body cannot be read
     */
    public Clinician decodeClinician(Response response) throws IOException {
        Gson gson = new GsonBuilder().setDateFormat(YYYY_MM_DD_HH_MM_SS)
                .create();
        return gson.fromJson(response.body().string(), Clinician.class);
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
                .create();
        try (FileWriter writer = new FileWriter(outFile)) {
            String usersString = gson.toJson(clinicians);
            writer.write(usersString);
        }
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
            Gson gson = new GsonBuilder().setDateFormat(YYYY_MM_DD_HH_MM_SS)
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

        try (FileWriter writer = new FileWriter(outFile)) {
            String adminsString = gson.toJson(admins);
            writer.write(adminsString);
        }
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
            Gson gson = new GsonBuilder().setDateFormat(YYYY_MM_DD_HH_MM_SS)
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
    public void saveChangelog(ArrayList<Change> changes, String name) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory() + DELIMITER + name + "changelog.json");


        if (outFile.exists()) {
            changes.addAll(importHistoryFromFile(name)); //don't worry about the position in the array JSON is not parsed in order anyway
        }

        outFile.createNewFile(); //creates new file if users does not exist
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json)))
                .create();
        try (FileWriter writer = new FileWriter(outFile)) {
            String usersString = gson.toJson(changes);
            writer.write(usersString);
        }
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
    public List<Change> importHistoryFromFile(String name) throws FileNotFoundException {

        File infile = new File(Directory.JSON.directory() + DELIMITER + name + "changelog.json");
        if (!infile.exists()) {
            return new ArrayList<>();
        }

        Gson gson = new GsonBuilder().setDateFormat(YYYY_MM_DD_HH_MM_SS)
                .create();
        Reader reader = new FileReader(infile);
        Change[] changes = gson.fromJson(reader, Change[].class);
        Log.info("successfully loaded changelog from file");
        return new ArrayList<>(Arrays.asList(changes));

    }


    public Administrator decodeAdmin(Response response) throws IOException {

        Gson gson = new GsonBuilder().setDateFormat(YYYY_MM_DD_HH_MM_SS)
                .create();
        return gson.fromJson(response.body().string(), Administrator.class);
    }

    /**
     * converts a raw response into a list of transplant details
     *
     * @param response response to decode
     * @return all valid TransplantDetails objects. will return empty list if none
     *
     * @throws IOException on undecodable json
     */
    public List<TransplantDetails> decodeTransplantList(Response response) throws IOException {
        return new Gson().fromJson(response.body().string(), new TypeToken<List<TransplantDetails>>() {
        }.getType());
    }

    public String decodeProfilePicture(ResponseBody body, String userId, String format) throws IOException {
        return PhotoHelper.createTempImageFile(body.bytes(), userId, format);

    }


    public Set decodeCountries(Response response) throws IOException {
        return new Gson().fromJson(response.body().string(), new TypeToken<HashSet<String>>() {
        }.getType());
    }

    public Collection<Clinician> decodeClinicians(String response) {
        return new Gson().fromJson(response, new TypeToken<Collection<Clinician>>() {
        }.getType());
    }

    public List<String> decodeQueryResult(ResponseBody body) throws IOException {
        return new Gson().fromJson(body.string(), new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public List<AvailableOrganDetail> decodeAvailableOrgansList(Response response) throws IOException {
        return new Gson().fromJson(response.body().string(), new TypeToken<List<AvailableOrganDetail>>() {
        }.getType());
    }

    /**
     * converts a raw response into a list of matching organs
     *
     * @param response response to decode
     * @return a map of matching organs. will return empty map if none
     * @throws IOException on undecodable json
     */
    public List<TransplantDetails> decodeMatchingOrgansList(Response response) throws IOException {
        return new Gson().fromJson(response.body().string(), new TypeToken<List<TransplantDetails>>() {
        }.getType());
    }

    /**
     * Decodes raw json string into a collection of appointments
     *
     * @param bodyString raw json string
     * @return Collection of appointments
     */
    public Collection<Appointment> decodeAppointments(String bodyString) {
        return new Gson().fromJson(bodyString, new TypeToken<Collection<Appointment>>() {
        }.getType());
    }

    /**
     * Decodes raw json string into an appointment object
     *
     * @param bodyString raw json string
     * @return an appointment object
     */
    public Appointment decodeOneAppointment(String bodyString) {
        return new Gson().fromJson(bodyString, new TypeToken<Appointment>() {
        }.getType());
    }

    /**
     * Decodes raw json string into a collection of localDateTime objects
     *
     * @param bodyString raw json string
     * @return a collection of LocalDateTimes
     */
    public Collection<LocalDateTime> decodeDateTimes(String bodyString) {
        return new Gson().fromJson(bodyString, new TypeToken<Collection<LocalDateTime>>() {

        }.getType());
    }

    /**
     * Decodes raw json stirng into a collection of OrgansWithDisqualification
     * @param bodyString raw json string
     * @return Collection of OrgansWithDisqualifications
     */
    public List<OrgansWithDisqualification> decodeDisqualified(String bodyString) {
        return new Gson().fromJson(bodyString, new TypeToken<List<OrgansWithDisqualification>>() {
        }.getType());
    }

    /**
     * decodes a raw json string of a bloodTest
     * @param body response body as a string containing a single blood test
     * @return the blood test
     */
    public BloodTest decodeBloodTest(String body) {
        return new Gson().fromJson(body, new TypeToken<BloodTest>(){}.getType());
    }

    /**
     * decodes a raw json string for a collection of blood tests
     *
     * @param body raw json body for the blood tests
     * @return collection of the blood tests
     */
    public Collection<BloodTest> decodeBloodTests(String body) {
        return new Gson().fromJson(body, new TypeToken<Collection<BloodTest>>(){}.getType());
    }
}

