package seng302.Utils;

import odms.commons.model.User;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import org.junit.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Ignore
public class DBHandlerTest {
    private DBHandler dbHandler;
    private Connection connection;
    private User expected;

    /**
     * Helper function to convert date string
     * to LocalDateTime object.
     * @param date date string from database
     * @return LocalDateTime object
     */
    private LocalDateTime dateToLocalDateTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return LocalDateTime.parse(date, formatter);
    }

    @BeforeClass
    public static void beforeAllTests() {
        seng302.TestUtils.SQLScriptRunner.run();
    }

    @Before
    public void beforeTest() throws SQLException {
        expected = new User();
        expected.setNhi("ABC1234");
        expected.setFirstName("Allan");
        expected.setMiddleName("Danny Zurich");
        expected.setLastName("Levi");
        expected.setPreferredFirstName("Al");
        expected.setTimeCreated(dateToLocalDateTime("1997-01-01 00:01:01.0"));
        expected.setLastModified(dateToLocalDateTime("1997-05-01 13:01:01.0"));
        //TODO: set expected's profile picture here
        expected.setGenderIdentity("Male");
        expected.setBirthGender("Male");
        expected.setSmoker(true);
        expected.setAlcoholConsumption("High");
        expected.setHeight(163.7);
        expected.setWeight(65.8);
        expected.setHomePhone(null);
        expected.setCellPhone("0221453566");
        expected.setEmail("aaronB@gmail.com");

        dbHandler = new DBHandler();
        connection = new JDBCDriver().getTestConnection();
    }

    @After
    public void afterTest() throws SQLException {
        connection.close();
    }
/*
    @Test
    public void testUserInstanceCreatedValid() throws SQLException {
        Collection<User> users = dbHandler.getUsers(connection, 10, 0);
        System.out.println(users);
        Assert.assertEquals(3, users.size());
    }

    @Test
    public void testDecodeUserInstanceCreatedValid() throws SQLException {
        Collection<User> users = dbHandler.getUsers(connection, 10, 0);
        User actual = users.iterator().next();
        Assert.assertTrue(actual.getNhi().equals(expected.getNhi()));
        Assert.assertTrue(actual.getMiddleName().equals(expected.getMiddleName()));
        Assert.assertTrue(actual.getLastName().equals(expected.getLastName()));
        Assert.assertTrue(actual.getTimeCreated().equals(expected.getTimeCreated()));
        //Assert.assertTrue (actual.getLastModified().equals(expected.getLastModified()));
//        System.out.println(actual.getLastModified());
//        System.out.println(expected.getLastModified());

    }*/
}
