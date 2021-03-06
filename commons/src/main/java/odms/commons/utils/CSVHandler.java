package odms.commons.utils;


import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model._enum.CountryCode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to handle the importing and redirection of the CSV data.
 */
public class CSVHandler extends DataHandler {

    private int malformed = 0;
    /**
     * Takes a CSV file, opened by the file handler
     * and reads the file into a list separated by record
     * <p>
     * Throws an IOException if an incorrect file is passed into it.
     *
     * @param csvFile CSV File to be parsed
     * @return A list of CSV records in the file
     * @throws IOException when an error occurs while parsing the .csv file
     */
    public List<CSVRecord> parseCSV(File csvFile) throws IOException {
        Reader reader = new FileReader(csvFile);
        CSVParser parser = CSVParser.parse(reader, CSVFormat.RFC4180);
        return parser.getRecords();
    }


    /**
     * Decodes the results of a parsed CSV file into a Collection of user classes
     *
     * @param records List of records to be decoded
     * @return Collection of Users from file
     */
    public Collection<User> decodeUsersFromCSV(List<CSVRecord> records) {
        Collection<User> users = new ArrayList<>();
        int count = 0;
        malformed = 0;
        int correct = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");

        for (CSVRecord record : records) {
            count++;
            // as column name maybe null this needs to be done by index
            User u = new User();
            u.setNhi(record.get(0).trim());
            u.setFirstName(record.get(1));
            u.setLastName(record.get(2));
            try {
                u.setDateOfBirth(LocalDate.parse(record.get(3), dtf));
                if (!record.get(4).equals("")) {
                    //TODO: Add a u.setTimeOfDeath and extract a LocalTime from record.get(4). (Needs deathDetails branch)
                    //here it is assumed that a non-empty DOD column implies a valid DOD should exist
                    u.setDateOfDeath(LocalDate.parse(record.get(4), dtf));
                }
                u.setHeight(Double.parseDouble(record.get(8)) / 100);
                u.setWeight(Double.parseDouble(record.get(9)));
            } catch (DateTimeParseException | NumberFormatException ex) {
                // catches a format error and then breaks that one malformed statement
                Log.warning("An invalid  value was detected in record: " + count, ex);
                malformed++;
                continue;
            }
            u.setBirthGender(record.get(5));
            u.setGenderIdentity(record.get(6));
            u.setBloodType(record.get(7));
            u.setStreetNumber(record.get(10));
            u.setStreetName(record.get(11));
            u.setNeighborhood(record.get(12));
            u.setCity(record.get(13));
            u.setZipCode(record.get(15));
            u.setRegion(record.get(14));
            if (CountryCode.getEnums().contains(record.get(16))) {
                u.setCountry(CountryCode.valueOf(record.get(16)).toString());
            } //else do not set country (it was invalid)

            if (CountryCode.getEnums().contains(record.get(17))) {
                u.setBirthCountry(CountryCode.valueOf(record.get(17)).toString());
            }
            u.setHomePhone(record.get(18));
            u.setCellPhone(record.get(19));
            u.setEmail(record.get(20));
            u.setContact(new EmergencyContact("", "", ""));
            ClassLoader classLoader = getClass().getClassLoader();
            File inFile = new File(classLoader.getResource("default-profile-picture.jpg").getFile());
            u.setProfilePhotoFilePath(inFile.toString());
            correct++;
            users.add(u);
        }
        Log.info("Finished Decoding File\n" +
                "Records in File: " + count +
                "\nRecords Successfully Read: " + correct +
                "\nNumber of Malformed Records " + malformed);
        return users;
    }

    /**
     * Abstract method that may or may not save the collection of users to the default location
     *
     * @param users Collection of users to attempt to save
     * @return true if the operation succeeded, false otherwise
     */
    @Override
    public boolean saveUsers(Collection<User> users) throws IOException {
        return false;
    }

    /**
     * Abstract method that loads the Users from a specified location into memory.
     *
     * @param location Location of where to load the data from
     */
    @Override
    public List<User> loadUsers(String location) throws FileNotFoundException {
        File userCSV = new File(location);
        try {
            return (List<User>) decodeUsersFromCSV(parseCSV(userCSV));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Abstract method that may or may not save the collection of users to the default location
     *
     * @param clinicians Collection of clinicians to attempt to save
     * @return true if the operation succeeded, false otherwise
     */
    @Override
    public boolean saveClinicians(Collection<Clinician> clinicians) throws IOException {
        return false;
    }

    /**
     * Abstract method that may or may not load the clinicians from a specified location in memory.
     *
     * @param location Location of where to load the clinicians
     * @return true if the operation succeeded, false otherwise
     */
    @Override
    public List<Clinician> loadClinicians(String location) throws FileNotFoundException {
        return null;
    }

    /**
     * Abstract method that may or may not save the collection of administrators to the default location
     *
     * @param administrators Collection of administrators to attempt to save
     * @return true if the operation succeeded, false otherwise
     */
    @Override
    public boolean saveAdmins(Collection<Administrator> administrators) throws IOException {
        return false;
    }

    /**
     * Abstract method that may or may not load the clinicians from a specified location in memory.
     *
     * @param location Location to load the administrators from
     * @return true if the operation succeeded, false otherwise
     */
    @Override
    public Collection<Administrator> loadAdmins(String location) throws FileNotFoundException {
        return null;
    }

    public int getMalformed() {
        return malformed;
    }

}
