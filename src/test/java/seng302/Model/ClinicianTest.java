package seng302.Model;

import javafx.scene.Parent;
import org.junit.Before;
import org.junit.Test;
import seng302.Service.PasswordManager;

import java.util.Arrays;

public class ClinicianTest {

    private Clinician testClinician;

    @Before
    public void setup(){
        testClinician  = new Clinician("","0","","","password");
    }

    @Test
    public void passwordIsCorrectlyHashed(){
        byte[] hash = PasswordManager.hash("password", testClinician.getSalt());
        assert (Arrays.equals(hash, testClinician.getPassword()));
    }

    @Test
    public void passwordIsCorrectlyUpdated(){
        testClinician.setPassword("Password");
        assert (PasswordManager.isExpectedPassword("Password", testClinician.getSalt(), testClinician.getPassword()));
    }
}
