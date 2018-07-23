package odms.GUITest2;

import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.commons.exception.ApiException;
import odms.commons.model.Administrator;
import odms.controller.AppController;
import odms.controller.gui.window.AdministratorViewController;
import odms.utils.AdministratorBridge;
import odms.utils.LoginBridge;
import odms.utils.TransplantBridge;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;


public class UpdateAdminControllerGUITest extends ApplicationTest {

    private Administrator testAdmin;
    private TransplantBridge transplantBridge = mock(TransplantBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, ApiException {

        testAdmin = new Administrator("admin1", "Anna", "Kate", "Robertson", "face");
        AdministratorBridge administratorBridge = mock(AdministratorBridge.class);
        LoginBridge loginBridge = mock(LoginBridge.class);
        AppController application = mock(AppController.class);

        AppController.setInstance(application);

        when(application.getAdministratorBridge()).thenReturn(administratorBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(administratorBridge.getAdmin(anyString(), anyString())).thenReturn(testAdmin);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(transplantBridge.getWaitingList(anyInt(), anyInt(), anyString(), anyString(), anyCollection())).thenReturn(new ArrayList<>());

        doCallRealMethod().when(application).setAdministratorViewController(any(AdministratorViewController.class));
        doCallRealMethod().when(application).getAdministratorViewController();

        AppController.getInstance().getAdmins().remove(testAdmin);
        AppController.getInstance().getAdmins().add(testAdmin);
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        clickOn("#administratorTab");
        setTextField(this,"#adminUsernameTextField","admin1");
        setTextField(this,"#adminPasswordField","face");
        clickOnButton(this,"#loginAButton");
        clickOnButton(this,"#updateButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getAdmins().remove(testAdmin);
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void preloadDetails() {
        verifyThat("#firstNameTextField", TextInputControlMatchers.hasText("Anna"));
        verifyThat("#middleNameTextField", TextInputControlMatchers.hasText("Kate"));
        verifyThat("#lastNameTextField", TextInputControlMatchers.hasText("Robertson"));
    }


    @Test
    public void updateFirstName() {
        setTextField(this,"#firstNameTextField","Annah");
        clickOnButton(this,"#confirmButton");
        verifyThat("#adminFirstnameLabel", LabeledMatchers.hasText("Annah"));
    }


    @Test
    public void updateMiddleName() {
        setTextField(this,"#middleNameTextField","Grace");
        clickOnButton(this,"#confirmButton");
        verifyThat("#adminMiddleNameLabel", LabeledMatchers.hasText("Grace"));
    }

    @Test
    public void updateLastName() {
        setTextField(this,"#lastNameTextField","Anderson");
        clickOnButton(this,"#confirmButton");
        verifyThat("#adminLastNameLabel", LabeledMatchers.hasText("Anderson"));
    }

    @Test
    public void updatePassword() {
        setTextField(this,"#passwordTextField","hey");
        setTextField(this,"#cPasswordTextField","hey");
        clickOnButton(this,"#confirmButton");
        clickOnButton(this,"#adminLogoutButton");
        clickOn("#administratorTab");
        setTextField(this,"#adminUsernameTextField","admin1");
        setTextField(this,"#adminPasswordField","hey");
        clickOnButton(this,"#loginAButton");
        verifyThat("#adminFirstnameLabel", LabeledMatchers.hasText("Anna"));
    }

    @Test
    public void passwordError() {
        setTextField(this,"#passwordTextField","hey");
        setTextField(this,"#cPasswordTextField","heyy");
        clickOnButton(this,"#confirmButton");
        verifyThat("#errorLabel", LabeledMatchers.hasText("Your passwords don't match"));
    }

    @Test
    public void cancel() {
        setTextField(this,"#firstNameTextField","Annah");
        clickOn("#cancelButton");
        clickOn("#yesButton");
        verifyThat("#adminFirstnameLabel", LabeledMatchers.hasText("Anna"));
    }

}
