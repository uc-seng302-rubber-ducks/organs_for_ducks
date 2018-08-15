package odms.GUITest2;

import javafx.scene.Node;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.UserBridge;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class UndoUserUpdateFormGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUp() throws TimeoutException, IOException {

        AppController application = AppControllerMocker.getFullMock();
        UserBridge bridge = mock(UserBridge.class);

        AppController.setInstance(application);
        User user = new User("Frank", LocalDate.now().minusDays(2), "ABC1234");
        user.setPreferredFirstName("Frank");
        user.setDateOfDeath(LocalDate.now());
        user.setContact(new EmergencyContact("", "", "0187878"));
        user.getUndoStack().clear();
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getUserOverviews()).thenReturn(Collections.singleton(UserOverview.fromUser(user)));
        when(bridge.getUser("ABC1234")).thenReturn(user);
        when(application.getName()).thenReturn("Jeff");
        when(application.getUsername()).thenReturn("erson");

        doCallRealMethod().when(application).setUserController(any(UserController.class));
        doCallRealMethod().when(application).getUserController();

        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(user);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);


        setTextField(this, "#userIDTextField","ABC1234");
        clickOnButton(this, "#loginUButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void SingleChangeSingleUndo() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        setTextField(this, "#preferredFNameTextField","i");

        clickOnButton(this,"#undoUpdateButton");
        verifyThat("#preferredFNameTextField", TextInputControlMatchers.hasText("Frank"));

    }

    @Test
    public void NoChangeUndoDisabled() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        verifyThat("#undoUpdateButton", Node::isDisabled);
    }

    @Test
    @Ignore
    public void ChangesResetWhenCancelButtonClicked() {
        //Dont change me to the new methods ill break
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        clickOn("#mNameInput");
        write("geoff");
        clickOn("#smokerCheckBox");

        clickOn("#cancelButton");
        clickOn("#yesButton");

        verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
        verifyThat("#mNameValue", LabeledMatchers.hasText(""));
    }


    @Test
    public void MultipleChangesSummedInMainWindow() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        setTextField(this,"#mNameInput","geoff");
        clickOn("#smokerCheckBox");
        clickOnButton(this,"#confirmButton");

        verifyThat("#smokerValue", LabeledMatchers.hasText("Yes"));
        verifyThat("#mNameValue", LabeledMatchers.hasText("geoff"));

        clickOnButton(this,"#undoButton");

        verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
        verifyThat("#mNameValue", LabeledMatchers.hasText(""));
    }

    @Test
    public void MultipleChangesSingleUndo() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        setTextField(this,"#ecPhone","123");
        setTextField(this,"#ecPhone","1234");

        clickOnButton(this,"#undoUpdateButton");
        sleep(1000);
        verifyThat("#ecPhone", TextInputControlMatchers.hasText("123"));
    }

    @Test
    public void MultipleChangesEqualUndos() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");

//    unable to check text in combo boxes as it is lazily created/populated
        clickOn("#genderIdComboBox");
        clickOn("Male");

        clickOn("#heightInput");
        write("1");

        clickOn("#lNameInput");
        write("qw");

        clickOnButton(this,"#undoUpdateButton");
        clickOnButton(this,"#undoUpdateButton");
        clickOnButton(this,"#undoUpdateButton");


        verifyThat("#lNameInput", TextInputControlMatchers.hasText(""));
        verifyThat("#heightInput", TextInputControlMatchers.hasText("0.0"));
    }

    @Test
    public void
    MultipleActionsTwoUndosOneAction() {
        //check we can traverse the stack properly
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");

        clickOn("#heightInput");
        write("1");

        clickOn("#lNameInput");
        write("qw");

        clickOnButton(this,"#undoUpdateButton");
        clickOnButton(this,"#undoUpdateButton");

        clickOn("#lNameInput");
        write("lasagna");

        verifyThat("#heightInput", TextInputControlMatchers.hasText("0.01"));
        verifyThat("#lNameInput", TextInputControlMatchers.hasText("lasagna"));
    }
}
