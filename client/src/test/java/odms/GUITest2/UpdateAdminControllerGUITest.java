package odms.GUITest2;

import odms.App;
import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.commons.model.Administrator;
import odms.utils.AdministratorBridge;
import odms.utils.ClinicianBridge;
import odms.utils.LoginBridge;
import odms.utils.UserBridge;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import odms.TestUtils.CommonTestMethods;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.*;
import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;


public class UpdateAdminControllerGUITest extends ApplicationTest {

    private Administrator testAdmin;

    @BeforeClass
    public static void initialization() {
        //CommonTestMethods.runHeadless();
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
        //clickOn("#firstNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        setTextField(this,"#firstNameTextField","Annah");
        clickOnButton(this,"#confirmButton");
        verifyThat("#adminFirstnameLabel", LabeledMatchers.hasText("Annah"));
    }


    @Test
    public void updateMiddleName() {
        clickOn("#middleNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        setTextField(this,"#middleNameTextField","Grace");
        clickOnButton(this,"#confirmButton");
        verifyThat("#adminMiddleNameLabel", LabeledMatchers.hasText("Grace"));
    }

    @Test
    public void updateLastName() {
        clickOn("#lastNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
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
        clickOn("#firstNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        setTextField(this,"#firstNameTextField","Annah");
        clickOn("#cancelButton");
        clickOn("#yesButton");
        verifyThat("#adminFirstnameLabel", LabeledMatchers.hasText("Anna"));

    }

}
