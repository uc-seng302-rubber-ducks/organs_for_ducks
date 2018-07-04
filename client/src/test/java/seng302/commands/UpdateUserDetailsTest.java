package seng302.commands;

import odms.commands.UpdateUserDetails;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import odms.controller.AppController;
import odms.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static org.junit.Assert.fail;

public class UpdateUserDetailsTest {

    AppController controller;
    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String NHI = "";

    @Before
    public void resetDonor() {


        controller = AppController.getInstance();
        controller.setUsers(new ArrayList<>());

        try {
            controller.addUser(new User("test dummy", LocalDate.parse("1111-11-11", sdf), "ABC1234"));
            NHI = "ABC1234";
            User user = controller.findUsers("test dummy").get(0);
            user.setWeight(65.3);
            System.out.println("Users size: " + controller.getUsers().size());
        } catch (Exception ex) {
            fail("exception thrown setting up tests");
        }
    }

    @Test
    public void ShouldUpdateFirstName() {
        String[] args = {"-NHI=" + NHI, "-f=Mal"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User mal = controller.findUsers("Mal dummy").get(0);
        Assert.assertNotNull(mal);

        ArrayList<User> test = controller.findUsers("test dummy");
        assert (test.size() == 0);
    }

    @Test
    public void ShouldUpdateLastName() {
        String[] args = {"-NHI=" + NHI, "-l=muppet"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User muppet = controller.findUsers("test muppet").get(0);
        Assert.assertNotNull(muppet);

        ArrayList<User> test = controller.findUsers("test dummy");
        assert (test.size() == 0);
    }

    @Test
    public void ShouldUpdateFullName() {
        String[] args = {"-NHI=" + NHI, "-f=stephen", "-l=hawking"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User alan = controller.findUsers("stephen hawking").get(0);
        Assert.assertNotNull(alan);

        ArrayList<User> test = controller.findUsers("test dummy");
        assert (test.size() == 0);
    }

    @Test
    public void ShouldUpdateNumberField() {
        //height and weight are identical, no use testing both
        //just checking it can parse numbers
        String[] args = {"-NHI=" + NHI, "-w=100"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.findUsers("test dummy").get(0);
        assert (test.getWeight() == 100);
    }

    @Test
    public void ShouldNotUpdateBadNumberField() {
        //height and weight are identical, no use testing both
        String[] args = {"-NHI=" + NHI, "-w=fat"};
        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.findUsers("test dummy").get(0);
        assert (test.getWeight() == 65.3);
    }

    @Test
    public void ShouldUpdateDateField() {
        //dob and dod are identical, no use testing both
        //just checking it can parse dates
        String[] args = {"-NHI=" + NHI, "-dob=2020-03-04"};

        new CommandLine(new UpdateUserDetails())
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        User test = controller.findUsers("test dummy").get(0);
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

        User test = controller.findUsers("test dummy").get(0);
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
