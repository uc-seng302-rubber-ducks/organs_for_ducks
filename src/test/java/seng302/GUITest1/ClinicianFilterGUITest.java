package seng302.GUITest1;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Utils.CommonTestMethods;
import seng302.Utils.TableViewsMethod;
import seng302.controller.AppController;
import seng302.model.EmergencyContact;
import seng302.model.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

public class ClinicianFilterGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUp() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        User adam = new User("Adam", LocalDate.now(), "ABC1234");
        adam.setContact(new EmergencyContact("Letifa", "0118999124", "1456789"));
        adam.getUndoStack().clear();
        AppController.getInstance().getUsers().add(adam);
        clickOn("#clinicianTab");
        clickOn("#staffIdTextField");
        write("0", 0);
        clickOn("#staffPasswordField");
        write("admin", 0);

    }

    @Test
    public void testFilterName() {
        clickOn("#loginCButton");
        clickOn("#searchTab");
        clickOn("#searchTextField");
        System.out.println(AppController.getInstance().getUsers());
        write("Adam", 0);
        doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }

    @Test
    public void testFilterManyResults() {
        for (int i = 0; i < 100; i++) {
            User user = new User(Integer.toString(i), LocalDate.now(), "ABC00" + ((i < 10 ? "0" + i : i)));
            user.setFirstName("#");
            user.setLastName(Integer.toString(i));
            AppController.getInstance().getUsers().add(user);
        }
        clickOn("#loginCButton");
        clickOn("#searchTab");
        clickOn("#searchTextField");
        write("Adam", 0);
        doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }
}
