package odms.GUITest2;

import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static odms.TestUtils.TableViewsMethod.getCell;
import static odms.TestUtils.TableViewsMethod.getCellValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

public class OrganExpiryGUITest extends ApplicationTest {
    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private User testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");
    private Collection<UserOverview> overviews = Collections.singletonList(UserOverview.fromUser(testUser));
    private OrgansBridge organsBridge = mock(OrgansBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {
        testUser.getDeathDetails().setMomentOfDeath(LocalDateTime.now());
        UserBridge bridge = mock(UserBridge.class);
        ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
        LoginBridge loginBridge = mock(LoginBridge.class);
        AppController application = AppControllerMocker.getFullMock();
        TransplantBridge transplantBridge = mock(TransplantBridge.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(application.getToken()).thenReturn("ahaahahahahhaha");
        when(application.getOrgansBridge()).thenReturn(organsBridge);
        when(application.getUsername()).thenReturn("0");

        when(application.getTransplantList()).thenReturn(new ArrayList<>());
        when(loginBridge.loginToServer(anyString(), anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        doNothing().when(organsBridge).getAvailableOrgansList(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), any(), null);
        doNothing().when(application).addUserOverview(any(UserOverview.class));

        when(application.getUserOverviews()).thenReturn(new HashSet<>(overviews));
        when(bridge.getUser(eq("ABC1244"))).thenReturn(testUser);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");

        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(testUser);

        //Use default clinician
        clickOn("#clinicianTab");
        setTextField(this, "#staffIdTextField", "0");
        setTextField(this, "#staffPasswordField", "admin");

        clickOnButton(this, "#loginCButton");

        clickOn("#searchTab");
        interact(() -> {
            lookup("#searchTableView").queryAs(TableView.class).setItems(FXCollections.observableList(Collections.singletonList(UserOverview.fromUser(testUser))));
            lookup("#searchTableView").queryAs(TableView.class).refresh();
        });
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#organsTab");
        clickOn("#canDonate");
        clickOnButton(this, "#donate");
        clickOnButton(this, "#donate");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void confirmExpireOrgan() {
        clickOnButton(this, "#expireOrganButton");
        clickOn("#expirationReasonTextArea");
        write("hello");
        clickOnButton(this, "#ExpiryConfirmButton");
        assertEquals("hello", getCellValue("#currentlyDonating", 2, 0).toString());
    }

    @Test
    public void CancelExpireOrgan() {
        clickOnButton(this, "#expireOrganButton");
        clickOn("#expirationReasonTextArea");
        write("hello");
        clickOnButton(this, "#cancelExpiryButton");
        assertEquals("", getCellValue("#currentlyDonating", 2, 0).toString());
    }

}
