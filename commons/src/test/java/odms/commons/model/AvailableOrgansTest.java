package odms.commons.model;

import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class AvailableOrgansTest {

    AvailableOrganDetail organDetail;
    LocalDateTime time;

    @Before
    public void beforeTest(){
        time = LocalDateTime.of(2018, 9, 8, 14, 43);
        organDetail = new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "", "" );
    }

    @Test
    public void testVaildOrgan(){
        Assert.assertTrue(organDetail.isOrganStillValid(time.plusHours(1)));
    }

    @Test
    public void testInvaildOrganJustInvalid(){
        Assert.assertFalse(organDetail.isOrganStillValid(time.plusHours(24)));
    }

    @Test
    public void testVaildOrganJustStillValid(){
        Assert.assertTrue(organDetail.isOrganStillValid(time.plusHours(23).plusMinutes(59)));
    }
}
