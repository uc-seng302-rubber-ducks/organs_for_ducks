package odms.commons.model;

import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class AvailableOrgansTest {

    private AvailableOrganDetail organDetail;
    private LocalDateTime time;

    @Before
    public void beforeTest(){
        time = LocalDateTime.of(2018, 9, 8, 14, 43);
        organDetail = new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "", "" );
    }

    @Test
    public void testValidOrgan(){
        Assert.assertTrue(organDetail.isOrganStillValid(time.plusSeconds(1)));
    }

    @Test
    public void testInvalidOrganJustInvalid(){
        Assert.assertFalse(organDetail.isOrganStillValid(time.plusSeconds(Organs.LIVER.getStorageSeconds())));
    }

    @Test
    public void testValidOrganJustStillValid() {
        Assert.assertTrue(organDetail.isOrganStillValid(time.plusSeconds(Organs.LIVER.getStorageSeconds() - 1)));
    }

    @Test
    public void testCalculateExpiryDate() {
        Assert.assertEquals(time.plusSeconds(Organs.LIVER.getStorageSeconds()), organDetail.getExpiryDate());
    }

    @Test
    public void testCalculateTimeRemaining() {
        Assert.assertEquals(Organs.LIVER.getStorageSeconds(), organDetail.calculateTimeLeft(time));
    }

    @Test
    public void testCalculateTimeRemainingShouldBeZero() {
        Assert.assertEquals(0, organDetail.calculateTimeLeft(time.plusSeconds(Organs.LIVER.getStorageSeconds() + 1)));
    }
}
