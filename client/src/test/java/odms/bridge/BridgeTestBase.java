package odms.bridge;

import odms.TestUtils.AppControllerMocker;
import odms.commons.config.ConfigPropertiesSession;
import odms.controller.AppController;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Before;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BridgeTestBase {

    OkHttpClient mockClient;
    AppController mockController;
    ConfigPropertiesSession mockSession;

    /**
     * static version of before. this is used for the parameterized GetExistsTest
     * as the setup needs to be run before params are created
     */
    static void before2() {
        BridgeTestBase base = new BridgeTestBase();
        base.before();
    }

    /**
     * some common setup done across all bridge tests.
     * fake url and token via mocked AppController and ConfigPropertiesSession
     */
    @Before
    public void before() {
        mockClient = mock(OkHttpClient.class);
        mockController = AppControllerMocker.getFullMock();
        mockSession = mock(ConfigPropertiesSession.class);
        when(mockSession.getProperty(eq("server.url"))).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.url"), anyString())).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.token.header"), anyString())).thenReturn("x-auth-token");
        when(mockSession.getProperty(eq("server.token.header"))).thenReturn("x-auth-token");
        when(mockController.getToken()).thenReturn("abcd");

        ConfigPropertiesSession.setInstance(mockSession);
        AppController.setInstance(mockController);
    }

    @After
    public void after() {
        AppController.setInstance(null);
        ConfigPropertiesSession.setInstance(null);
    }
}
