package odms.controller.gui.panel.view;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import odms.commons.model._abstract.UserLauncher;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AvailableOrgansViewController {

    @FXML
    private TableView<AvailableOrganDetail> availableOrgansTableView;

    @FXML
    private ComboBox<String> availableOrganFilterComboBox;

    @FXML
    private TextField regionFilterTextField;

    @FXML
    private TableColumn<AvailableOrganDetail, String> nhiColumn;

    @FXML
    private TableColumn<AvailableOrganDetail, String> regionColumn;

    @FXML
    private TableColumn<AvailableOrganDetail, String> organColumn;

    @FXML
    private TableColumn<AvailableOrganDetail, LocalDateTime> deathMomentColumn;

    @FXML
    private TableColumn<AvailableOrganDetail, Service> progressBarColumn;


    private ObservableList<AvailableOrganDetail> availableOrganDetails = FXCollections.observableList(new ArrayList<>());
    private AvailableOrgansLogicController logicController = new AvailableOrgansLogicController(availableOrganDetails);
    private PauseTransition pause = new PauseTransition(Duration.millis(300));
    private UserLauncher parent;

    @FXML
    public void init(UserLauncher parent) {
        ObservableList<String> organs = FXCollections.observableList(new ArrayList<>());
        for (Organs organ : Organs.values()) {
            organs.add(organ.toString());
        }
        this.parent = parent;
        availableOrganFilterComboBox.setItems(organs);
        availableOrganDetails.addListener((ListChangeListener<? super AvailableOrganDetail>) observable -> populateTables());
        regionFilterTextField.setOnKeyPressed(event -> {
            availableOrganDetails.add(new AvailableOrganDetail(Organs.LIVER, "", LocalDateTime.now(), "", ""));
//            pause.setOnFinished(e -> search());
//            pause.playFromStart();
        });
        initAvailableOrgansTableView();
    }

    private void initAvailableOrgansTableView() {
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("donorNhi"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        organColumn.setCellValueFactory(new PropertyValueFactory<>("organ"));
        deathMomentColumn.setCellValueFactory(new PropertyValueFactory<>("momentOfDeath"));
        progressBarColumn.setCellValueFactory(new PropertyValueFactory<>("progressTask"));
        progressBarColumn.setCellFactory(callback -> {
            ProgressBar progressBar = new ProgressBar(1.0F);
            TableCell<AvailableOrganDetail, Service> cell = new TableCell<AvailableOrganDetail, Service>() {
                @Override
                protected void updateItem(Service item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        progressBar.progressProperty().bind(item.progressProperty());
                        progressBar.minWidthProperty().bind(progressBarColumn.widthProperty().subtract(10));
                        System.out.println(item.getTotalWork());
                        progressBar.setStyle(getColorStyle(0.5));

                        if (!item.isRunning()) {
                            item.restart();
                        }
                    }
                }
            };
            cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(progressBar));
            return cell;
        });
        // figure out how to do progress bars
        search();
        populateTables();
    }


    private String getColorStyle(double progress) {
        // this doesn't work yet =/
        String green;
        String red;

        double percent = progress * 100;
        if (percent > 50.0) {
            // more red as it is closer to expiring
            green = Integer.toHexString((int) Math.round(percent * 255 * 2));
            if (green.length() == 1) {
                green = "0" + green;
            }
            red = "ff";
        } else {
            // more green as you there is more time
            red = Integer.toHexString((int) Math.round(percent * 255 * 2));
            if (red.length() == 1) {
                red = "0" + red;
            }
            green = "ff";
        }

        String colour = red + green + "00";

        return "-fx-background-color: -fx-box-border," + "linear-gradient(to left, green, red); -fx-accent: " + colour + ";";
    }


    @FXML
    private void search() {
        logicController.search(0, availableOrganFilterComboBox.getValue(), regionFilterTextField.getText());
    }

    @FXML
    private void goToPreviousPage() {
        logicController.goPrevPage();
    }

    @FXML
    private void goToNextPage() {
        logicController.goNextPage();
    }

    public void populateTables() {
        availableOrgansTableView.setItems(availableOrganDetails);

        setOnClickBehaviour();
    }

    private void setOnClickBehaviour() {
        availableOrgansTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && availableOrgansTableView.getSelectionModel().getSelectedItem() != null) {
                parent.launchUser(availableOrgansTableView.getSelectionModel().getSelectedItem().getDonorNhi());
            }
        });
    }
}
