package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.controller.gui.popup.logic.DisqualifyOrganReasonLogicController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DisqualifyOrganReasonLogicControllerTest {
    private DisqualifyOrganReasonLogicController controller;
    private ObservableList<OrgansWithDisqualification> disqualifiedOrgans = FXCollections.observableArrayList();
    private OrgansWithDisqualification testOrgan = new OrgansWithDisqualification(Organs.HEART, "Heart is broken", LocalDate.now(), "0");
    private User testUser = new User("Frank", LocalDate.parse("1 2 3", (DateTimeFormatter.ofPattern("y M d"))),
            "ABC1234");
    @Before
    public void setUp(){
        disqualifiedOrgans.add(testOrgan);
        controller = new DisqualifyOrganReasonLogicController(testUser, disqualifiedOrgans);
        testUser.getDonorDetails().addOrgan(Organs.LUNG, null);
    }


    @After
    public void tearDown() {
        AppController.setInstance(null);
    }


    @Test
    public void confirmCreateDisqualifiedOrganTest(){
        controller.confirm(Organs.LUNG, LocalDate.now().plusDays(1), "Lung not fresh", "0");
        OrgansWithDisqualification organ = disqualifiedOrgans.get(1);
        assertEquals(LocalDate.now().plusDays(1),organ.getEligibleDate());
        assertEquals("Lung not fresh",organ.getReason());
        assertFalse(testUser.getDonorDetails().getOrgans().contains(Organs.LUNG));
    }

    @Test
    public void confirmUpdateTest(){
        controller.confirm(Organs.HEART, LocalDate.now().plusDays(1), "Heart cancer", "0");
        OrgansWithDisqualification organ = disqualifiedOrgans.get(0);
        assertEquals(LocalDate.now().plusDays(1),organ.getEligibleDate());
        assertEquals("Heart cancer",organ.getReason());
    }
}
