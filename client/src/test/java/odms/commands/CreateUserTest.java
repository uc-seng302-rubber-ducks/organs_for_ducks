package odms.commands;

import odms.bridge.UserBridge;
import odms.commons.model.User;
import odms.commons.model.datamodel.Address;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreateUserTest {

    AppController controller;
    UserBridge bridge;
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private User minInfo;
    private User maxInfo;

    @Before
    public void setup() {
        controller = mock(AppController.class);
        doCallRealMethod().when(controller).setUsers(any(ArrayList.class));
        when(controller.addUser(any(User.class))).thenCallRealMethod();
        controller.setUsers(new ArrayList<>()); //reset users list between tests

        minInfo = new User("John Doe", LocalDate.parse("1961-02-12", format), "ABC1234");
        maxInfo = new User("Gus Johnson", LocalDate.parse("1990-04-03", format), "BCD2345");
        maxInfo.setDateOfDeath(LocalDate.parse("2010-05-16", format));
        maxInfo.setHeight(1.85);
        maxInfo.setWeight(86.3);
        maxInfo.setBirthGender("m");
        maxInfo.getContactDetails().setAddress(new Address("42", "wallaby-way", "", "", "Sydney", "", ""));

        bridge = mock(UserBridge.class);
        when(controller.getUserBridge()).thenReturn(bridge);
        when(controller.getUsers()).thenCallRealMethod();
        when(controller.findUser(anyString())).thenCallRealMethod();
        AppController.setInstance(controller);
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
    }

    //<editor-fold>
    @Test
    public void ShouldRegisterDonorWithMinimumInfo() throws IOException {
        when(bridge.getExists(anyString())).thenReturn(false);
        String[] args = {"ABC1234", "John", "1961-02-12", "-l=Doe"};
        when(bridge.getUser(anyString())).thenReturn(minInfo);
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().contains(minInfo));
    }

    @Test
    public void ShouldRegisterDonorWithMaximumInfo() throws IOException {
        String[] args = {"BCD2345", "Gus", "1990-04-03", "-l=Johnson", "-dod=2010-05-16", "-he=1.85",
                "-w=86.3",
                "-g=m", "-n=42", "-s=wallaby-way", "-r=Sydney"};
        when(bridge.getUser(anyString())).thenReturn(maxInfo);
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        User registered;
        registered = controller.findUser("BCD2345");
        Assert.assertEquals(maxInfo, registered); //checks name and dob
        Assert.assertEquals(maxInfo.getDateOfDeath(), registered.getDateOfDeath());
        assertTrue(maxInfo.getHeight() == registered.getHeight());
        assertTrue(maxInfo.getWeight() == registered.getWeight());
        Assert.assertEquals(maxInfo.getBirthGender(), registered.getBirthGender());
        Assert.assertEquals(maxInfo.getContactDetails().getAddress().toString(), registered.getContactDetails().getAddress().toString());
        Assert.assertEquals(maxInfo.getRegion(), registered.getRegion());
    }

    @Test
    public void ShouldNotRegisterWhenMissingParameters() {
        String[] args = {"Robert"};

        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().isEmpty());
    }

    @Test
    public void ShouldNotRegisterWhenMalformedParameters() {
        String[] args = {"ABC3333", "Frank", "abcd-as-sa", "-l=Sinatra"}; //invalid date
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().isEmpty());
    }

    @Test
    public void ShouldNotRegisterWhenMalformedOptions() {
        String[] args = {"ABC1111", "Ryan", "1967-21-03", "-l=Clark", "-he=myheight"};
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().size() == 0);
    }

    @Test
    public void ShouldCancelRegistrationWhenHelpFlagPresent() {
        String[] args = {"ABC2222", "Les", "1967-21-03", "-l=Claypool", "-h"};
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().isEmpty());
    }
}
