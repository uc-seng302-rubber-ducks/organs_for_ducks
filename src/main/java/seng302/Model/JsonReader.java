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
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

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
        File inFile = new File(Directory.JSON.directory() + "/donors.json");
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        JSONParser parser = new JSONParser();
        int imported = 0;
        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader(inFile));
            imported = 0;
            for (Object o : a) {
                JSONObject donor = (JSONObject) o;
                Date dod;
                String name = (String) donor.get("Name");

                String gender = (String) donor.get("Gender");

                String bloodType = (String) donor.get("Blood Type");
                String currentAddress = (String) donor.get("Current Address");
                String region = (String) donor.get("Region");

                try {
                    Double height = (Double) donor.get("Height");
                    Double weight = (Double) donor.get("Weight");
                    Date dob = dateFormat.parse(donor.get("DOB").toString());
                    if (donor.get("DOD").toString().equals("null")) {
                        dod = null;
                    } else {
                        dod = dateFormat.parse(donor.get("DOD").toString());
                    }
                    DateTime timeCreated = new DateTime(donor.get("Time Created"));
                    DateTime lastModified = new DateTime(donor.get("Last Modified"));
                    Boolean isDeceased = (Boolean) donor.get("is Deceased");
                    Donor d = new Donor(dob, dod, gender, height, weight, bloodType, currentAddress, region, timeCreated, name, lastModified, isDeceased);
                    JSONArray organs = (JSONArray) donor.get("Organs");
                    if (organs != null) {
                        for (Object org : organs) {
                            d.addOrgan(Organs.valueOf(org.toString()));
                        }
                    }
                    donorsIn.add(d);
                    JSONArray miscAttributes = (JSONArray) donor.get("Misc");
                    if (miscAttributes != null){
                        for (Object attribute : miscAttributes)
                            d.addAttribute(attribute.toString());
                    }
                    JSONArray previousMedication = (JSONArray) donor.get("Previous Medication");
                        if(previousMedication != null){
                            for(Object medication : previousMedication){
                                d.addPreviousMedicationSetUp((String) medication);
                            }
                        }

                    JSONArray currentMedication = (JSONArray) donor.get("Current Medication");
                        if(currentMedication != null){
                            for(Object medication : currentMedication) {
                                d.addCurrentMedicationSetup((String) medication);
                            }
                        }
                    JSONArray previousMedicationTimeStamps = (JSONArray) donor.get("Previous Medication TimeStamps");
                        if(previousMedicationTimeStamps != null) {
                            Iterator<Object> iterator = previousMedicationTimeStamps.iterator();
                            while (iterator.hasNext()){
                                JSONObject medication = (JSONObject) iterator.next();
                                Set<String> keyset = medication.keySet();
                                for(String key : keyset){
                                    ArrayList<DateTime> timestamps = new ArrayList<>(); // Sorry about the cone of doom
                                    JSONArray stamps = (JSONArray) medication.get(key);// unfortunalty it is needed
                                    for(Object stamp : stamps){
                                        DateTime dateTime = new DateTime((String) stamp);
                                        timestamps.add(dateTime);
                                    }
                                    d.addPreviousMedicationTimes(key,timestamps);
                                }
                            }
                        }

                    JSONArray currentMedicationTimeStamps = (JSONArray) donor.get("Current Medication TimeStamps");
                    if(previousMedicationTimeStamps != null) {
                        Iterator<Object> iterator = currentMedicationTimeStamps.iterator();
                        while (iterator.hasNext()){
                            JSONObject medication = (JSONObject) iterator.next();
                            Set<String> keyset = medication.keySet();
                            for(String key : keyset){
                                ArrayList<DateTime> timestamps = new ArrayList<>();
                                JSONArray stamps = (JSONArray) medication.get(key);
                                for(Object stamp : stamps){
                                    DateTime dateTime = new DateTime((String) stamp);
                                    timestamps.add(dateTime);
                                }
                                d.addCurrentMedicationTimes(key,timestamps);
                            }
                        }
                    }

                    imported++;
                } catch (IllegalArgumentException e) {
                    System.out.println("malformed DateTime has been detected for Donor: " + name + " has not been imported. This record will be purged if changes are made in this session.");
                } catch (java.text.ParseException e) {
                    System.out.println("Malformed DOB has been detected for Donor: " + name + " has not been imported. This record will be purged if changes are made in this session.");
                }


            }
        } catch (ParseException e) {
            System.out.println("Parsing error please ensure that file is a correctly formatted JSON file. Nothing has been imported" +
                    "\nAny data saved may overwrite previously saved data");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("No previous user data has been found. Initiating blank session");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(imported + " Donors Successfully imported");
        return donorsIn;
    }

    public static ArrayList<Clinician> importClinicians(){

        ArrayList<Clinician> clinicians = new ArrayList<>();


        File infile =  new File(Directory.JSON.directory()+"/clinicians.json");
        if(!infile.exists()){
            System.out.println("File did not exist creating new clinication list");
            return clinicians;
        }
        JSONParser jsonParser = new JSONParser();

        int imported = 0;
        try {
            JSONArray a = (JSONArray) jsonParser.parse(new FileReader(infile));

            for(Object o : a) {
                JSONObject clinician = (JSONObject) o;
                String name = (String) clinician.get("Name");
                Long staffIdl =(Long) clinician.get("Staff Id");
                int staffId = staffIdl.intValue();
                String workAddress =(String) clinician.get("Work Address");
                String region = (String) clinician.get("Region");
                String password = (String) clinician.get("Password");
                DateTime dateCreated = new DateTime(clinician.get("Date Created"));
                DateTime dateLastModified = new DateTime(clinician.get("Last Modified"));

                Clinician c = new Clinician(name,staffId,workAddress,region,password, dateCreated,dateLastModified);
                clinicians.add(c);
                imported += 1;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        System.out.println(imported + " Clinicians Successfully added");
        return clinicians;
    }
}
