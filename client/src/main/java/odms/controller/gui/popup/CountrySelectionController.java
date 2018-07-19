package odms.controller.gui.popup;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import odms.commons.model.Administrator;
import odms.controller.AppController;
;import java.util.ArrayList;
import java.util.List;

public class CountrySelectionController {

    @FXML
    private ListView<String> countrySelection;

    @FXML
    private TextField searchCountry;

    private Administrator admin;
    private Stage stage;
    private AppController appController;
    private List<String> allowedCountries;

    /**
     * initialise country selection pop up window.
     * @param admin current admin
     * @param stage gui stage
     */
    public void init(Administrator admin, Stage stage, AppController appController) {
        this.admin = admin;
        this.stage = stage;
        this.appController = appController;
        initCountrySelectionList();
        allowedCountries = new ArrayList<>();
    }

    /**
     * initialise  Country Selection List. includes rendering checkboxes for each row.
     */
    private void initCountrySelectionList(){
        countrySelection.setItems(FXCollections.observableList(appController.getAllCountries()));
        countrySelection.setCellFactory(CheckBoxListCell.forListView(country -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener((obs, wasSelected, isNowSelected) ->
                    {
                        if (isNowSelected) {
                            allowedCountries.add(country);
                        } else if (wasSelected) {
                            allowedCountries.remove(country);
                        }
                    }
            );
            return observable ;
        }));
    }

    @FXML
    void selectDeselectAll(){
        //countrySelection.setCellFactory(CheckBoxListCell.forListView(i->i.setS));
    }

    /**
     * cancel the Country Selection and close the window.
     */
    @FXML
    void cancelCountrySelection(){
        stage.close();
    }

    /**
     * save the Country Selection and close the window.
     */
    @FXML
    void saveCountriesSelection(){
        allowedCountries.sort(String.CASE_INSENSITIVE_ORDER);
        appController.setAllowedCountries(allowedCountries);
        stage.close();
    }
}
