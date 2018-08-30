package odms.bridge;

import com.google.gson.Gson;
import odms.commons.exception.ApiException;
import odms.commons.model.Administrator;
import odms.commons.model.AdministratorBuilder;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdministratorBridgeTest extends BridgeTestBase {

    private AdministratorBridge bridge;

    @Before
    public void setUp() {
        bridge = new AdministratorBridge(mockClient);
    }

    @Test
    public void getAdminsShouldAddAdminsOnSuccess() throws IOException {
        //mocked request effectively returns an empty list
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(new Administrator[]{
                new Administrator("hackerman", "Jeff", "copyright", "hello there", "password")
        }));

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        //run getAdmins, verify and get the callback it uses.
        bridge.getAdmins(0, 10, "", "tokenGoesHere");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();


        callback.onResponse(mockCall, mockResponse);
        verify(mockController, times(1)).addAdmin(any(Administrator.class));
    }


    @Test
    public void getAdminsShouldNotAddOnEmptyResponse() throws IOException {
        //mocked request effectively returns an empty list
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponseBody.string()).thenReturn("[]");

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        //run getAdmins, verify and get the callback it uses.
        bridge.getAdmins(0, 10, "", "tokenGoesHere");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();


        callback.onResponse(mockCall, mockResponse);
        verify(mockController, times(0)).addAdmin(any(Administrator.class));
    }

    @Test(expected = IOException.class)
    public void postAdminShouldThrowExceptionOnBadResponse() throws IOException {
        //mocked request effectively returns an empty list
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        //run postAdmin, verify and get the callback it uses.
        bridge.postAdmin(new Administrator("hackerman", "Jeff", "copyright", "hello there", "password"), "tokenGoesHere");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

    }

    @Test
    public void postAdminShouldCloseResponseOnSuccess() throws IOException {
        //mocked request effectively returns an empty list
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        //run postAdmin, verify and get the callback it uses.
        bridge.postAdmin(new Administrator("hackerman", "Jeff", "copyright", "hello there", "password"), "tokenGoesHere");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

        verify(mockResponse, times(1)).close();
    }


    @Test(expected = IOException.class)
    public void putAdminShouldThrowExceptionOnBadResponse() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        bridge.putAdmin(new Administrator("hackerman", "Jeff", "copyright", "hello there", "password"), "ABC1234", "tokenGoesHere");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

    }

    @Test
    public void putAdminShouldCloseResponseOnSuccess() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        bridge.putAdmin(new Administrator("hackerman", "Jeff", "copyright", "hello there", "password"), "ABC1234", "tokenGoesHere");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

        verify(mockResponse, times(1)).close();
    }


    @Test(expected = IOException.class)
    public void deleteAdminShouldThrowExceptionOnBadResponse() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);


        bridge.deleteAdmin(new Administrator("hackerman", "Jeff", "copyright", "hello there", "password"), "ABC1234");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

    }

    @Test
    public void deleteAdminShouldCloseResponseOnSuccess() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);


        bridge.deleteAdmin(new Administrator("hackerman", "Jeff", "copyright", "hello there", "password"), "ABC1234");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

        verify(mockResponse, times(1)).close();
    }


    @Test
    public void getAdminShouldReturnNullOnFailingToSend() throws IOException {
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException());

        Administrator result = bridge.getAdmin("default", "tokenGoesHere");

        Assert.assertEquals(null, result);
    }

    @Test
    public void getAdminShouldThrowExceptionOn500range() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(500);
        try {
            bridge.getAdmin("default", "tokenGoesHere");
            Assert.fail("no exception thrown");
        } catch (ApiException expectedEx) {
            Assert.assertTrue(500 <= expectedEx.getResponseCode() && expectedEx.getResponseCode() <= 599);
        }
    }

    @Test
    public void getAdminShouldThrowExceptionOn400range() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(403);
        try {
            bridge.getAdmin("default", "tokenGoesHere");
            Assert.fail("no exception thrown");
        } catch (ApiException expectedEx) {
            Assert.assertTrue(400 <= expectedEx.getResponseCode() && expectedEx.getResponseCode() <= 499);
        }
    }

    @Test
    public void getAdminShouldThrowExceptionOn400response() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(400);
        try {
            bridge.getAdmin("default", "tokenGoesHere");
            Assert.fail("no exception thrown");
        } catch (ApiException expectedEx) {
            Assert.assertEquals(400, expectedEx.getResponseCode());
        }
    }

    @Test
    public void getAdminShouldThrowExceptionOnNon200response() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(201);
        try {
            bridge.getAdmin("default", "tokenGoesHere");
            Assert.fail("no exception thrown");
        } catch (ApiException expectedEx) {
            Assert.assertEquals(201, expectedEx.getResponseCode());
        }
    }

    @Test
    public void getAdminShouldReturnAdminOnSucessfulRequest() throws IOException {
        Administrator expected = new AdministratorBuilder().setUserName("hackerman").setFirstName("geoff").setLastName("geofferson").build();

        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(expected));

        Administrator actual = bridge.getAdmin("default", "tokenGoesHere");
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assert.assertEquals(expected.getLastName(), actual.getLastName());
    }

    @Test
    public void getAdminShouldReturnNullWhenBadResponseBodyReceived() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenThrow(new IOException());

        Administrator response = bridge.getAdmin("default", "tokenGoesHere");

        Assert.assertNull(response);
    }


}
