package seng302.Model;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import seng302.Directory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for writing to a JSON file to allow data persistence.
 * Class is deprecated please use JsonHandler
 * @author Josh Burt
 */
@Deprecated
public final class JsonWriter {

    /**
     * Method for saving in the list of donors at the end of a session. Should be called before closing the application.
     *
     * Takes an Arraylist of the donors present in the current session and saves them to a hidden folder on the user profile.
     * Stores them as a JSON object. Any previous JSON files that exist at the current directory are overwritten.
     * @param donors list of donors maintained by the application
     * @throws IOException Caused by the system being unable to write to the specified file
     */
    public static void saveCurrentDonorState(ArrayList<Donor> donors) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        //System.out.println("Created Directories:" + Directory.JSON.directory());
        File outFile = new File(Directory.JSON.directory()+"/donors.json");

        if (outFile.exists()){
            outFile.delete(); //purge old data before writing new data in
        }

        outFile.createNewFile(); //creates new file if donors does not exist
        FileOutputStream outFileStream = new FileOutputStream(outFile, false);
        JSONArray outerJSON = new JSONArray();

        for (Donor d : donors){
            JSONObject j = new JSONObject();
            j.put("Name", d.getName());

            j.put("DOB", d.getDateOfBirth().toString());

            if(d.getDateOfDeath() == null){
                j.put("DOD", "null");
            }else {
                j.put("DOD", d.getDateOfDeath().toString());
            }
            j.put("Gender", d.getGender());
            j.put("Height", d.getHeight());
            j.put("Weight", d.getWeight());
            j.put("Blood Type", d.getBloodType());
            j.put("Current Address", d.getCurrentAddress());
            j.put("Region", d.getRegion());
            j.put("Time Created", d.getTimeCreated().toString());
            j.put("Last Modified", d.getLastModified().toString());
            j.put("is Deceased", d.getDeceased());
            JSONArray organs = new JSONArray();
            HashSet<Organs> organsDonating = d.getOrgans();
            if (organsDonating == null) {
                j.put("Organs", null);
            } else {
                for (Organs o : organsDonating) {
                    organs.add(o.toString());
                }
                j.put("Organs", organs);
            }
            JSONArray miscAttributes = new JSONArray();
            ArrayList<String> attributes = d.getMiscAttributes();
            if (attributes == null){
                j.put("Misc", null);
            } else {
                for(String a : attributes){
                    miscAttributes.add(a);
                }
            }
            JSONArray previousMedication = new JSONArray();
            ArrayList<String> previousMeds = d.getPreviousMedication();
            if (previousMeds == null){
                j.put("Previous Medication", null);
            } else{
                for (String med : previousMeds) {
                    previousMedication.add(med);
                }
                j.put("Previous Medication", previousMedication);
            }

            JSONArray currentMedication = new JSONArray();
            ArrayList<String> currentMeds = d.getCurrentMedication();
            if (currentMeds == null){
                j.put("Current Medication", null);
            } else{
                for (String med : currentMeds) {
                    currentMedication.add(med);
                }
                j.put("Current Medication", currentMedication);
            }
            j.put("Misc", miscAttributes); //why is this here?
            JSONArray currentMedicationTimeStamps = new JSONArray();
            HashMap<String, ArrayList<LocalDateTime>> currentMedsTimes = d.getCurrentMedicationTimes();
            for(String key : currentMedsTimes.keySet()){
                JSONArray times = new JSONArray();
                ArrayList<LocalDateTime> dateTimes = currentMedsTimes.get(key);
                for (LocalDateTime t : dateTimes){
                    times.add((String) t.toString());
                }
                JSONObject hashMapGlue = new JSONObject();
                hashMapGlue.put(key, times);
                currentMedicationTimeStamps.add(hashMapGlue);
            }
            j.put("Current Medication TimeStamps", currentMedicationTimeStamps);
            JSONArray previousMedicationTimeStamps = new JSONArray();
            HashMap<String, ArrayList<LocalDateTime>> previousMedsTimes = d.getPreviousMedicationTimes();
            for(String key : previousMedsTimes.keySet()){
                JSONArray times = new JSONArray();
                ArrayList<LocalDateTime> dateTimes = previousMedsTimes.get(key);
                for (LocalDateTime t : dateTimes){
                    times.add((String) t.toString());
                }
                JSONObject hashMapGluePre = new JSONObject();
                hashMapGluePre.put(key, times);
                previousMedicationTimeStamps.add(hashMapGluePre);
            }
            j.put("Previous Medication TimeStamps", previousMedicationTimeStamps);

            outerJSON.add(j);


        }
        outFileStream.write(outerJSON.toJSONString().getBytes());
        outFileStream.close();
        System.out.println(donors.size() + " Donors Sucessfully saved");
    }


    /**
     * Used to generate a machine and human readable changelog.
     * The old changelog is imported into the method as a JSONArray
     * The new change is then mapped to a JSON object with the timestamp attached
     * The JSON object is then placed into the JSONArray and written back to the orignal place overwritting the file that is there
     *
     * @param toWriteArray change to be written into the changelog.
     */
    public static void changeLog(String[] toWriteArray){
        try {
            if(!Files.exists(Paths.get(Directory.JSON.directory()))) {
                Files.createDirectories(Paths.get(Directory.JSON.directory()));
            }
            File outfile = new File(Directory.JSON.directory() +"/changelog.json");
            boolean isCreated  = outfile.createNewFile(); //does nothing if file does not exist
            JSONArray changeFile;
            if (isCreated) {
                changeFile = new JSONArray();
            } else {
                JSONParser parser = new JSONParser(); //read file first as it must be created now to append data to any previous JSON changelogs.
                changeFile = (JSONArray) parser.parse(new FileReader(outfile));
            }
            JSONObject newChange = new JSONObject();
            String toWrite = "";
            for(String s : toWriteArray){
                toWrite += s + " ";
            }
            newChange.put(DateTime.now(), toWrite);
            changeFile.add(newChange);
            FileOutputStream outStream = new FileOutputStream(outfile, false);
            outStream.write(changeFile.toJSONString().getBytes());
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to generate a machine and human readable changelog.
     * The old changelog is imported into the method as a JSONArray
     * The new change is then mapped to a JSON object with the timestamp attached
     * The JSON object is then placed into the JSONArray and written back to the orignal place overwritting the file that is there
     *
     * @param toWrite change to be written into the changelog.
     */
    public static void changeLog(ArrayList<String> toWrite, String name){
        try {
            if(!Files.exists(Paths.get(Directory.JSON.directory()))) {
                Files.createDirectories(Paths.get(Directory.JSON.directory()));
            }
            File outfile = new File(Directory.JSON.directory() +"/"+name+"changelog.json");
            boolean isCreated  = outfile.createNewFile(); //does nothing if file does exist
            JSONArray changeFile;
            if (isCreated) {
                changeFile = new JSONArray();
            } else {
                JSONParser parser = new JSONParser(); //read file first as it must be created now to append data to any previous JSON changelogs.
                changeFile = (JSONArray) parser.parse(new FileReader(outfile));
            }
            for (String change : toWrite) {
                JSONObject newChange = new JSONObject();
                newChange.put(DateTime.now(), change);
                changeFile.add(newChange);
            }
                FileOutputStream outStream = new FileOutputStream(outfile, false);
                outStream.write(changeFile.toJSONString().getBytes());
                outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves clinicians as created by the application
     *
     * @param clinicians list of clinicians to save
     */
    public static void saveClinicians(ArrayList<Clinician> clinicians){
        try{
            if(!Files.exists(Paths.get(Directory.JSON.directory()))){
                Files.createDirectories(Paths.get(Directory.JSON.directory()));
            }
            File outfile = new File(Directory.JSON.directory()+"/clinicians.json");
            FileOutputStream fileOutputStream = new FileOutputStream(outfile, false);

            JSONArray outerJSON = new JSONArray();
            for(Clinician c : clinicians){
                JSONObject j = new JSONObject();
                j.put("Name", c.getFullName());
                j.put("Staff Id", c.getStaffId());
                j.put("Work Address", c.getWorkAddress());
                j.put("Region", c.getRegion());
                j.put("Password", c.getPassword());
                j.put("Date Created", c.getDateCreated().toString());
                j.put("Last Modified", c.getDateLastModified().toString());

                outerJSON.add(j);
            }

            fileOutputStream.write(outerJSON.toJSONString().getBytes());
            fileOutputStream.close();
            System.out.println("Clinician Successfully saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

