package seng302.commands;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.controller.AppController;
import seng302.model._enum.OrganDeregisterReason;
import seng302.model._enum.Organs;
import seng302.model.ReceiverDetails;
import seng302.model.User;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UpdateUserReceiveTest {

    private UpdateUserReceive command;
    private AppController controller;
    private User user;

    @Before
    public void setUp() {

        command = new UpdateUserReceive();
        controller = mock(AppController.class);
        user = mock(User.class);
    }

    @Test
    public void testNoParams() {
        String[] args = {};
        when(controller.getUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        //intelliJ shows error as getDonorDetails is returning an object, despite being in a verify stmt
        verify(user, times(0)).getReceiverDetails();
    }

    @Test
    public void testNoOrgans() {
        String[] args = {"ABC1234"};
        when(controller.getUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(user, times(0)).getReceiverDetails();
    }

    @Test
    public void testInvalidOrganName() {
        String[] args = {"ABC1234", "+squid"};
        when(controller.getUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(user, times(0)).getReceiverDetails();
    }

    @Test
    public void testInvalidSymbol() {
        String[] args = {"ABC1234", "~liver"};
        when(controller.getUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(user, times(0)).getReceiverDetails();
    }

    @Test
    public void testAddAndRemoveOrgans() {
        String[] args = {"ABC1234", "+liver", "+kidney", "/lung"};
        ReceiverDetails details = mock(ReceiverDetails.class);
        when(user.getReceiverDetails()).thenReturn(details);
        when(controller.getUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(details, times(1)).startWaitingForOrgan(Organs.LIVER);
        verify(details, times(1)).startWaitingForOrgan(Organs.KIDNEY);
        verify(details, times(1)).stopWaitingForOrgan(Organs.LUNG, OrganDeregisterReason.TRANSPLANT_RECEIVED);
    }

    @Test
    public void testValidEntryWithInvalidEntry() {
        //+liver -spleen
        //should still add a liver
        String[] args = {"ABC1234", "+lung", "+squid", "+liver"};
        ReceiverDetails details = mock(ReceiverDetails.class);
        when(user.getReceiverDetails()).thenReturn(details);
        when(controller.getUser(anyString())).thenReturn(user);
        command.setController(controller);
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(details, times(1)).startWaitingForOrgan(Organs.LUNG);
        verify(details, times(1)).startWaitingForOrgan(Organs.LIVER);
    }
}
