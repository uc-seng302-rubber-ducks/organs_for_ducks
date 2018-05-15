package seng302.GUITests;

import javafx.scene.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.Controller.AppController;
import seng302.Model.Administrator;
import java.util.concurrent.TimeoutException;
import static org.testfx.api.FxAssert.verifyThat;

public class DeleteAdminGUITest extends ApplicationTest {

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getAdmins().clear();
        AppController.getInstance().getAdmins().add(new Administrator("default", null, null, null, "admin"));
        System.out.println(AppController.getInstance().getAdmins());

        clickOn("#administratorTab");
        clickOn("#adminUsernameTextField");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void testCannotDeleteDefaultAdmin(){
        write("default", 0);
        clickOn("#adminPasswordField");
        write("admin", 0);
        clickOn("#loginAButton");
        verifyThat("#deleteAdminButton", Node::isDisabled);
    }

}
