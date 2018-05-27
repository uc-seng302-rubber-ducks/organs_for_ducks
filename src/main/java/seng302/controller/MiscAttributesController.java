package seng302.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.model.User;
import seng302.service.Log;

/**
 * Class for controlling the miscellaneous attributes view
 *
 * @author Josh Burt
 */
public class MiscAttributesController {

    @FXML
    private ListView<String> attributesList;

    @FXML
    private TextField attributeTextFeild;

    private User currentUser;
    private AppController appController;
    private Stage stage;

    /**
     * Initializes the Misc Attributes controller
     *
     * @param user          The current user.
     * @param appController An instance of AppController.
     * @param stage         The applications stage.
     */
    public void init(User user, AppController appController, Stage stage) {
        currentUser = user;
        this.appController = appController;
        this.stage = stage;
        attributesList.setItems(FXCollections.observableList(user.getMiscAttributes()));

    }

    /**
     * Adds miscellaneous attribute to the current user profile
     */
    @FXML
    void addAttribute() {
        String toAdd = attributeTextFeild.getText();
        attributeTextFeild.setText("");
        if (toAdd == null) {
            Log.warning("Unable to add miscellaneous attribute for User NHI: " + currentUser.getNhi() + "as attribute string is empty");
            return;
        }
        currentUser.addAttribute(toAdd);
        attributesList.setItems(FXCollections.observableList(currentUser.getMiscAttributes()));
        appController.update(currentUser);
        Log.info("Successfully to added miscellaneous attribute for User NHI: " + currentUser.getNhi());
    }

    /**
     * Removes selected item from the user profile
     */
    @FXML
    void removeAttribute() {
        String selected = attributesList.getSelectionModel().getSelectedItem();
        attributesList.getItems().remove(selected);
        currentUser.removeMiscAttribute(selected);
        appController.update(currentUser);
        Log.info("Successfully to removed miscellaneous attribute for User NHI: " + currentUser.getNhi());
    }

    /**
     * Closes the currentStage and shows the user on the previous controller.
     */
    @FXML
    void goBack() {
        AppController appController = AppController.getInstance();
        UserController userController = appController.getUserController();
        try {
            userController.showUser(currentUser);
            Log.info("Successfully to closed MiscAttributes window for User NHI: " + currentUser.getNhi());
        } catch (NullPointerException ex) {
            //TODO causes npe if donor is new in this session
            //the text fields etc. are all null
            Log.severe("unable to display user profile for User NHI: " + currentUser.getNhi() + " when closing MiscAttributes window", ex);
        }
        stage.close();
    }

}
