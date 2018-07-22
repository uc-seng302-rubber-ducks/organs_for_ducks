package odms.GUITest1;


import odms.App;
import odms.TestUtils.TableViewsMethod;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class DeleteClinicianUserGUITest extends ApplicationTest {

    private AppController controller;
    private UserBridge bridge;
    private ClinicianBridge clinicianBridge;
    private LoginBridge loginBridge;
    private AdministratorBridge administratorBridge;
    private Collection<UserOverview> overviews;
    private User testUser = new User("A", LocalDate.now(), "ABC1234");
    private User testUser2 = new User("Aa", LocalDate.now(), "ABC1244");

    @BeforeClass
    public static void initialization() {
        //CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {
        controller = mock(AppController.class);
        bridge = mock(UserBridge.class);
        clinicianBridge = mock(ClinicianBridge.class);
        loginBridge = mock(LoginBridge.class);
        administratorBridge = mock(AdministratorBridge.class);

        AppController.setInstance(controller);
        when(controller.getUserBridge()).thenReturn(bridge);
        when(controller.getClinicianBridge()).thenReturn(clinicianBridge);
        when(controller.getAdministratorBridge()).thenReturn(administratorBridge);
        when(controller.getLoginBridge()).thenReturn(loginBridge);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();

        overviews = new ArrayList<>();

        overviews.add(UserOverview.fromUser(testUser));
        overviews.add(UserOverview.fromUser(testUser2));
        when(controller.getToken()).thenReturn("haHAA");
        when(bridge.loadUsersToController(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(overviews);
        when(bridge.getUser(anyString())).thenReturn(testUser);
        when(loginBridge.loginToServer(anyString(), anyString(), anyString())).thenReturn("haHAA");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(new Clinician("Default", "0", "admin"));

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
