package odms.controller.gui.panel.logic;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.controller.AppController;
import odms.controller.gui.widget.CountableLoadingWidget;

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

    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param availableOrganDetails observable list of AvailableOrganDetail to use to populate the available organs table
     * @param transplantDetails     Observable list of TransplantDetails to use for populating the potential matches
     */
    public AvailableOrgansLogicController(ObservableList<AvailableOrganDetail> availableOrganDetails, ObservableList<TransplantDetails> transplantDetails) {
        this.availableOrganDetails = availableOrganDetails;
        this.transplantDetails = transplantDetails;
    }

    /**
     * Sends a search request to the server to populate the observable list
     * @param startIndex the number of entries to skip
     * @param organ the organ to filter by
     * @param region the region to filter by
     * @param widget widget to stop loading when the call has finished
     */
    public void search(int startIndex, String organ, String region, CountableLoadingWidget widget) {
        shutdownThreads();
        availableOrganDetails.clear();
        this.organ = organ;
        this.region = region;
        this.startingIndex = startIndex;

        // Make the call to the bridge here, and hand the arraylist to the bridge function
        AppController.getInstance().getOrgansBridge().getAvailableOrgansList(startIndex, ROWS_PER_PAGE, organ, region, "", "", "", availableOrganDetails, widget);
    }

    /**
     * Goes to the previous page in the available organs table.
     * @param widget widget to stop loading when the call has finished
     */
    public void goPrevPage(CountableLoadingWidget widget) {
        if (startingIndex - ROWS_PER_PAGE < 0) {
            return;
        }

        startingIndex = startingIndex - ROWS_PER_PAGE;
        search(startingIndex, organ, region, widget);
    }

    /**
     * Goes to the next page in the available organs table
     * @param widget widget to stop loading when the call has finished
     */
    public void goNextPage(CountableLoadingWidget widget) {
        if (availableOrganDetails.size() < ROWS_PER_PAGE) {
            return;
        }

        startingIndex = startingIndex + ROWS_PER_PAGE;
        search(startingIndex, organ, region, widget);
    }

    /**
     * Shuts down all the running background tasks for the progress bars.
     */
    public synchronized void shutdownThreads() {
        if (!Platform.isFxApplicationThread()) {
            return;
        }
        for (AvailableOrganDetail detail : availableOrganDetails) {
            detail.getProgressTask().cancel(true);
        }
    }

    /**
     * Goes to the next page of the potential matches table
     * @param widget widget to stop loading when the call has finished
     */
    public void goNextPageMatches(CountableLoadingWidget widget) {
        if(transplantDetails.size() < ROWS_PER_PAGE){
            return;
        }
        startingIndexMatches += ROWS_PER_PAGE;
        searchMatches(startingIndexMatches, widget);

    }

    /**
     * Provides pagination functionality for the matches table
     * @param startingIndexMatches how many entries to skip before returning
     * @param widget widget to stop loading when the call has finished
     */
    private void searchMatches(int startingIndexMatches, CountableLoadingWidget widget) {
        if(availableOrgan == null){
            return;
        }
        transplantDetails.clear();
        this.startingIndexMatches = startingIndexMatches;
        AppController.getInstance().getOrgansBridge().getMatchingOrgansList(startingIndexMatches,
                ROWS_PER_PAGE, availableOrgan.getDonorNhi(), availableOrgan, transplantDetails, widget);
    }

    /**
     * Goes to the previous page of the potential matches table
     * @param widget widget to stop loading when the call has finished
     */
    public void goPrevPageMatches(CountableLoadingWidget widget) {
        if (startingIndexMatches - ROWS_PER_PAGE < 0) {
            return;
        }

        startingIndexMatches -= ROWS_PER_PAGE;
        searchMatches(startingIndexMatches, widget);
    }

    /**
     * Populates the potential matches for a selected organ
     * @param selectedItem selected item in the available organs list
     * @param widget widget to stop loading when the call has finished
     */
    public void showMatches(AvailableOrganDetail selectedItem, CountableLoadingWidget widget) {
        this.availableOrgan = selectedItem;
        searchMatches(0, widget);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        availableOrganDetails.removeIf(p -> !p.isOrganStillValid());
    }
}
