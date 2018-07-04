package seng302.Utils;

import odms.utils.PasswordManager;
import org.junit.Test;

public class PasswordManagerTest {

    @Test
    public void testIsExpectedPasswordTrue() {
        byte[] salt = PasswordManager.getNextSalt();
        byte[] hash = PasswordManager.hash("password", salt);

        assert (PasswordManager.isExpectedPassword("password", salt, hash));
    }

    @Test
    public void testIsExpectedPasswordFalse() {
        byte[] salt = PasswordManager.getNextSalt();
        byte[] hash = PasswordManager.hash("password", salt);

        assert (!PasswordManager.isExpectedPassword("Password", salt, hash));
    }

    @Test
    public void testIsExpectedPasswordWrongSalt() {
        byte[] salt = PasswordManager.getNextSalt();
        byte[] hash = PasswordManager.hash("password", salt);

        assert (!PasswordManager.isExpectedPassword("Password", PasswordManager.getNextSalt(), hash));
    }


}
