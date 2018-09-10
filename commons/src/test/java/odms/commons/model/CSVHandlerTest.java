package odms.commons.model;

import odms.commons.utils.CSVHandler;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CSVHandlerTest {
    private CSVHandler csvHandler;
    private User expected;


    @Before
    public void beforeTests() {
        expected = new User();
        expected.setNhi("ADB8724");
        expected.setFirstName("Marga");
        expected.setLastName("Gindghill");
        expected.setDateOfBirth(LocalDate.parse("7/07/1943", DateTimeFormatter.ofPattern("M/d/yyyy")));
        expected.setDateOfDeath(LocalDate.parse("7/07/1949", DateTimeFormatter.ofPattern("M/d/yyyy")));
        expected.setBirthGender("Female");
        expected.setGenderIdentity("Male");
        expected.setBloodType("Male");
        expected.setHeight(1.94);
        expected.setWeight(105.0);
        expected.setStreetNumber("2158");
        expected.setStreetName("Melody");
        expected.setNeighborhood("Bình Thủy");
        expected.setCity("Hamilton");
        expected.setZipCode("5813");
        expected.setRegion("Waikato");
        expected.setHomePhone("07 743 4657");
        expected.setCellPhone("020 483 1284");
        expected.setEmail("mgindghill2@furl.net");

        csvHandler = new CSVHandler();

    }

    @Test

    public void testParseValidCsv() throws IOException {

        File inFile = new File("src/test/resources/csvData/csvTestData.csv");
        CSVHandler csvHandler = new CSVHandler();
        List<CSVRecord> records = csvHandler.parseCSV(inFile);

        Assert.assertTrue (records.size() == 4);
    }

    @Test
    public void testDecodeValidCsvWithInvalidData() throws IOException {
        File inFile = new File("src/test/resources/csvData/csvTestData.csv");
        List<CSVRecord> records = csvHandler.parseCSV(inFile);
        Collection<User> users = csvHandler.decodeUsersFromCSV(records);
        List<User> usersList = new ArrayList<>(users);
        User actual = usersList.get(2);
        Assert.assertTrue (actual.getNhi().equals(expected.getNhi()));
        Assert.assertTrue (actual.getFirstName().equals(expected.getFirstName()));
        Assert.assertTrue (actual.getLastName().equals(expected.getLastName()));
        Assert.assertTrue (actual.getDateOfBirth().equals(expected.getDateOfBirth()));
        Assert.assertTrue (actual.getDateOfDeath().equals(expected.getDateOfDeath()));
        Assert.assertTrue (actual.getBirthGender().equals(expected.getBirthGender()));
        Assert.assertTrue (actual.getGenderIdentity().equals(expected.getGenderIdentity()));
        Assert.assertTrue (actual.getHeight() == expected.getHeight());
        Assert.assertTrue (actual.getWeight() == expected.getWeight());
        Assert.assertTrue (actual.getRegion().equals(expected.getRegion()));
        Assert.assertTrue (actual.getHomePhone().equals(expected.getHomePhone()));
        Assert.assertTrue (actual.getStreetNumber().equals(expected.getStreetNumber()));
        Assert.assertTrue (actual.getStreetName().equals(expected.getStreetName()));
        Assert.assertTrue (actual.getNeighborhood().equals(expected.getNeighborhood()));
        Assert.assertTrue (actual.getCity().equals(expected.getCity()));

    }

    @Test
    public void testInvalidCsvReturnsNoDataInLoadUsers() throws FileNotFoundException {
        CSVHandler csvHandler = new CSVHandler();
        ArrayList<User> users = (ArrayList<User>) csvHandler.loadUsers("src/test/resources/csvData/invalidCSV.csv");
        Assert.assertTrue (users.size() == 0);
    }

}

