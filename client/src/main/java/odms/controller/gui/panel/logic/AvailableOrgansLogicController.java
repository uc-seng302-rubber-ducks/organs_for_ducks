package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.controller.AppController;

public class AvailableOrgansLogicController {

    private int startingIndex = 0;
    private static final int ROWS_PER_PAGE = 30;
    private String organ = "";
    private String region = "";
    private ObservableList<AvailableOrganDetail> availableOrganDetails;

    public AvailableOrgansLogicController(ObservableList<AvailableOrganDetail> availableOrganDetails) {
        this.availableOrganDetails = availableOrganDetails;
    }

    public void search(int startIndex, String organ, String region) {
        availableOrganDetails.clear();
        this.organ = organ;
        this.region = region;
        this.startingIndex = startIndex;

        // Make the call to the bridge here, and hand the arraylist to the bridge function
        AppController.getInstance().getAvailableOrgansBridge().getAvailableOrgansList(startIndex, ROWS_PER_PAGE, organ, region, "", "", "", availableOrganDetails);
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

    public void shutdownThreads() {
        for (AvailableOrganDetail detail : availableOrganDetails) {
            detail.getProgressTask().cancel();
        }
    }
}
