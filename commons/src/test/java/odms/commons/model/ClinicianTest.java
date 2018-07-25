package odms.commons.model;

import odms.commons.utils.PasswordManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClinicianTest {

    private Clinician testClinician;

    @Before
    public void setup() {

        testClinician = new Clinician("", "0",  "password");
    }


    @Test
    public void passwordIsCorrectlyHashed() {
        String hash = PasswordManager.hash("password", testClinician.getSalt());
        Assert.assertTrue(hash.equals(testClinician.getPassword()));
    }

    @Test
    public void passwordIsCorrectlyAccepted() {
        Assert.assertTrue(testClinician.isPasswordCorrect("password"));
    }

    @Test
    public void passwordIsNotAccepted() {
        Assert.assertFalse(testClinician.isPasswordCorrect("Password"));
    }

    @Test
    public void passwordIsCorrectlyUpdated() {
        testClinician.setPassword("Password");
        Assert.assertTrue(testClinician.isPasswordCorrect("Password"));
    }
}
