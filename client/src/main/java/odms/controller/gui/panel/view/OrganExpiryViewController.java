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

import java.util.regex.Pattern;

public class OrganExpiryViewController {
    @FXML
    private Label expirationOrgan;
    @FXML
    private Label expirationNhi;

    @FXML
    private TextArea expirationReasonTextArea;

    @FXML
    private Label warningLabelOE;

    private OrganExpiryLogicController logicController;
    private Stage stage;
    private DonationTabPageController donationTabPageController;

    /**
     * Initializes the window with the option to manually expire the selected organ
     *
     * @param appController             main controller that holds the name and id of the account expiring the organ
     * @param organs                    the organ to be manually expired
     * @param expiryReason              the expiry detail object to be modified
     * @param user                      the current user
     * @param stage                     a new stage
     * @param donationTabPageController the donation tab which holds tables that need to be refreshed
     */
    @FXML
    public void init(AppController appController, Organs organs, ExpiryReason expiryReason, User user, Stage stage, DonationTabPageController donationTabPageController) {
        logicController = new OrganExpiryLogicController(appController, expiryReason);
        expirationOrgan.setText(organs.toString());
        expirationReasonTextArea.setText(expiryReason
                .getReason());
        expirationNhi.setText(user.getNhi());
        this.stage = stage;
        this.donationTabPageController = donationTabPageController;
        warningLabelOE.setText("");
    }

    /**
     * Confirms and updates the manual expiry of the selected organ
     */
    public void confirmExpiration() {
        if (Pattern.compile(" *").matcher(expirationReasonTextArea.getText()).matches()) {
            warningLabelOE.setText("A reason for expiry must be given");
            return;
        }
        logicController.setExpiryReason(expirationReasonTextArea.getText());
        donationTabPageController.refreshCurrentlyDonating();
        stage.close();
    }

    /**
     * Cancels the manual expiry
     */
    public void cancelExpiration() {
        donationTabPageController.refreshCurrentlyDonating();
        stage.close();
    }
}
