package odms.controller.gui.panel.logic;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.controller.AppController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AvailableOrgansLogicController implements PropertyChangeListener {

    private int startingIndex = 0;
    private int startingIndexMatches = 0;
    private static final int ROWS_PER_PAGE = 30;
    private String organ = "";
    private String region = "";
    private ObservableList<AvailableOrganDetail> availableOrganDetails;
    private ObservableList<TransplantDetails> transplantDetails;
    private AvailableOrganDetail availableOrgan;

    public AvailableOrgansLogicController(ObservableList<AvailableOrganDetail> availableOrganDetails) {
        this.availableOrganDetails = availableOrganDetails;
    }

    public void search(int startIndex, String organ, String region) {
        shutdownThreads();
        availableOrganDetails.clear();
        this.organ = organ;
        this.region = region;
        this.startingIndex = startIndex;

        // Make the call to the bridge here, and hand the arraylist to the bridge function
        AppController.getInstance().getOrgansBridge().getAvailableOrgansList(startIndex, ROWS_PER_PAGE, organ, region, "", "", "", availableOrganDetails);
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

    public synchronized void shutdownThreads() {
        if (!Platform.isFxApplicationThread()) {
            return;
        }
        for (AvailableOrganDetail detail : availableOrganDetails) {
            detail.getProgressTask().cancel(true);
        }
    }

    public void goNextPageMatches() {
        if(transplantDetails.size() < ROWS_PER_PAGE){
            return;
        }
        startingIndexMatches += ROWS_PER_PAGE;
        searchMatches(startingIndexMatches);

    }

    private void searchMatches(int startingIndexMatches) {
        transplantDetails.clear();
        this.startingIndexMatches = startingIndexMatches;
        AppController.getInstance().getOrgansBridge().getMatchingOrgansList(startingIndexMatches,
                ROWS_PER_PAGE, availableOrgan.getDonorNhi(), availableOrgan.getOrgan().toString(), transplantDetails);
    }

    public void goPrevPageMatches() {
        if (startingIndexMatches - ROWS_PER_PAGE < 0) {
            return;
        }

        startingIndexMatches -= ROWS_PER_PAGE;
        searchMatches(startingIndexMatches);
    }

    public void expireOrgans() {


    }

    public void showMatches(AvailableOrganDetail selectedItem) {
        this.availableOrgan = selectedItem;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        availableOrganDetails.removeIf(p -> !p.isOrganStillValid());
    }
}
