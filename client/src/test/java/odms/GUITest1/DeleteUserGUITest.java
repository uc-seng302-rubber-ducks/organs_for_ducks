package odms.GUITest1;

import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.commons.model.User;
import odms.bridge.UserBridge;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

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

public class DeleteUserGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {

        UserBridge bridge = mock(UserBridge.class);
        AppController application = AppControllerMocker.getFullMock();
        User testUser = new User("A", LocalDate.now(), "ABC1234");

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getUserOverviews()).thenReturn(Collections.singleton(UserOverview.fromUser(testUser)));
        when(bridge.getUser("ABC1234")).thenReturn(testUser);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(testUser);
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        setTextField(this,"#userIDTextField","ABC1234");
        clickOnButton(this,"#loginUButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void deletedUser() {
        clickOn("#editMenuUser");
        clickOn("#deleteUser");
        clickOn("OK");
        setTextField(this,"#userIDTextField", "ABC1234");
        clickOnButton(this,"#loginUButton");
        verifyThat("#userWarningLabel", LabeledMatchers
                .hasText("User was not found. \nTo register a new user, please click sign up."));
    }

    @Test
    public void canceledDeletedUser() {
        clickOn("#editMenuUser");
        clickOn("#deleteUser");
        clickOn("Cancel");
        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }


}
