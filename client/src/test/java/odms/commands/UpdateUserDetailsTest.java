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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UpdateUserDetailsTest {

    AppController controller;
    UserBridge bridge;
    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String NHI = "";

    @Before
    public void resetDonor() throws IOException {
        bridge = mock(UserBridge.class);
        controller = mock(AppController.class);
        doCallRealMethod().when(controller).setUsers(any(ArrayList.class));
        AppController.setInstance(controller);
        when(controller.getUserBridge()).thenReturn(bridge);
        when(controller.findUser(anyString())).thenReturn(new User("test dummy", LocalDate.parse("1111-11-11", sdf), "ABC1234"));
        controller.setUsers(new ArrayList<>());

        try {
            controller.addUser(new User("test dummy", LocalDate.parse("1111-11-11", sdf), "ABC1234"));
            NHI = "ABC1234";
            User user = controller.findUser(NHI);
            user.setWeight(65.3);
            System.out.println("Users size: " + controller.getUsers().size());
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
    public void ShouldUpdateFirstName() {
        String[] args = {"-NHI=" + NHI, "-f=Mal"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User mal = controller.findUser(NHI);
        Assert.assertNotNull(mal);

        assertEquals("Mal", mal.getFirstName());
    }

    @Test
    public void ShouldUpdateLastName() {
        String[] args = {"-NHI=" + NHI, "-l=muppet"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User muppet = controller.findUser(NHI);
        Assert.assertNotNull(muppet);

        List<User> test = controller.findUsers("test dummy");
        assert (test.size() == 0);
    }

    @Test
    public void ShouldUpdateFullName() {
        String[] args = {"-NHI=" + NHI, "-f=stephen", "-l=hawking"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User alan = controller.findUser(NHI);
        Assert.assertNotNull(alan);

        Assert.assertEquals("stephen", alan.getFirstName());
        Assert.assertEquals("hawking", alan.getLastName());
    }

    @Test
    public void ShouldUpdateNumberField() {
        //height and weight are identical, no use testing both
        //just checking it can parse numbers
        String[] args = {"-NHI=" + NHI, "-w=100"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.findUser(NHI);
        assert (test.getWeight() == 100);
    }

    @Test
    public void ShouldNotUpdateBadNumberField() {
        //height and weight are identical, no use testing both
        String[] args = {"-NHI=" + NHI, "-w=fat"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.findUser(NHI);
        assert (test.getWeight() == 65.3);
    }

    @Test
    public void ShouldUpdateDateField() {
        //dob and dod are identical, no use testing both
        //just checking it can parse dates
        String[] args = {"-NHI=" + NHI, "-dob=2020-03-04"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.findUser(NHI);
        try {
            assert (test.getDateOfBirth().equals(LocalDate.parse("2020-03-04", sdf)));
        } catch (DateTimeParseException ex) {
            fail("Could not parse date (error in tester)");
        }
    }

    @Test
    public void ShouldNotUpdateBadDate() {
        //dob and dod are identical, no use testing both
        String[] args = {"-NHI=" + NHI, "-dob=1963"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.findUser(NHI);
        try {
            assert (test.getDateOfBirth().equals(LocalDate.parse("1111-11-11", sdf)));
        } catch (DateTimeParseException ex) {
            fail("Could not parse date (error in tester)");
        }

    }

    @Test
    public void ShouldUpdateLastModifiedTimestamp() throws InterruptedException {
        User user = controller.findUser(NHI);
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

    @Test
    public void ShouldNotUpdateNHItoDuplicateOfExistingUser() {
        //one user cannot have the NHI changed to that of another user
        User user = controller.findUser(NHI);
        controller.addUser(new User("Frank", LocalDate.of(1990, 3, 3), "CDE1234"));
        User other = controller.findUser("CDE1234");

        String[] args = {"-NHI=ABC1234", "-newNHI=CDE1234"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        Assert.assertEquals(controller.findUser("CDE1234"), other);
        Assert.assertEquals(controller.findUser("ABC1234"), user);
    }
}
