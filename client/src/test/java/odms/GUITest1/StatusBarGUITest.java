package odms.GUITest1;

import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.User;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import odms.utils.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.SHORTCUT;
import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class StatusBarGUITest extends ApplicationTest {

    private AppController controller = mock(AppController.class);
    private UserBridge userBridge = mock(UserBridge.class);
    private ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
    private AdministratorBridge administratorBridge = mock(AdministratorBridge.class);
    private LoginBridge loginBridge = mock(LoginBridge.class);
    private TransplantBridge transplantBridge = mock(TransplantBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {
        AppController.setInstance(controller);

        when(controller.getClinicianBridge()).thenReturn(clinicianBridge);
        when(controller.getUserBridge()).thenReturn(userBridge);
        when(controller.getTransplantBridge()).thenReturn(transplantBridge);
        when(controller.getLoginBridge()).thenReturn(loginBridge);
        when(controller.getAdministratorBridge()).thenReturn(administratorBridge);

        doCallRealMethod().when(controller).setUserController(any(UserController.class));
        doCallRealMethod().when(controller).getUserController();

        when(userBridge.getUser(anyString())).thenReturn(new User("A", LocalDate.now().minusDays(1000), "ABC1234"));

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(new User("A", LocalDate.now().minusDays(1000), "ABC1234"));
        UserController userController = AppController.getInstance().getUserController();
        clickOn("#userIDTextField");
        write("ABC1234");
        clickOn("#loginUButton");
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        clickOn("#fNameInput").push(SHORTCUT, A).push(BACK_SPACE);
        clickOn("#fNameInput");
        write("Kate");
        clickOn("#confirmButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void checkStatusBarUpdates() {
        verifyThat("#statusBar", LabeledMatchers.hasText("ABC1234 Changed first name to Kate"));
    }

    @Test
    public void checkStatusBarClears() throws InterruptedException {
        Thread.sleep(7000);
        verifyThat("#statusBar", LabeledMatchers.hasText(""));
    }

}
