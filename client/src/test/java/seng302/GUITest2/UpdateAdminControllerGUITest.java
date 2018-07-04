package seng302.GUITest2;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import odms.App;
import seng302.Utils.CommonTestMethods;
import odms.controller.AppController;
import odms.model.Administrator;

import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.*;
import static org.testfx.api.FxAssert.verifyThat;


public class UpdateAdminControllerGUITest extends ApplicationTest {

    private Administrator testAdmin;

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        testAdmin = new Administrator("admin1", "Anna", "Kate", "Robertson", "face");
        AppController.getInstance().getAdmins().remove(testAdmin);
        AppController.getInstance().getAdmins().add(testAdmin);
        clickOn("#administratorTab");
        clickOn("#adminUsernameTextField");
        write("admin1");
        clickOn("#adminPasswordField");
        write("face");
        clickOn("#loginAButton");
        clickOn("#updateButton");
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
        clickOn("#firstNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        clickOn("#firstNameTextField");
        write("Annah");
        clickOn("#confirmButton");
        verifyThat("#adminFirstnameLabel", LabeledMatchers.hasText("Annah"));
    }


    @Test
    public void updateMiddleName() {
        clickOn("#middleNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        clickOn("#middleNameTextField");
        write("Grace");
        clickOn("#confirmButton");
        verifyThat("#adminMiddleNameLabel", LabeledMatchers.hasText("Grace"));
    }

    @Test
    public void updateLastName() {
        clickOn("#lastNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        clickOn("#lastNameTextField");
        write("Anderson");
        clickOn("#confirmButton");
        verifyThat("#adminLastNameLabel", LabeledMatchers.hasText("Anderson"));
    }

    @Test
    public void updatePassword() {
        clickOn("#passwordTextField");
        write("hey");
        clickOn("#cPasswordTextField");
        write("hey");
        clickOn("#confirmButton");
        clickOn("#adminLogoutButton");
        clickOn("#administratorTab");
        clickOn("#adminUsernameTextField");
        write("admin1");
        clickOn("#adminPasswordField");
        write("hey");
        clickOn("#loginAButton");
        verifyThat("#adminFirstnameLabel", LabeledMatchers.hasText("Anna"));
    }

    @Test
    public void passwordError() {
        clickOn("#passwordTextField");
        write("hey");
        clickOn("#cPasswordTextField");
        write("heyy");
        clickOn("#confirmButton");
        verifyThat("#errorLabel", LabeledMatchers.hasText("Your passwords don't match"));
    }

    @Test
    public void cancel() {
        clickOn("#firstNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        clickOn("#firstNameTextField");
        write("Annah");
        clickOn("#cancelButton");
        clickOn("#yesButton");
        verifyThat("#adminFirstnameLabel", LabeledMatchers.hasText("Anna"));

    }

}
