package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.TestUtils.AppControllerMocker;
import odms.bridge.BloodTestBridge;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.model.User;
import odms.commons.model.datamodel.BloodTest;
import odms.controller.gui.panel.logic.BloodTestsLogicController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;

public class BloodTestControllerTest {

    private ObservableList<BloodTest> bloodTests;
    private AppController controller = AppControllerMocker.getFullMock();
    private OkHttpClient client = mock(OkHttpClient.class);
    private Call call = mock(Call.class);
    private BloodTestsLogicController bloodTestsLogicController;
    private BloodTestBridge bloodTestBridge;
    private User testUser;

    @Before
    public void setUp() {
        ConfigPropertiesSession mockSession = mock(ConfigPropertiesSession.class);
        when(mockSession.getProperty(eq("server.url"))).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.url"), anyString())).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.token.header"), anyString())).thenReturn("x-auth-token");
        when(mockSession.getProperty(eq("server.token.header"))).thenReturn("x-auth-token");

        bloodTests = FXCollections.observableList(new ArrayList<>());
        testUser = new User("Anna", LocalDate.now(),"AAA9999");
        bloodTestsLogicController = spy(new BloodTestsLogicController(bloodTests,testUser));

        bloodTestBridge = mock(BloodTestBridge.class);

        when(controller.getToken()).thenReturn("token");
        doNothing().when(bloodTestBridge).postBloodtest(any(BloodTest.class), anyString());
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
    public void testGoNextPageNoPages() {
        bloodTests.add(new BloodTest());
        bloodTestsLogicController.gotoNextPage();

        assertTrue(bloodTests.size() == 1);
        // this is one as it is called in the constructor
        verify(controller, times(0)).getBloodTestBridge();
    }

    @Test
    public void testGoPrevPageNoPages() {
        bloodTests.add(new BloodTest());
        bloodTestsLogicController.goToPreviousPage();

        assertTrue(bloodTests.size() == 1);
        // this is one as it is called in the constructor
        verify(controller, times(0)).getBloodTestBridge();
    }

    @Test
    public void testDeleteBloodTest() {
        doNothing().when(bloodTestBridge).deleteBloodtest(anyString(), anyString());
        BloodTest bloodTest = new BloodTest();
        bloodTest.setBloodTestId(1);
        bloodTests.add(bloodTest);

        bloodTestsLogicController.deleteBloodTest(bloodTest);
        verify(bloodTestBridge, times(1)).deleteBloodtest(Integer.toString(bloodTest.getBloodTestId()), testUser.getNhi());
    }

    @Test
    public void updateBloodTest() {
        doNothing().when(bloodTestBridge).patchBloodtest(any(BloodTest.class), anyString());
        BloodTest bloodTest = new BloodTest();
        bloodTest.setBloodTestId(1);
        bloodTest.setRedBloodCellCount(1.0);

        bloodTestsLogicController.updateBloodTest(bloodTest);
        verify(bloodTestBridge, times(1)).patchBloodtest(bloodTest, testUser.getNhi());
    }

    @Test
    public void testUpdateTableView() {
        ObservableList<BloodTest> bloodTests = FXCollections.observableList(new ArrayList<>());
        doNothing().when(bloodTestBridge).getBloodTests(anyString(), anyString(),anyString(),anyInt(),anyInt(), eq(bloodTests));

        bloodTestsLogicController.updateTableView(0);
        verify(bloodTestBridge, times(1)).getBloodTests(testUser.getNhi(),LocalDate.now().minusYears(100).toString(),LocalDate.now().toString(),30,0,bloodTests);
    }


}
