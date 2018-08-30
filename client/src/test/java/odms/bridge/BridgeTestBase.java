package odms.bridge;

import com.google.gson.Gson;
import javafx.application.Platform;
import odms.TestUtils.AppControllerMocker;
import odms.commons.config.ConfigPropertiesSession;
import odms.controller.AppController;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.Semaphore;

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
    static void staticBefore() {
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
        when(mockSession.getProperty(eq("testConfig"), anyString())).thenReturn("true");
        when(mockSession.getProperty(eq("testConfig"))).thenReturn("true");
        when(mockController.getToken()).thenReturn("abcd");

        ConfigPropertiesSession.setInstance(mockSession);
        AppController.setInstance(mockController);
    }

    @After
    public void after() {
        AppController.setInstance(null);
        ConfigPropertiesSession.setInstance(null);
    }

    /**
     * helper function to wait for a Platform.runLater call (often used for populating lists)
     * stolen from stack overflow.
     *
     * @throws InterruptedException if the thread is interrupted
     */
    void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }

    /**
     * helper function to create a mock response that returns a json body
     *
     * @param toJsonify object to be returned as json in response.body.string
     * @param code      response code to be returned
     * @return mocked response object
     */
    Response jsonResponseMock(Object toJsonify, int code) throws IOException {
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponse.code()).thenReturn(code);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(toJsonify));

        return mockResponse;
    }
}
