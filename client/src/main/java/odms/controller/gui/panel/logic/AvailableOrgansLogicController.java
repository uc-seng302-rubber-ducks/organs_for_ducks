package odms.controller.gui.panel.logic;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import odms.commons.model.User;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.controller.AppController;
import odms.controller.gui.panel.view.AvailableOrgansViewController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.io.IOException;

public class AvailableOrgansLogicController {

    private int startingIndex = 0;
    private final int ROWS_PER_PAGE = 30;
    private String organ = "";
    private String region = "";
    private ObservableList<AvailableOrganDetail> availableOrganDetails;
    private AvailableOrgansViewController availableOrgansViewController;

    public AvailableOrgansLogicController(ObservableList<AvailableOrganDetail> availableOrganDetails) {
        this.availableOrganDetails = availableOrganDetails;
        availableOrganDetails.addListener((ListChangeListener<? super AvailableOrganDetail>) observable -> availableOrgansViewController.populateTables());
    }

    public void search(int startIndex, String organ, String region) {
        this.organ = organ;
        this.region = region;
        this.startingIndex = startIndex;
        // Make the call to the bridge here, and hand the arraylist to the bridge function
    }

    public void goPrevPage() {
        if (startingIndex - ROWS_PER_PAGE < 0) {
            return;
        }

        startingIndex = startingIndex - ROWS_PER_PAGE;
        search(startingIndex, organ, region);
    }

    public void goNextPage() {
        if (availableOrganDetails.size() < ROWS_PER_PAGE) {
            return;
        }

        startingIndex = startingIndex + ROWS_PER_PAGE;
        search(startingIndex, organ, region);
    }

    public void launchUser(String donorNhi) {
        try {
            User user = AppController.getInstance().getUserBridge().getUser(donorNhi);
            // Make FXML calls to create the new window.
        } catch (IOException e) {
            AlertWindowFactory.generateError(e);
        }
    }
}
