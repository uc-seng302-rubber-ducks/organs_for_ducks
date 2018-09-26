package odms.commons.model;

import odms.commons.model.datamodel.Address;
import odms.commons.model.datamodel.ContactDetails;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class UndoClinicianTest {

    private Clinician testUser;

    @Before
    public void setUp() {
        testUser = new Clinician("Staff1", "password", "John", "Angus", "McGurkinshaw");
        Address workAddress =  new Address("20", "Kirkwood Ave", "", "", "Canterbury", "", "");
        testUser.setWorkContactDetails(new ContactDetails("", "", workAddress, ""));
    }

    @Test
    public void testSingleChangeSingleUndo() {
        testUser.setFirstName("Jonathan");
        testUser.undo();
        assertEquals(testUser.getFirstName(), "John");
    }

    @Test
    public void testSingleChangeMultipleUndo() {
        testUser.setStreetName("Example St");
        testUser.undo();
        testUser.undo();

        assertEquals(testUser.getStreetName(), "Kirkwood Ave");
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
