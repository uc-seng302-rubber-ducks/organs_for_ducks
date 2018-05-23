package seng302.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UndoAdminTest {

    private Administrator testAdmin;

    @Before
    public void setUp() {
        testAdmin = new Administrator("username", "first", "middle", "last", "1234");
    }

    @Test
    public void testSingleUndo() {
        testAdmin.setFirstName("notFirst");
        testAdmin.undo();
        assertEquals("first", testAdmin.getFirstName());
    }

    @Test
    public void testSingleChangeMultipleUndo() {
        testAdmin.setMiddleName("Jeffery");
        testAdmin.undo();
        testAdmin.undo();
        testAdmin.undo();
        assertEquals("middle", testAdmin.getMiddleName());
    }

    @Test
    public void testMultipleChangeMultipleUndo() {
        testAdmin.setUserName("egg");
        testAdmin.setFirstName("John");
        testAdmin.setLastName("Johnson");

        testAdmin.undo();
        assertEquals("last", testAdmin.getLastName());

        testAdmin.undo();
        assertEquals("first", testAdmin.getFirstName());

        testAdmin.undo();
        assertEquals("username", testAdmin.getUserName());
    }

    @Test
    public void testMultipleChangesSingleUndo() {
        testAdmin.setFirstName("Michael");
        testAdmin.setMiddleName("Tyler");
        testAdmin.setLastName("Ling");

        testAdmin.undo();

        assertEquals("last", testAdmin.getLastName());
        assertEquals("Tyler", testAdmin.getMiddleName());
        assertEquals("Michael", testAdmin.getFirstName());
    }
}
