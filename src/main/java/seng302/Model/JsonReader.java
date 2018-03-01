package seng302.Model;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng302.Directory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class for reading from JSON file containing application data.
 *
 * @author Josh Burt
 */
public final class JsonReader {


    /**
     * Reads from the specified file and reads in a the JSON file containing the files. Should be called on startup
     *
     * @return List of donors present in the application during the last session
     */
    public static ArrayList<Donor> importJsonDonors() {
        ArrayList<Donor> donorsIn = new ArrayList<>();
        File inFile = new File(Directory.JSON.directory()+"/donors.json");
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
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
        } catch (FileNotFoundException e) {
            System.out.println("No previous user data has been found. Initiating blank session");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return donorsIn;
    }
}
