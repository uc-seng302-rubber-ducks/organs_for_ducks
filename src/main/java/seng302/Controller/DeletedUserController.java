package seng302.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.Exception.UserAlreadyExistsException;
import seng302.Exception.UserNotFoundException;
import seng302.Model.Organs;
import seng302.Model.User;

public class DeletedUserController {

  @FXML
  private Button restoreButton;

  @FXML
  private TableView<User> deletedUserTableView;

  @FXML
  public void init() {
    initTableView();
  }

  public void initTableView() {
    ObservableList<User> oListUsers = FXCollections
        .observableList(AppController.getInstance().getDeletedUsers());

    TableColumn<User, String> fNameColumn = new TableColumn<>("First name");
    fNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

    TableColumn<User, String> lNameColumn = new TableColumn<>("Last name");
    lNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

    TableColumn<User, LocalDate> dobColumn = new TableColumn<>("Date of Birth");
    dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

    TableColumn<User, LocalDate> dodColumn = new TableColumn<>("Date of Death");
    dodColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeath"));

    TableColumn<User, Integer> ageColumn = new TableColumn<>("Age");
    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

    TableColumn<User, String> regionColumn = new TableColumn<>("Region");
    regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

    TableColumn<User, ArrayList<Organs>> organsColumn = new TableColumn<>("Organs");
    organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));

    deletedUserTableView.getColumns()
        .setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn,
            organsColumn);
    //searchTableView.setItems(FXCollections.observableList(sListDonors.subList(startIndex, endIndex)));
    deletedUserTableView.setItems(oListUsers);
    deletedUserTableView.setRowFactory((searchTableView) -> {
      return new TooltipTableRow<User>((User user) -> {
        return user.getTooltip();
      });
    });
  }

  @FXML
  public void undoDeletedUser() {
    try {
      AppController.getInstance()
          .undoDeletion(deletedUserTableView.selectionModelProperty().getValue().getSelectedItem());
    } catch (UserAlreadyExistsException e) {
      //TODO: Set error label text
    } catch (UserNotFoundException e) {
      //TODO: Set error label text
    }
  }

  public void setErrorLabelText(String text) {

  }
}
