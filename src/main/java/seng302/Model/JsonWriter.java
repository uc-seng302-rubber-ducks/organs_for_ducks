package seng302.Model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import seng302.Directory;

import java.io.File;
import java.io.FileOutputStream;
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
            for (Organs o: organsDonating) {
                organs.add(o.toString());
            }
            j.put("Organs", organs);

            outerJSON.add(j);
        }
        outFileStream.write(outerJSON.toJSONString().getBytes());
        outFileStream.close();
    }
}
