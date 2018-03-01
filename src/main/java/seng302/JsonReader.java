package seng302;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class JsonReader {

    public static ArrayList<Donor> importJsonDonors() throws IOException{
        ArrayList<Donor> donorsIn = new ArrayList<>();
        File inFile = new File(Directory.JSON.directory()+"/donors.json");
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        if(!inFile.exists()) {
            throw new IOException("File does not exist");
        }
        JSONParser parser = new JSONParser();
        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader(inFile));

            for(Object o : a ){
                JSONObject donor = (JSONObject) o;
                Date dod;
                String name = (String) donor.get("Name");
                Date dob = dateFormat.parse(donor.get("DOB").toString());
                if(donor.get("DOD").toString().equals("null")){
                    dod = null;
                } else {
                    dod = dateFormat.parse(donor.get("DOD").toString());
                }
                String gender = (String) donor.get("Gender");
                Double height = (Double) donor.get("Height");
                Double weight = (Double) donor.get("Weight");
                String bloodType = (String) donor.get("Blood Type");
                String currentAddress = (String) donor.get("Current Address");
                String region = (String) donor.get("Region");
                DateTime timeCreated = (DateTime) donor.get("Date Created");
                donorsIn.add(new Donor(dob,dod,gender,height,weight,bloodType,currentAddress,region,timeCreated,name));

            }
        } catch( ParseException e){
            System.out.println("Parsing error please ensure that file is a correctly formatted JSON file. Nothing has been imported");
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return donorsIn;
    }
}
