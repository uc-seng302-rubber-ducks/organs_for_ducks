package seng302;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class JsonWriter {


    public static void saveCurrentDonorState(ArrayList<Donor> donors) throws IOException {
        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        System.out.println("Created Directories:" + Directory.JSON.directory());
        File outFile = new File(Directory.JSON.directory()+"/donors.json"); //#TODO: change to absolute file paths and create hidden folder for user data
        if (outFile.exists()){
            outFile.delete(); //purge old data before writing new data in
        }

        outFile.createNewFile(); //creates new file if donors does not exist
        FileOutputStream outFileStream = new FileOutputStream(outFile, true);

        for (Donor d : donors){
            JSONObject j = new JSONObject();
            j.put("name", d.getName());
            j.put("DOB", d.getDateOfBirth());
            j.put("DOD", d.getDateOfDeath());
            j.put("Gender", d.getGender());
            j.put("Height", d.getHeight());
            j.put("Weight", d.getWeight());
            j.put("Blood Type", d.getBloodType());
            j.put("Current Address", d.getCurrentAddress());
            j.put("Region", d.getRegion());
            j.put("Time Created", d.getTimeCreated());
            outFileStream.write(j.toJSONString().getBytes());
        }
        outFileStream.close();
    }
}
