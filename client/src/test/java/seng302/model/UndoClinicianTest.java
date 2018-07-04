package seng302.model;

import odms.commons.model.Clinician;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static junit.framework.TestCase.assertEquals;

public class UndoClinicianTest extends ApplicationTest {

    private Clinician testUser;

    @Before
    public void setUp() {
        testUser = new Clinician("Staff1", "password", "John", "Angus", "McGurkinshaw",
                "20 Kirkwood Ave", "Canterbury");
    }

    @Test
    public void testSingleChangeSingleUndo() {
        testUser.setFirstName("Jonathan");
        testUser.undo();
        assertEquals(testUser.getFirstName(), "John");
    }

    @Test
    public void testSingleChangeMultipleUndo() {
        testUser.setWorkAddress("112 Example St");
        testUser.undo();
        testUser.undo();

        assertEquals(testUser.getWorkAddress(), "20 Kirkwood Ave");
    }

    @Test
    public void testMultipleChangeMultipleUndo() {
        testUser.setFirstName("Thomas");
        testUser.setLastName("Tank Engine");
        testUser.setStaffId("BiggieSmalls");

        testUser.undo();
        testUser.undo();
        testUser.undo();
        assertEquals(testUser.getFirstName(), "John");
        assertEquals(testUser.getLastName(), "McGurkinshaw");
        assertEquals(testUser.getStaffId(), "Staff1");
    }

    @Test
    public void testMultipleChangesSingleUndo() {
        testUser.setFirstName("Alexander");
        testUser.setRegion("Hawkes Bay");

        testUser.undo();
        assertEquals(testUser.getRegion(), "Canterbury");
        assertEquals(testUser.getFirstName(), "Alexander");
    }
}
