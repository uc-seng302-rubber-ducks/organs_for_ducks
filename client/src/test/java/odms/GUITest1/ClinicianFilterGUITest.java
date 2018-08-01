package odms.GUITest1;

import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.TestUtils.TableViewsMethod;
import odms.commons.model.Clinician;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model._enum.Environments;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.utils.ClinicianBridge;
import odms.utils.LoginBridge;
import odms.utils.TransplantBridge;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class ClinicianFilterGUITest extends ApplicationTest {

    private AppController application;
    private UserBridge bridge;
    private ClinicianBridge clinicianBridge;
    private LoginBridge loginBridge;
    private TransplantBridge transplantBridge;
    private Collection<UserOverview> overviews;
    private User adam;

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUp() throws TimeoutException, IOException {
        Log.setup(Environments.TEST);

        application = mock(AppController.class);
        bridge = mock(UserBridge.class);
        clinicianBridge = mock(ClinicianBridge.class);
        loginBridge = mock(LoginBridge.class);
        transplantBridge = mock(TransplantBridge.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");
        clinician.setProfilePhotoFilePath("");
        adam = new User("Adam", LocalDate.now(), "ABC1234");
        adam.setContact(new EmergencyContact("Letifa", "0118999124", "1456789"));
        adam.getUndoStack().clear();
        overviews = new ArrayList<>();
        overviews.add(UserOverview.fromUser(adam));

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(application.getToken()).thenReturn("WoWee");

        when(transplantBridge.getWaitingList(anyInt(), anyInt(), anyString(), anyString(), anyCollection())).thenReturn(new ArrayList<>());
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        when(application.getUserOverviews()).thenReturn(new HashSet<>(overviews));
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
        setTextField(this, "#searchTextField","Adam" );
        doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }

    @Test
    public void testFilterManyResults() {
        for (int i = 0; i < 50; i++) {
            User user = new User(Integer.toString(i), LocalDate.now(), "ABC00" + ((i < 10 ? "0" + i : i)));
            user.setFirstName("#");
            user.setLastName(Integer.toString(i));
            overviews.add(UserOverview.fromUser(user));
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
