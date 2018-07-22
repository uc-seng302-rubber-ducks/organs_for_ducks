package odms.GUITest1;

import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.TestUtils.TableViewsMethod;
import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

        AppController application = mock(AppController.class);
        UserBridge bridge = mock(UserBridge.class);
        ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
        LoginBridge loginBridge = mock(LoginBridge.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");
        User adam = new User("Adam", LocalDate.now(), "ABC1234");
        adam.setContact(new EmergencyContact("Letifa", "0118999124", "1456789"));
        adam.getUndoStack().clear();

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        when(bridge.loadUsersToController(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(UserOverview.fromUser(adam)));
        when(bridge.getUser("ABC1234")).thenReturn(adam);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);

        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(adam);

        clickOn("#clinicianTab");
        setTextField(this,"#staffIdTextField" ,"0");
        setTextField(this, "#staffPasswordField","admin");

    }


    @Test
    public void testFilterName(){
        clickOnButton(this,"#loginCButton");
        clickOn("#searchTab");
        clickOn("#searchTextField");

        setTextField(this, "#searchTextField","Adam" );
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
        clickOnButton(this,"#loginCButton");
        clickOn("#searchTab");
        setTextField(this, "#searchTextField","Adam" );
        doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }
}
