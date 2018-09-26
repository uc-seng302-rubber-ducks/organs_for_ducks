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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;

public class BloodTestControllerTest {

    private ObservableList<BloodTest> bloodTests;
    private ObservableList<BloodTest> graphBloodTests;
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
        graphBloodTests = FXCollections.observableList(new ArrayList<>());
        testUser = new User("Anna", LocalDate.now(),"AAA9999");
        bloodTestsLogicController = spy(new BloodTestsLogicController(bloodTests, graphBloodTests, testUser));

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
    public void testGoNextPageNoPages() {
        bloodTests.add(new BloodTest());
        bloodTestsLogicController.gotoNextPage();

        assertTrue(bloodTests.size() == 1);
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
        doNothing().when(bloodTestBridge).deleteBloodTest(anyString(), anyString(), anyString());
        BloodTest bloodTest = new BloodTest();
        bloodTest.setBloodTestId(1);
        bloodTests.add(bloodTest);

        bloodTestsLogicController.deleteBloodTest(bloodTest);
        verify(bloodTestBridge, times(1)).deleteBloodTest(Integer.toString(bloodTest.getBloodTestId()), testUser.getNhi(), "token");
    }

    @Test
    public void updateBloodTest() {
        doNothing().when(bloodTestBridge).patchBloodTest(any(BloodTest.class), anyString(), anyString());
        BloodTest bloodTest = new BloodTest();
        bloodTest.setBloodTestId(1);
        bloodTest.setRedBloodCellCount(1.0);

        bloodTestsLogicController.updateBloodTest(bloodTest);
        verify(bloodTestBridge, times(1)).patchBloodTest(bloodTest, testUser.getNhi(), "token");
    }

    @Test
    public void testUpdateTableView() {
        ObservableList<BloodTest> bloodTests = FXCollections.observableList(new ArrayList<>());
        doNothing().when(bloodTestBridge).getBloodTests(anyString(), anyString(), anyString(), anyInt(), anyInt(), eq(bloodTests), eq(null));

        bloodTestsLogicController.updateTableView(0);
        verify(bloodTestBridge, times(1)).getBloodTests(testUser.getNhi(), LocalDate.now().minusYears(100).toString(), LocalDate.now().toString(), 30, 0, bloodTests, null);
    }

    @Test
    public void testUpdateGraphWithWeekTimeRange() {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusWeeks(1));

        bloodTestsLogicController.updateGraph("Week", null);
        verify(bloodTestBridge, times(1)).getBloodTests(testUser.getNhi(), bloodTest.getTestDate().toString(), LocalDate.now().toString(), 365, 0, graphBloodTests, null);
    }

    @Test
    public void testUpdateGraphWithFortnightTimeRange() {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusWeeks(2));

        bloodTestsLogicController.updateGraph("Fortnight", null);
        verify(bloodTestBridge, times(1)).getBloodTests(testUser.getNhi(), bloodTest.getTestDate().toString(), LocalDate.now().toString(), 365, 0, graphBloodTests, null);
    }

    @Test
    public void testUpdateGraphWithMonthTimeRange() {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusMonths(1));

        bloodTestsLogicController.updateGraph("Month", null);
        verify(bloodTestBridge, times(1)).getBloodTests(testUser.getNhi(), bloodTest.getTestDate().toString(), LocalDate.now().toString(), 365, 0, graphBloodTests, null);
    }

    @Test
    public void testUpdateGraphWithYearTimeRange() {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusYears(1));

        bloodTestsLogicController.updateGraph("Year", null);
        verify(bloodTestBridge, times(1)).getBloodTests(testUser.getNhi(), bloodTest.getTestDate().toString(), LocalDate.now().toString(), 365, 0, graphBloodTests, null);
    }

    @Test
    public void testGetGraphStartDateWithWeekTimeRange() {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusWeeks(1));
        String dateDisplayName = bloodTest.getTestDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        String testDate = bloodTestsLogicController.changeValuesBasedOnTimeRange(bloodTest, "Week");
        Assert.assertEquals(dateDisplayName, testDate);
    }

    @Test
    public void testGetGraphStartDateWithFortnightTimeRange() {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusWeeks(2));
        String date = bloodTest.getTestDate().toString();

        String testDate = bloodTestsLogicController.changeValuesBasedOnTimeRange(bloodTest, "Fortnight");
        Assert.assertEquals(date, testDate);
    }

    @Test
    public void testGetGraphStartDateWithMonthTimeRange() {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusMonths(1));
        String date = bloodTest.getTestDate().toString();

        String testDate = bloodTestsLogicController.changeValuesBasedOnTimeRange(bloodTest, "Month");
        Assert.assertEquals(date, testDate);
    }

    @Test
    public void testGetGraphStartDateWithYearTimeRange() {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusYears(1));
        String dateDisplayName = bloodTest.getTestDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        String testDate = bloodTestsLogicController.changeValuesBasedOnTimeRange(bloodTest, "Year");
        Assert.assertEquals(dateDisplayName, testDate);
    }
}
