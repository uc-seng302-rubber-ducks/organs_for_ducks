package odms.commands;

import odms.commons.model.User;
import odms.controller.AppController;
import odms.bridge.UserBridge;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateUserDetailsTest {

    AppController controller;
    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String NHI = "";
    User testUser;

    @Before
    public void resetDonor() throws IOException {
        UserBridge bridge = mock(UserBridge.class);
        controller = mock(AppController.class);
        AppController.setInstance(controller);
        when(controller.getUserBridge()).thenReturn(bridge);
        testUser = new User("test dummy", LocalDate.parse("1111-11-11", sdf), "ABC1234");
        when(bridge.getUser("ABC1234")).thenReturn(testUser);
        when(bridge.getExists("CDE1234")).thenReturn(true);
        controller.setUsers(new ArrayList<>());

        try {
            controller.addUser(new User("test dummy", LocalDate.parse("1111-11-11", sdf), "ABC1234"));
            NHI = "ABC1234";
            User user = bridge.getUser(NHI);
            user.setWeight(65.3);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("exception thrown setting up tests");
        }
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
    }

    @Test
    public void ShouldUpdateFirstName() throws IOException {
        String[] args = {NHI, "-f=Mal"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User mal = controller.getUserBridge().getUser(NHI);
        Assert.assertNotNull(mal);
        assertEquals("Mal", mal.getFirstName());
    }

    @Test
    public void ShouldUpdateLastName() throws IOException {
        String[] args = {NHI, "-l=muppet"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User muppet = controller.getUserBridge().getUser(NHI);
        Assert.assertNotNull(muppet);
    }

    @Test
    public void ShouldUpdateFullName() throws IOException {
        String[] args = {NHI, "-f=stephen", "-l=hawking"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User alan = controller.getUserBridge().getUser(NHI);
        Assert.assertNotNull(alan);

        Assert.assertEquals("stephen", alan.getFirstName());
        Assert.assertEquals("hawking", alan.getLastName());
    }

    @Test
    public void ShouldUpdateNumberField() throws IOException {
        //height and weight are identical, no use testing both
        //just checking it can parse numbers
        String[] args = {NHI, "-w=100"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);
        assert (test.getWeight() == 100);
    }

    @Test
    public void ShouldNotUpdateBadNumberField() throws IOException {
        //height and weight are identical, no use testing both
        String[] args = {NHI, "-w=fat"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);
        assert (test.getWeight() == 65.3);
    }

    @Test
    public void ShouldUpdateDateField() throws IOException, DateTimeParseException {
        //dob and dod are identical, no use testing both
        //just checking it can parse dates
        String[] args = {NHI, "-dob=2016-03-04"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);

        Assert.assertEquals(LocalDate.parse("2016-03-04", sdf), test.getDateOfBirth());
    }

    @Test
    public void ShouldNotUpdateBadDate() throws IOException, DateTimeParseException {
        //dob and dod are identical, no use testing both
        String[] args = {NHI, "-dob=1963"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);

        Assert.assertEquals(LocalDate.parse("1111-11-11", sdf), test.getDateOfBirth());
    }

    @Test
    public void ShouldUpdateLastModifiedTimestamp() throws InterruptedException, IOException {
        User user = controller.getUserBridge().getUser(NHI);
        LocalDateTime oldTime = user.getLastModified();
        Thread.sleep(100);
        System.out.println(oldTime);
        String[] args = {NHI, "-f=fred"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        LocalDateTime newTime = user.getLastModified();
        System.out.println(LocalDateTime.now());
        System.out.println(oldTime);
        System.out.println(newTime); // test needs delay removing these lines will cause the test to fail
        assert (newTime.isAfter(oldTime));
    }


    @Test
    public void ShouldNotUpdateNHItoDuplicateOfExistingUser() throws IOException {
        //one user cannot have the NHI changed to that of another user
        User user = controller.getUserBridge().getUser(NHI);
        controller.addUser(new User("Frank", LocalDate.of(1990, 3, 3), "CDE1234"));
        User other = controller.findUser("CDE1234");

        String[] args = {"ABC1234", "-newNHI=CDE1234"};
        UpdateUserDetails updateUserDetails = new UpdateUserDetails();
        updateUserDetails.setAppController(controller);
        new CommandLine(updateUserDetails)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        Assert.assertEquals(controller.getUserBridge().getUser("ABC1234"), user);
    }

    @Test
    public void ShouldSetDateOfDeathToNullWhenPassedNull() throws IOException, DateTimeParseException {
        String[] args = {NHI, "-dod=null"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);

        Assert.assertEquals (null, test.getDateOfDeath());

    }

    @Test
    public void ShouldCorrectlyUpdateTimeOfDeathWithNoPreviousDeath() throws IOException, DateTimeParseException {
        String[] args = {NHI, "-tod=02:45"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);

        Assert.assertEquals(LocalTime.parse("02:45"), test.getMomentDeath().toLocalTime());

    }

    @Test
    public void ShouldCorrectlyUpdateTimeOfDeathWithPreviousDeath() throws IOException, DateTimeParseException {
        String[] args = {NHI, "-tod=02:45"};

        User test = controller.getUserBridge().getUser(NHI);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        test.setDateOfDeath(LocalDate.parse("2010-01-01"));

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        Assert.assertEquals(LocalDateTime.parse("2010-01-01 02:45", formatter), test.getMomentDeath());

    }

    @Test
    public void ShouldUpdateCityOfDeath() throws IOException {
        String[] args = {NHI, "-dc=Christchurch"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);

        Assert.assertEquals("Christchurch", test.getDeathCity());
    }

    @Test
    public void ShouldUpdateRegionOfDeath() throws IOException {
        String[] args = {NHI, "-dr=Canterbury"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);

        Assert.assertEquals("Canterbury", test.getDeathRegion());
    }

    @Test
    public void ShouldUpdateCountryOfDeath() throws IOException {
        String[] args = {NHI, "-dco=New_Zealand"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);

        Assert.assertEquals("New Zealand", test.getDeathCountry());
    }

    @Test
    public void shouldNotUpdateInvalidCountry() throws IOException {
        when(controller.getAllowedCountries()).thenReturn(new ArrayList<>());

        String[] args = {NHI, "-co=Antarctica"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User testUser = controller.getUserBridge().getUser(NHI);
        assertEquals("", testUser.getCountry());
    }

    @Test
    public void shouldUpdateAvailableCountry() throws IOException {
        ArrayList<String> availableCountries = new ArrayList<>();
        availableCountries.add("Antarctica");
        when(controller.getAllowedCountries()).thenReturn(availableCountries);

        String[] args = {NHI, "-co=Antarctica"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User testUser = controller.getUserBridge().getUser(NHI);
        assertEquals("Antarctica", testUser.getCountry());
    }

    @Test
    public void shouldUpdateMultiWordCountry() throws IOException {
        ArrayList<String> availableCountries = new ArrayList<>();
        availableCountries.add("New Zealand");
        when(controller.getAllowedCountries()).thenReturn(availableCountries);

        String[] args = {NHI, "-co=New_Zealand"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User testUser = controller.getUserBridge().getUser(NHI);
        assertEquals("New Zealand", testUser.getCountry());
    }

    @Test
    public void shouldUpdateNonNZRegion() throws IOException {
        testUser.setCountry("Algeria");

        String[] args = {NHI, "-r=notInNZ"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User resultUser = controller.getUserBridge().getUser(NHI);
        assertEquals("notInNZ", resultUser.getRegion());
    }

    @Test
    public void shouldNotUpdateNonNZRegion() throws IOException {
        testUser.setCountry("New Zealand");

        String[] args = {NHI, "-r=notInNZ"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User resultUser = controller.getUserBridge().getUser(NHI);
        assertNotEquals("notInNZ", resultUser.getRegion());
    }
}
