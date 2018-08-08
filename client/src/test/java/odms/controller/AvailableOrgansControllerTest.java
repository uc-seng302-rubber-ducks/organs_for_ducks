package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class AvailableOrgansControllerTest {

    @Before
    public void setUp() {
        ObservableList<AvailableOrganDetail> availableOrganDetails = FXCollections.observableList(new ArrayList<>());
        AvailableOrgansLogicController controller = new AvailableOrgansLogicController(availableOrganDetails);
    }

    @Test
    public void testNextPageNoPages() {

    }

    @Test
    public void testPrevPageNoPages() {

    }

    @Test
    public void testSearchCallsCorrectLink() {

    }


}
