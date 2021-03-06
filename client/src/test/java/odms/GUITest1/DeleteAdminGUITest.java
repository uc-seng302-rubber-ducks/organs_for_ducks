package odms.GUITest1;

import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.exception.UnauthorisedException;
import odms.commons.model.Administrator;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeleteAdminGUITest extends ApplicationTest {

    private AppController appC = mock(AppController.class);
    private UserBridge uBridge = mock(UserBridge.class);
    private ClinicianBridge cBridge = mock(ClinicianBridge.class);
    private AdministratorBridge aBridge = mock(AdministratorBridge.class);
    private LoginBridge lBridge = mock(LoginBridge.class);
    private TransplantBridge tBridge = mock(TransplantBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException, UnauthorisedException {
        when(appC.getUserBridge()).thenReturn(uBridge);
        when(appC.getClinicianBridge()).thenReturn(cBridge);
        when(appC.getAdministratorBridge()).thenReturn(aBridge);
        when(appC.getLoginBridge()).thenReturn(lBridge);
        when(appC.getTransplantBridge()).thenReturn(tBridge);

        when(appC.getTransplantList()).thenReturn(new ArrayList());
        when(appC.getUserOverviews()).thenReturn(new HashSet<>());
        when(appC.getClinicians()).thenReturn(new ArrayList<>());
        when(lBridge.loginToServer(anyString(), anyString(), anyString())).thenReturn("haHAA");
        when(aBridge.getAdmin(anyString(), anyString())).thenReturn(new Administrator("default", null, null, null, "admin"));

        AppController.setInstance(appC);
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");
        AppController.getInstance().getAdmins().clear();

        clickOn("#administratorTab");
        clickOn("#adminUsernameTextField");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }
}
