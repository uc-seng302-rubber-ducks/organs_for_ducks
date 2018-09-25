package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import odms.TestUtils.AppControllerMocker;
import odms.bridge.BloodTestBridge;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;
import odms.controller.gui.popup.logic.NewBloodTestLogicController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class NewBloodTestControllerTest {
    private ObservableList<BloodTest> bloodTests;
    private AppController controller = AppControllerMocker.getFullMock();
    private OkHttpClient client = mock(OkHttpClient.class);
    private Call call = mock(Call.class);
    private NewBloodTestLogicController newBloodTestLogicController;
    private BloodTestBridge bloodTestBridge;
    private User testUser;
    private Stage stage;

    @Before
    public void setUp() {
        ConfigPropertiesSession mockSession = mock(ConfigPropertiesSession.class);
        when(mockSession.getProperty(eq("server.url"))).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.url"), anyString())).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.token.header"), anyString())).thenReturn("x-auth-token");
        when(mockSession.getProperty(eq("server.token.header"))).thenReturn("x-auth-token");
        bloodTests = FXCollections.observableList(new ArrayList<>());
        testUser = new User("Anna", LocalDate.now(),"AAA9999");
        newBloodTestLogicController = spy(new NewBloodTestLogicController(testUser,mock(Stage.class)));

        bloodTestBridge = mock(BloodTestBridge.class);

        when(controller.getToken()).thenReturn("token");
        when(controller.getBloodTestBridge()).thenReturn(bloodTestBridge);
        when(client.newCall(any(Request.class))).thenReturn(call);
        doNothing().when(call).enqueue(any(Callback.class));

        ConfigPropertiesSession.setInstance(mockSession);
        AppController.setInstance(controller);
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
        ConfigPropertiesSession.setInstance(null);
    }

    @Test
    public void testAddNewBloodTest(){
        doNothing().when(bloodTestBridge).postBloodtest(any(BloodTest.class), anyString());
        BloodTest bloodTest = new BloodTest(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,LocalDate.now());
        newBloodTestLogicController.addBloodTest(LocalDate.now(),"1.0","1.0","1.0","1.0","1.0","1.0","1.0","1.0");
        verify(bloodTestBridge, times(1)).postBloodtest(bloodTest, testUser.getNhi());


    }
}
