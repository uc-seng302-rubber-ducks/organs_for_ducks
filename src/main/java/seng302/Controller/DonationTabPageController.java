package seng302.Controller;

import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Service.OrganListCellFactory;

public class DonationTabPageController {

  @FXML
  private ListView<Organs> currentlyDonating;

  @FXML
  private ListView<Organs> canDonate;

  @FXML
  private Button donateButton;

  @FXML
  private Button undonateButton;

  @FXML
  private Label donorNameLabel;

  private User currentUser;
  private AppController application;
  private DonorController parent;

  /**
   * Gives the donor view the application controller and hides all label and buttons that are not
   * needed on opening
   *
   * @param controller the application controller
   * @param user the current user
   * @param parent the DonorController class this belongs to
   */
  public void init(AppController controller, User user, DonorController parent) {
    application = controller;
    currentUser = user;
    this.parent = parent;
    currentlyDonating.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    currentlyDonating.setCellFactory(column -> OrganListCellFactory.generateListCell(currentUser));

    populateOrganLists(user);
  }

  /**
   * Popoulates the organ lists of the user
   *
   * @param user user to use to populate
   */
  public void populateOrganLists(User user) {
    ArrayList<Organs> donating;
    try {
      donating = new ArrayList<>(user.getDonorDetails().getOrgans());
    } catch (NullPointerException ex) {
      donating = new ArrayList<>();
    }
    currentlyDonating.setItems(FXCollections.observableList(donating));
    ArrayList<Organs> leftOverOrgans = new ArrayList<Organs>();
    Collections.addAll(leftOverOrgans, Organs.values());
    for (Organs o : donating) {
      leftOverOrgans.remove(o);
    }
    canDonate.setItems(FXCollections.observableList(leftOverOrgans));
  }

  public void refreshCurrentlyDonating() {
    currentlyDonating.refresh();
  }

  /**
   * Moves selected organ from donatable to currently donating
   *
   * @param event passed in automatically by the gui
   */
  @FXML
  void donate(ActionEvent event) {

    if (!canDonate.getSelectionModel().isEmpty()) {
      Organs toDonate = canDonate.getSelectionModel().getSelectedItem();
      currentlyDonating.getItems().add(toDonate);
      currentUser.getDonorDetails().addOrgan(toDonate);
      if (parent.currentlyReceivingContains(toDonate)) {
        currentUser.getCommonOrgans().add(toDonate);
      }
      application.update(currentUser);
      canDonate.getItems().remove(toDonate);
      parent.updateUndoRedoButtons();
    }
    currentlyDonating.refresh();
    parent.refreshCurrentlyReceivingList();
  }

  /**
   * Moves selected organ from currently donating to donatable
   *
   * @param event passed in automatically by the gui
   */
  @FXML
  void undonate(ActionEvent event) {
    if (!currentlyDonating.getSelectionModel().isEmpty()) {
      Organs toUndonate = currentlyDonating.getSelectionModel().getSelectedItem();
      currentlyDonating.getItems().remove(toUndonate);
      canDonate.getItems().add(toUndonate);
      if (currentUser.getCommonOrgans().contains(toUndonate)) {
        currentUser.getCommonOrgans().remove(toUndonate);
        currentlyDonating.refresh();
      }

      currentUser.getDonorDetails().removeOrgan(toUndonate);
      currentlyDonating.refresh();
      application.update(currentUser);
      parent.updateUndoRedoButtons();
    }

    currentlyDonating.refresh();
    parent.refreshCurrentlyReceivingList();
  }

}
