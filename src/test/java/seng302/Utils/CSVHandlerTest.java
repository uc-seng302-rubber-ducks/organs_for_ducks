package seng302.Utils;

import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;
import seng302.model.User;
import seng302.utils.CSVHandler;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;


public class CSVHandlerTest {
    CSVHandler csvHandler;
    User shouldEqual;


    @Before
    public void beforeTests() {
        shouldEqual = new User();
        shouldEqual.setNhi("ADB8724");
        shouldEqual.setFirstName("Marga");
        shouldEqual.setLastName("Gindghill");
        shouldEqual.setDateOfBirth(LocalDate.parse("7/07/1943", DateTimeFormatter.ofPattern("MM/dd/YYYY")));
        shouldEqual.setDateOfDeath(LocalDate.parse("7/07/1949", DateTimeFormatter.ofPattern("MM/dd/YYYY")));
        shouldEqual.setBirthGender("Female");
        shouldEqual.setGenderIdentity("Male");
        shouldEqual.setBloodType("Male");
        shouldEqual.setHeight(1.94);
        shouldEqual.setWeight(105);
        /* todo: change these to work when user is re-engineered
        shouldEqual.setStreetNumber(2158);
        shouldEqual.setStreetName("Melody");
        shouldEqual.setSetNeighbourhood("Bình Thủy");
        shouldEqual.setCity("Hamilton")

        shouldEqual.setZipCode(5813);
        */
        shouldEqual.setRegion("Waikato");
        shouldEqual.setHomePhone("07 743 4657");
        shouldEqual.setCellPhone("020 483 1284");
        shouldEqual.setEmail("mgindghill2@furl.net");

        csvHandler = new CSVHandler();

    }

    @Test

    public void testParseValidCsv() throws IOException {

        File inFile = new File("src/test/resources/csvData/csvTestData.csv");
        CSVHandler csvHandler = new CSVHandler();
        List<CSVRecord> records = csvHandler.parseCSV(inFile);

        assert (records.size() == 4);
    }

    @Test
    public void testDecodeValidCsv() throws IOException {
        File inFile = new File("src/test/resources/csvData/csvTestData.csv");
        List<CSVRecord> records = csvHandler.parseCSV(inFile);
        Collection<User> users = csvHandler.decodeUsersFromCSV(records);
        List<User> usersList = new ArrayList<>(users);
        User toTest = usersList.get(4);
        assert (toTest.equals(shouldEqual));
        assert (toTest.getFirstName().equals(shouldEqual.getFirstName()));
        assert (toTest.getLastName().equals(shouldEqual.getLastName()));
        assert (toTest.getDateOfBirth().equals(shouldEqual.getDateOfBirth()));
        assert (toTest.getDateOfDeath().equals(shouldEqual.getDateOfDeath()));
        assert (toTest.getBirthGender().equals(shouldEqual.getBirthGender()));
        assert (toTest.getGenderIdentity().equals(shouldEqual.getGenderIdentity()));
        assert (toTest.getHeight() == shouldEqual.getHeight());
        assert (toTest.getWeight() == shouldEqual.getWeight());
        assert (toTest.getRegion().equals(shouldEqual.getRegion()));
        assert (toTest.getHomePhone().equals(shouldEqual.getHomePhone()));

    }

}

