package odms.controller.gui.panel.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import odms.commons.model.Clinician;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.controller.gui.panel.logic.OrganExpiryLogicController;

public class OrganExpiryViewController {
    @FXML
    private Label expirationOrgan;
    @FXML
    private Label expirationNhi;

    @FXML
    private TextArea expirationReasonTextArea;

    private OrganExpiryLogicController logicController;
    private Stage stage;

    @FXML
    private void init(AvailableOrganDetail detail, Clinician clinician, Stage stage) {
        logicController = new OrganExpiryLogicController(detail, clinician);
        this.stage = stage;
        expirationOrgan.setText(detail.getOrgan().toString());
        expirationNhi.setText(detail.getDonorNhi());
    }

    public void confirmExpiration() {
        logicController.setExpiryReason(expirationReasonTextArea.getText());
    }

    public void cancelExpiration() {
        stage.close();
    }
}
