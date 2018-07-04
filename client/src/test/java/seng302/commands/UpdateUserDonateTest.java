package seng302.commands;


import odms.commands.UpdateUserDonate;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import odms.controller.AppController;
import odms.model.DonorDetails;
import odms.model.User;
import odms.model._enum.Organs;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UpdateUserDonateTest {

    private UpdateUserDonate command;
    private AppController controller;
    private User user;

    @Before
    public void setUp() {

        command = new UpdateUserDonate();
        controller = mock(AppController.class);
        user = mock(User.class);
    }

    @Test
    public void testNoParams() {
        String[] args = {};
        when(controller.findUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        //intelliJ shows error as getDonorDetails is returning an object, despite being in a verify stmt
        verify(user, times(0)).getDonorDetails();
    }

    @Test
    public void testNoOrgans() {
        String[] args = {"ABC1234"};
        when(controller.findUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(user, times(0)).getDonorDetails();
    }

    @Test
    public void testInvalidOrganName() {
        String[] args = {"ABC1234", "+squid"};
        when(controller.findUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(user, times(0)).getDonorDetails();
    }

    @Test
    public void testInvalidSymbol() {
        String[] args = {"ABC1234", "~liver"};
        when(controller.findUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(user, times(0)).getDonorDetails();
    }

    @Test
    public void testAddAndRemoveOrgans() {
        String[] args = {"ABC1234", "+liver", "+kidney", "/lung"};
        DonorDetails details = mock(DonorDetails.class);
        when(user.getDonorDetails()).thenReturn(details);
        when(controller.findUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(details, times(1)).addOrgan(Organs.LIVER);
        verify(details, times(1)).addOrgan(Organs.KIDNEY);
        verify(details, times(1)).removeOrgan(Organs.LUNG);
    }

    @Test
    public void testValidEntryWithInvalidEntry() {
        //+liver -spleen
        //should still add a liver
        String[] args = {"ABC1234", "+lung", "+squid", "+liver"};
        DonorDetails details = mock(DonorDetails.class);
        when(user.getDonorDetails()).thenReturn(details);
        when(controller.findUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(details, times(1)).addOrgan(Organs.LUNG);
        verify(details, times(1)).addOrgan(Organs.LIVER);
    }


}
