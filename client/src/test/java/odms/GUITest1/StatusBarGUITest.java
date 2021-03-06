package odms.GUITest1;

import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.model.User;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.*;
import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class StatusBarGUITest extends ApplicationTest {

    private AppController controller = AppControllerMocker.getFullMock();
    private UserBridge userBridge = mock(UserBridge.class);
    private ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
    private AdministratorBridge administratorBridge = mock(AdministratorBridge.class);
    private LoginBridge loginBridge = mock(LoginBridge.class);
    private TransplantBridge transplantBridge = mock(TransplantBridge.class);


    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
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
        FxToolkit.setupApplication(App.class, "--testConfig=true");
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
        clickOnButton(this,"#updateProfileButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    //TODO fix me pls 24/09/2018
    @Test @Ignore
    public void checkStatusBarUpdates() {
        //Told to ignore by Alanna, status bars have changed 20/9/18 - JB
        verifyThat("#statusBar", LabeledMatchers.hasText("ABC1234 Changed first name to Kate"));
    }

    @Test
    public void checkStatusBarClears() throws InterruptedException {
        Thread.sleep(7000);
        verifyThat("#statusBar", LabeledMatchers.hasText(""));
    }

}
