package odms.controller.gui.panel.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.OrgansWithExpiry;
import odms.controller.AppController;
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
    public void init(AppController appController, OrgansWithExpiry detail, User user, Stage stage) {
        logicController = new OrganExpiryLogicController(appController, detail);
        expirationOrgan.setText(detail.getOrganType().toString());
        expirationNhi.setText(user.getNhi());
        this.stage = stage;
    }

    public void confirmExpiration() {
        logicController.setExpiryReason(expirationReasonTextArea.getText());
        stage.close();
    }

    public void cancelExpiration() {
        stage.close();
    }
}
