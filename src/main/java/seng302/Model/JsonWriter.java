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

public final class JsonWriter {


    public static void saveCurrentDonorState(ArrayList<Donor> donors) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        System.out.println("Created Directories:" + Directory.JSON.directory());
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
            outerJSON.add(j);
        }
        outFileStream.write(outerJSON.toJSONString().getBytes());
        outFileStream.close();
    }
}
