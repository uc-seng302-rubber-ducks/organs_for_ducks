package odms.GUITest1;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.Administrator;
import odms.controller.AppController;
import odms.utils.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class DeleteAdminGUITest extends ApplicationTest {

    private AppController appC = mock(AppController.class);
    private UserBridge uBridge = mock(UserBridge.class);
    private ClinicianBridge cBridge = mock(ClinicianBridge.class);
    private AdministratorBridge aBridge = mock(AdministratorBridge.class);
    private LoginBridge lBridge = mock(LoginBridge.class);
    private TransplantBridge tBridge = mock(TransplantBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {
        when(appC.getUserBridge()).thenReturn(uBridge);
        when(appC.getClinicianBridge()).thenReturn(cBridge);
        when(appC.getAdministratorBridge()).thenReturn(aBridge);
        when(appC.getLoginBridge()).thenReturn(lBridge);
        when(appC.getTransplantBridge()).thenReturn(tBridge);

        when(tBridge.getWaitingList(anyInt(), anyInt(), anyString(), anyString(), any(Collection.class))).thenReturn(new ArrayList());
        when(uBridge.getUsers(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(new ArrayList<>());
        when(cBridge.getClinicians(anyInt(), anyInt(), anyString(), anyString(), anyString())).thenReturn(new ArrayList<>());
        when(lBridge.loginToServer(anyString(), anyString(), anyString())).thenReturn("haHAA");
        when(aBridge.getAdmin(anyString(), anyString())).thenReturn(new Administrator("default", null, null, null, "admin"));

        AppController.setInstance(appC);
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getAdmins().clear();

        clickOn("#administratorTab");
        clickOn("#adminUsernameTextField");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void testCannotDeleteDefaultAdmin() {
        write("default", 0);
        lookup("#adminPasswordField").queryAs(TextField.class).setText("admin");
        clickOn("#loginAButton");
        verifyThat("#deleteAdminButton", Node::isDisabled);
    }

}
