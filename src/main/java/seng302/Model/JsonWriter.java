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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class for writing to a JSON file to allow data persistence.
 *
 * @author Josh Burt
 */
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
        FileOutputStream outFileStream = new FileOutputStream(outFile, true);
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
            outerJSON.add(j);
        }
        outFileStream.write(outerJSON.toJSONString().getBytes());
        outFileStream.close();
    }


    /**
     * Used to generate a machine and human readable changelog.
     * The old changelog is imported into the method as a JSONArray
     * The new change is then mapped to a JSON object with the timestamp attached
     * The JSON object is then placed into the JSONArray and written back to the orignal place overwritting the file that is there
     *
     * @param toWrite change to be written into the changelog.
     */
    public void changeLog(String toWrite){
        try {
            Files.createDirectory(Paths.get(Directory.JSON.directory()));
            File outfile = new File(Directory.JSON.directory() +"/changelog.json");
            outfile.createNewFile(); //does nothing if file does not exist
            JSONParser parser = new JSONParser(); //read file first as it must be created now to append data to any previous JSON changelogs.
            JSONArray changeFile = (JSONArray) parser.parse(new FileReader(outfile));
            JSONObject newChange = new JSONObject();
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
}
