package odms.GUITest2;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.exception.ApiException;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.controller.AppController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class LoginControllerGUITest extends ApplicationTest {

    private AppController controller;
    private UserBridge bridge;
    private ClinicianBridge clinicianBridge;
    private LoginBridge loginBridge;
    private AdministratorBridge administratorBridge;
    private TransplantBridge transplantBridge = mock(TransplantBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void startUp() throws TimeoutException, ApiException {
        controller = mock(AppController.class);
        bridge = mock(UserBridge.class);
        clinicianBridge = mock(ClinicianBridge.class);
        loginBridge = mock(LoginBridge.class);
        administratorBridge = mock(AdministratorBridge.class);

        AppController.setInstance(controller);
        when(controller.getUserBridge()).thenReturn(bridge);
        when(controller.getClinicianBridge()).thenReturn(clinicianBridge);
        when(controller.getAdministratorBridge()).thenReturn(administratorBridge);
        when(controller.getLoginBridge()).thenReturn(loginBridge);
        when(controller.getTransplantBridge()).thenReturn(transplantBridge);

        when(transplantBridge.getWaitingList(anyInt(), anyInt(), anyString(), anyString(), any(Collection.class))).thenReturn(new ArrayList());

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();

    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void invalidDonorLogin() throws IOException {
        when(bridge.getUser(anyString())).thenReturn(null);
        interact(() -> {
            lookup("#userIDTextField").queryAs(TextField.class).setText("AD");
            lookup("#loginUButton").queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
        verifyThat("#userWarningLabel", LabeledMatchers.hasText("User was not found. \nTo register a new user, please click sign up."));
    }

    @Test
    public void validDonorLogin() throws IOException {
        when(bridge.getUser(anyString())).thenReturn(new User("A", LocalDate.now(), "ABC1234"));
        setTextField(this, "#userIDTextField", "ABC1234");
        clickOnButton(this, "#loginUButton");
        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }

    @Test
    public void ValidClinicianLogin() throws IOException {
        when(loginBridge.loginToServer(anyString(), anyString(), anyString())).thenReturn("haHAA");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(new Clinician("default", "0", "admin"));
        clickOn("#clinicianTab");
        setTextField(this, "#staffIdTextField", "0");
        setTextField(this, "#staffPasswordField", "admin");
        clickOnButton(this, "#loginCButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
    }

    @Test
    public void clinicianInvalidClinician() throws IOException {
        when(loginBridge.loginToServer(anyString(), anyString(), anyString())).thenReturn("FeelsGoodMan");
        clickOn("#clinicianTab");
        setTextField(this,"#staffIdTextField", "-1000");
        setTextField(this, "#staffPasswordField", "admin");
        clickOnButton(this, "#loginCButton");
        verifyThat("#clinicianWarningLabel", LabeledMatchers.hasText("The Clinician does not exist"));
    }

    @Test
    public void clinicianWrongPassword() throws IOException {
        when(loginBridge.loginToServer(anyString(), anyString(), anyString())).thenThrow(new ApiException(401, ""));
        clickOn("#clinicianTab");
        setTextField(this,"#staffIdTextField", "0");
        setTextField(this, "#staffPasswordField", "garbledo");
        clickOnButton(this,"#loginCButton");
        verifyThat("#clinicianWarningLabel", LabeledMatchers.hasText("An error occurred. Please try again later."));
    }

    @Test
    public void validDonorLoginEnterPressed() throws IOException {
        when(bridge.getUser(anyString())).thenReturn(new User("A", LocalDate.now(), "ABC1234"));
        setTextField(this, "#userIDTextField", "ABC1234");
        press(KeyCode.ENTER);
        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }

    @Ignore
    @Test
    public void validAdminLogin() throws IOException {
        when(loginBridge.loginToServer(anyString(), anyString(), anyString())).thenReturn("FeelsAdminMan");
        when(administratorBridge.getAdmin(anyString(), anyString())).thenReturn(new Administrator("default", "", "", "", "admin"));
        //use default admin
        clickOn("#administratorTab");
        clickOn("#adminUsernameTextField");
        write("default");
        clickOn("#adminPasswordField");
        write("admin");
        clickOn("#loginAButton");
        //verifyThat();
    }

    @Test
    public void invalidAdminLogin() throws IOException{
        when(loginBridge.loginToServer(anyString(), anyString(), anyString())).thenThrow(new ApiException(404, "Not found"));
        clickOn("#administratorTab");
        setTextField(this,"#adminUsernameTextField", "therock");
        setTextField(this, "#adminPasswordField", "password");
        clickOnButton(this, "#loginAButton");
        verifyThat("#adminWarningLabel", LabeledMatchers.hasText("An unspecified error occurred. Please try again or contact your IT department."));
    }

    @Test
    public void wrongAdminPassword() throws IOException {
        when(loginBridge.loginToServer(anyString(), anyString(), anyString())).thenThrow(new ApiException(401, "Unauthorized"));
        clickOn("#administratorTab");
        setTextField(this, "#adminUsernameTextField", "default");
        setTextField(this, "#adminPasswordField","notpassword");
        clickOnButton(this, "#loginAButton");
        verifyThat("#adminWarningLabel", LabeledMatchers.hasText("An unspecified error occurred. Please try again or contact your IT department."));
    }

}
