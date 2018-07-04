package seng302.GUITest1;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import odms.App;
import odms.controller.AppController;
import odms.commons.model.Administrator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.Utils.CommonTestMethods;

import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

public class DeleteAdminGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getAdmins().clear();
        AppController.getInstance().getAdmins().add(new Administrator("default", null, null, null, "admin"));

        clickOn("#administratorTab");
        clickOn("#adminUsernameTextField");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void testCannotDeleteDefaultAdmin() {
        write("default", 0);
        lookup("#adminPasswordField").queryAs(TextField.class).setText("admin");
        clickOn("#loginAButton");
        verifyThat("#deleteAdminButton", Node::isDisabled);
    }

}
