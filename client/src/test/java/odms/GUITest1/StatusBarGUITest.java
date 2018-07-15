package odms.GUITest1;

import odms.App;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import odms.commons.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import odms.TestUtils.CommonTestMethods;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.*;
import static org.testfx.api.FxAssert.verifyThat;

public class StatusBarGUITest extends ApplicationTest {
    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(new User("A", LocalDate.now().minusDays(1000), "ABC1234"));
        UserController userController = AppController.getInstance().getUserController();
        clickOn("#userIDTextField");
        write("ABC1234");
        clickOn("#loginUButton");
        clickOn("#editDetailsButton");
        clickOn("#fNameInput").push(SHORTCUT, A).push(BACK_SPACE);
        clickOn("#fNameInput");
        write("Kate");
        clickOn("#confirmButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void checkStatusBarUpdates() {
        verifyThat("#statusBar", LabeledMatchers.hasText("ABC1234 Changed first name to Kate"));
    }

    @Test
    public void checkStatusBarClears() throws InterruptedException {
        Thread.sleep(6000);
        verifyThat("#statusBar", LabeledMatchers.hasText(""));
    }

}
