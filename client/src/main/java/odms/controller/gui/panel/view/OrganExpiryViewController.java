package odms.controller.gui.panel.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ExpiryReason;
import odms.controller.AppController;
import odms.controller.gui.panel.DonationTabPageController;
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
    private DonationTabPageController donationTabPageController;

    @FXML
    public void init(AppController appController, Organs organs, ExpiryReason expiryReason, User user, Stage stage, DonationTabPageController donationTabPageController) {
        logicController = new OrganExpiryLogicController(appController, expiryReason);
        expirationOrgan.setText(organs.toString());
        expirationReasonTextArea.setText(expiryReason
                .getReason());
        expirationNhi.setText(user.getNhi());
        this.stage = stage;
        this.donationTabPageController = donationTabPageController;
    }

    public void confirmExpiration() {
        logicController.setExpiryReason(expirationReasonTextArea.getText());
        donationTabPageController.refreshCurrentlyDonating();
        stage.close();
    }

    public void cancelExpiration() {
        donationTabPageController.refreshCurrentlyDonating();
        stage.close();
    }
}
