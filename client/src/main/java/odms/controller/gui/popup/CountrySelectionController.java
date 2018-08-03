package odms.controller.gui.popup;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.commons.model.Administrator;
import odms.controller.AppController;
import odms.controller.gui.widget.TextStringCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountrySelectionController {

    @FXML
    private ListView<TextStringCheckBox> countrySelection;

    @FXML
    private TextField searchCountry;
    @FXML
    private CheckBox selectDeselectCountries;

    private Administrator admin;
    private Stage stage;
    private AppController appController;
    private List<String> allowedCountries;
    private List<String> initialCountries;
    private boolean selectAll = false;

    /**
     * initialise country selection pop up window.
     * @param admin current admin
     * @param stage gui stage
     */
    public void init(Administrator admin, Stage stage, AppController appController) {
        this.admin = admin;
        this.stage = stage;
        this.appController = appController;
        allowedCountries = appController.getAllowedCountries();
        initialCountries = allowedCountries.subList(0, allowedCountries.size());
        initCountrySelectionList();
    }

    /**
     * initialise  Country Selection List. includes rendering checkboxes for each row.
     */
    private void initCountrySelectionList(){
        List<TextStringCheckBox> checkBoxes = new ArrayList<>();
        for (String country : appController.getAllCountries()) {
            TextStringCheckBox newCountry = new TextStringCheckBox(country);
            newCountry.setSelected(appController.getAllowedCountries().contains(country));
            newCountry.selectedProperty().addListener((observable, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    allowedCountries.add(country);
                } else if (wasSelected) {
                    allowedCountries.remove(country);
                }
            });
            checkBoxes.add(newCountry);
        }
        countrySelection.setItems(FXCollections.observableList(checkBoxes));
    }

    @FXML
    void selectDeselectAll(){
        selectAll = !selectAll;
        for (TextStringCheckBox checkBox : countrySelection.getItems()) {
            checkBox.setSelected(selectAll);
        }
    }

    /**
     * cancel the Country Selection and close the window.
     */
    @FXML
    void cancelCountrySelection() {
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

    @FXML
    private void resetInitialCountries() {
        allowedCountries = appController.getAllowedCountries();
        for (TextStringCheckBox checkBox : countrySelection.getItems()) {
            checkBox.setSelected(allowedCountries.contains(checkBox.toString()));
        }
        selectDeselectCountries.setSelected(false);
        selectAll = false;
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
    private List<TextStringCheckBox> countriesQuery(String queryStr) {
        List<TextStringCheckBox> desiredCountries = new ArrayList<>();
        Pattern pattern = Pattern.compile(queryStr+".*", Pattern.CASE_INSENSITIVE);

        for (String country : appController.getAllCountries()) {
            Matcher matcher = pattern.matcher(country);
            if (matcher.lookingAt()) {
                TextStringCheckBox newCountry = new TextStringCheckBox(country);
                newCountry.setSelected(appController.getAllowedCountries().contains(country));
                newCountry.selectedProperty().addListener((observable, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        allowedCountries.add(country);
                    } else if (wasSelected) {
                        allowedCountries.remove(country);
                    }
                });
                desiredCountries.add(newCountry);
            }
        }
        return desiredCountries;
    }
}
