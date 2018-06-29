package seng302.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.model._enum.Organs;

import java.time.LocalDate;

public class undoReceiverOrgansTest {

    private User testUser;

    @Before
    public void setUp() {
        testUser = new User("Frank", LocalDate.of(1980, 3, 5), "ABC1234");
    }

    @Test
    public void shouldEmptyReceiverOrgans() {
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.LUNG);
        testUser.undo();
        Assert.assertFalse(testUser.getReceiverDetails().isCurrentlyWaitingFor(Organs.LUNG));
        Assert.assertFalse(testUser.getReceiverDetails().getOrgans().containsKey(Organs.LUNG));
    }

    @Test
    public void shouldNoLongerBeReceivingHeart() {
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.LUNG);
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.HEART);
        testUser.undo();
        Assert.assertFalse(testUser.getReceiverDetails().getOrgans().containsKey(Organs.HEART));
    }
}
