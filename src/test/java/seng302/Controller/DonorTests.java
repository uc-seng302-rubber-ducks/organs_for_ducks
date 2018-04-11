package seng302.Controller;

import org.junit.Assert;
import org.junit.Test;
import seng302.Model.Donor;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Deprecated
public class DonorTests {


    @Test
    public void testDonorEqualityEqual(){


        LocalDate dob = LocalDate.parse("01/01/1990",(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        Donor d1 = new Donor("John jacob", dob);
        Donor d2 = new Donor("John Jacob",dob);

        Assert.assertEquals(d1,d2);
    }

    @Test
    public void testDonorEqualityNotEqual(){

        LocalDate dob = LocalDate.parse("01/01/1990",(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        Donor d1 = new Donor("John jaxob", dob);
        Donor d2 = new Donor("John Jacob",dob);

        Assert.assertNotEquals(d1,d2);
    }
}

