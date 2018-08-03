package odms.controller.gui.popup;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import odms.commons.model.Administrator;
import odms.controller.AppController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountrySelectionController {

    @FXML
    private ListView<String> countrySelection;

    @FXML
    private TextField searchCountry;
    @FXML
    private CheckBox selectDeselectCountries;

    private Administrator admin;
    private Stage stage;
    private AppController appController;
    private List<String> allowedCountries;
    private List<String> allCountries;

    /**
     * initialise country selection pop up window.
     * @param admin current admin
     * @param stage gui stage
     */
    public void init(Administrator admin, Stage stage, AppController appController) {
        this.admin = admin;
        this.stage = stage;
        this.appController = appController;
        allCountries = appController.getAllCountries();
        allowedCountries = new ArrayList<>();
        initCountrySelectionList();
    }

    /**
     * initialise  Country Selection List. includes rendering checkboxes for each row.
     */
    private void initCountrySelectionList(){
        countrySelection.setItems(FXCollections.observableList(allCountries));
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
        allowedCountries = appController.getAllowedCountries();
    }

    @FXML
    void selectDeselectAll(){
        //optional
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

    /**
     * gets the country names based on the search query and re-populates the table.
     * This method ges fired on key release from search text field.
     */
    @FXML
    void getDesiredCountries(){
        String query = searchCountry.getText();
        countrySelection.setItems(FXCollections.observableList(countriesQuery(query)));
    }

    /**
     * gets the country names based on the search query
     * @param queryStr query string
     * @return list of desired country names
     */
    private List<String> countriesQuery(String queryStr) {
        List<String> desiredCountries = new ArrayList<>();
        Pattern pattern = Pattern.compile(queryStr+".*", Pattern.CASE_INSENSITIVE);

        for (String country : allCountries) {
            Matcher matcher = pattern.matcher(country);
            if (matcher.lookingAt()) {
                desiredCountries.add(country);
            }
        }
        return desiredCountries;
    }
}
