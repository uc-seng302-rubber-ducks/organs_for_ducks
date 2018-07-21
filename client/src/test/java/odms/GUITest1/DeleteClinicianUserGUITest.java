package odms.GUITest1;


import odms.App;
import odms.controller.AppController;
import odms.commons.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import odms.TestUtils.CommonTestMethods;
import odms.TestUtils.TableViewsMethod;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

public class DeleteClinicianUserGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
        AppController.getInstance().getUsers().add(new User("Aa", LocalDate.now(), "ABC1244"));
        clickOn("#clinicianTab");
        clickOn("#staffIdTextField");
        write("0");
        clickOn("#staffPasswordField");
        write("admin");
        clickOn("#loginCButton");
        clickOn("#searchTab");
        doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void deleteUser() {
        clickOn("#deleteUser");
        clickOn("OK");
        verifyThat("#fNameLabel", LabeledMatchers.hasText("Default"));

    }

    @Test
    public void canceledDeleteUser() {
        clickOn("#deleteUser");
        clickOn("Cancel");
        clickOn("#backButton");
        verifyThat("#fNameLabel", LabeledMatchers.hasText("Default"));

    }

}
