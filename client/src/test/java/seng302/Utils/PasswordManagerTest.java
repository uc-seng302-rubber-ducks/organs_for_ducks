package seng302.Utils;

import cucumber.api.java.cs.A;
import odms.commons.utils.PasswordManager;
import org.junit.Assert;
import org.junit.Test;

public class PasswordManagerTest {

    @Test
    public void testIsExpectedPasswordTrue() {
        String salt = PasswordManager.getNextSalt();
        String hash = PasswordManager.hash("password", salt);

        Assert.assertTrue(PasswordManager.isExpectedPassword("password", salt, hash));
    }

    @Test
    public void testIsExpectedPasswordFalse() {
        String salt = PasswordManager.getNextSalt();
        String hash = PasswordManager.hash("password", salt);

        Assert.assertTrue(!PasswordManager.isExpectedPassword("Password", salt, hash));
    }

    @Test
    public void testIsExpectedPasswordWrongSalt() {
        String salt = PasswordManager.getNextSalt();
        String hash = PasswordManager.hash("password", salt);

        Assert.assertTrue(!PasswordManager.isExpectedPassword("Password", PasswordManager.getNextSalt(), hash));
    }


}
