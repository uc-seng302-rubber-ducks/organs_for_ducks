package odms.commands;

import odms.commons.model.User;
import odms.controller.AppController;
import odms.utils.UserBridge;
import org.junit.*;
import picocli.CommandLine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateUserDetailsTest {

    AppController controller;
    UserBridge bridge;
    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String NHI = "";

    @Before
    public void resetDonor() throws IOException {
        bridge = mock(UserBridge.class);
        controller = mock(AppController.class);
        AppController.setInstance(controller);
        when(controller.getUserBridge()).thenReturn(bridge);
        User u = new User("test dummy", LocalDate.parse("1111-11-11", sdf), "ABC1234");
        when(bridge.getUser("ABC1234")).thenReturn(u);
        when(bridge.getExists("CDE1234")).thenReturn(false);
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
        String[] args = {"-NHI=" + NHI, "-f=Mal"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User mal = controller.getUserBridge().getUser(NHI);
        Assert.assertNotNull(mal);

        assertEquals("Mal", mal.getFirstName());
    }

    @Test
    public void ShouldUpdateLastName() throws IOException {
        String[] args = {"-NHI=" + NHI, "-l=muppet"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User muppet = controller.getUserBridge().getUser(NHI);
        Assert.assertNotNull(muppet);

    }

    @Test
    public void ShouldUpdateFullName() throws IOException {
        String[] args = {"-NHI=" + NHI, "-f=stephen", "-l=hawking"};
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
        String[] args = {"-NHI=" + NHI, "-w=100"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);
        assert (test.getWeight() == 100);
    }

    @Test
    public void ShouldNotUpdateBadNumberField() throws IOException {
        //height and weight are identical, no use testing both
        String[] args = {"-NHI=" + NHI, "-w=fat"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);
        assert (test.getWeight() == 65.3);
    }

    @Test
    public void ShouldUpdateDateField() throws IOException {
        //dob and dod are identical, no use testing both
        //just checking it can parse dates
        String[] args = {"-NHI=" + NHI, "-dob=2016-03-04"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);
        try {
            assert (test.getDateOfBirth().equals(LocalDate.parse("2016-03-04", sdf)));
        } catch (DateTimeParseException ex) {
            fail("Could not parse date (error in tester)");
        }
    }

    @Test
    public void ShouldNotUpdateBadDate() throws IOException {
        //dob and dod are identical, no use testing both
        String[] args = {"-NHI=" + NHI, "-dob=1963"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.getUserBridge().getUser(NHI);
        try {
            assert (test.getDateOfBirth().equals(LocalDate.parse("1111-11-11", sdf)));
        } catch (DateTimeParseException ex) {
            fail("Could not parse date (error in tester)");
        }

    }

    @Test
    public void ShouldUpdateLastModifiedTimestamp() throws InterruptedException, IOException {
        User user = controller.getUserBridge().getUser(NHI);
        LocalDateTime oldTime = user.getLastModified();
        Thread.sleep(100);
        System.out.println(oldTime);
        String[] args = {"-NHI=" + NHI, "-f=fred"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        LocalDateTime newTime = user.getLastModified();
        System.out.println(LocalDateTime.now());
        System.out.println(oldTime);
        System.out.println(newTime); // test needs delay removing these lines will cause the test to fail
        assert (newTime.isAfter(oldTime));
    }

    @Ignore
    @Test
    public void ShouldNotUpdateNHItoDuplicateOfExistingUser() throws IOException {
        //one user cannot have the NHI changed to that of another user
        User user = controller.getUserBridge().getUser(NHI);
        controller.addUser(new User("Frank", LocalDate.of(1990, 3, 3), "CDE1234"));
        User other = controller.findUser("CDE1234");

        String[] args = {"-NHI=ABC1234", "-newNHI=CDE1234"};
        UpdateUserDetails updateUserDetails = new UpdateUserDetails();
        updateUserDetails.setAppController(controller);
        new CommandLine(updateUserDetails)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        Assert.assertEquals(controller.findUser("CDE1234"), other);
        Assert.assertEquals(controller.findUser("ABC1234"), user);
    }
}
