package odms.GUITest1;

import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.TestUtils.TableViewsMethod;
import odms.commons.model.Clinician;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.utils.ClinicianBridge;
import odms.utils.UserBridge;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class ClinicianFilterGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUp() throws TimeoutException, IOException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        User adam = new User("Adam", LocalDate.now(), "ABC1234");
        adam.setContact(new EmergencyContact("Letifa", "0118999124", "1456789"));
        adam.getUndoStack().clear();
        UserBridge bridge = mock(UserBridge.class);
        when(bridge.loadUsersToController(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(Collections.singletonList(UserOverview.fromUser(adam)));
        when(bridge.getUser("ABC1234")).thenReturn(adam);

        ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
        Clinician clinician = new Clinician();
        clinician.setStaffId("0");
        clinician.setFirstName("default");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);

        AppController application = mock(AppController.class);
        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        AppController.getInstance().getUsers().clear();
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
        write("Adam", 300);
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
        write("Adam", 300);
        doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }
}
