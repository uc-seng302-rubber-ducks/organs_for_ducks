package odms.utils;

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

    @Test
    public void testHashStaySame(){
        String hash = "qEm6pUtZyUc70w_goHbGUoHSSA4IDdM7vgtbYdzigLo";
        String salt = "54, -71, 20,-65, -104, 56, -85, 73, -74, 123, -87, -13, 40, 9, 3, 44, -64, -105, 120, " +
               "47, -23, -63, 31, 51, 49, 23, -77,-10, 60, -29, -120, 117";

        Assert.assertTrue(PasswordManager.isExpectedPassword("pass4", salt,hash));
    }

}
