package seng302.model;

import odms.model.Clinician;
import odms.utils.PasswordManager;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ClinicianTest {

    private Clinician testClinician;

    @Before
    public void setup() {

        testClinician = new Clinician("", "0", "", "", "password");
    }


    @Test
    public void passwordIsCorrectlyHashed() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        byte[] hash = PasswordManager.hash("password", testClinician.getSalt());
        Clinician instance = new Clinician();
        Class<?> secretClass = instance.getClass();

        Method method = secretClass.getDeclaredMethod("getPassword");
        method.setAccessible(true);
        assert (Arrays.equals(hash, (byte[]) method.invoke(testClinician)));
    }

    @Test
    public void passwordIsCorrectlyAccepted() {
        assert testClinician.isPasswordCorrect("password");
    }

    @Test
    public void passwordIsNotAccepted() {
        assert !testClinician.isPasswordCorrect("Password");
    }

    @Test
    public void passwordIsCorrectlyUpdated() {
        testClinician.setPassword("Password");
        assert testClinician.isPasswordCorrect("Password");
    }
}
