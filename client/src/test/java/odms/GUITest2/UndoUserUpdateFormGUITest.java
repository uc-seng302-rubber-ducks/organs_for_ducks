package odms.GUITest2;

import javafx.scene.Node;
import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.utils.UserBridge;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class UndoUserUpdateFormGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUp() throws TimeoutException, IOException {

        AppController application = mock(AppController.class);
        UserBridge bridge = mock(UserBridge.class);

        AppController.setInstance(application);
        User user = new User("Frank", LocalDate.now().minusDays(2), "ABC1234");
        user.setDateOfDeath(LocalDate.now());
        user.setContact(new EmergencyContact("", "", "0187878"));
        user.getUndoStack().clear();
        when(application.getUserBridge()).thenReturn(bridge);
        when(bridge.getUsers(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(UserOverview.fromUser(user)));
        when(bridge.getUser("ABC1234")).thenReturn(user);

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
        clickOnButton(this,"#editDetailsButton");
        setTextField(this, "#preferredFNameTextField","i");

        clickOnButton(this,"#undoUpdateButton");
        verifyThat("#preferredFNameTextField", TextInputControlMatchers.hasText("Frank"));

    }

    @Test
    public void NoChangeUndoDisabled() {
        clickOnButton(this,"#editDetailsButton");
        verifyThat("#undoUpdateButton", Node::isDisabled);
    }

    @Test
    public void ChangesResetWhenCancelButtonClicked() {
        //Dont change me to the new methods ill break
        clickOn("#editDetailsButton");
        clickOn("#mNameInput");
        write("geoff");
        clickOn("#smokerCheckBox");

        clickOn("#cancelButton");
        clickOn("#yesButton");

        verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
        verifyThat("#mNameValue", LabeledMatchers.hasText(""));
    }

    @Test
    public void ChangesResetWhenWindowClosed() {
        //by clicking the X
        //TODO unsure how to use system controls from testFX
    }

    @Test
    public void MultipleChangesSummedInMainWindow() {
        clickOnButton(this,"#editDetailsButton");
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
        clickOnButton(this,"#editDetailsButton");
        setTextField(this,"#ecPhone","1234");

        clickOnButton(this,"#undoUpdateButton");

        verifyThat("#ecPhone", TextInputControlMatchers.hasText("123"));
    }

    @Test
    public void MultipleChangesEqualUndos() {
        clickOnButton(this,"#editDetailsButton");

//    unable to check text in combo boxes as it is lazily created/populated
        clickOn("#genderIdComboBox");
        clickOn("Male");

        setTextField(this,"#heightInput","1");

        setTextField(this,"#lNameInput","qw");

        clickOnButton(this,"#undoUpdateButton");
        clickOnButton(this,"#undoUpdateButton");
        clickOnButton(this,"#undoUpdateButton");


        verifyThat("#lNameInput", TextInputControlMatchers.hasText(""));
        verifyThat("#heightInput", TextInputControlMatchers.hasText(""));
    }

    @Test
    public void MultipleActionsTwoUndosOneAction() {
        //check we can traverse the stack properly
        clickOnButton(this,"#editDetailsButton");

        setTextField(this,"#heightInput","1");

        setTextField(this,"#lNameInput","qw");

        clickOnButton(this,"#undoUpdateButton");
        clickOnButton(this,"#undoUpdateButton");

        setTextField(this,"#lNameInput","lasagna");

        verifyThat("#heightInput", TextInputControlMatchers.hasText("1"));
        verifyThat("#lNameInput", TextInputControlMatchers.hasText("lasagna"));
    }
}
