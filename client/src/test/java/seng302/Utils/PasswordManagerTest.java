package seng302.Utils;

import org.junit.Test;
import seng302.utils.PasswordManager;

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
