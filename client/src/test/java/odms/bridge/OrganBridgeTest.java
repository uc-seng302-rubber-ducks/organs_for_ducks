package odms.bridge;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.commons.exception.ApiException;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.testfx.api.FxToolkit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrganBridgeTest extends BridgeTestBase {

    private OrgansBridge bridge;

    @Before
    public void setUp() {
        bridge = new OrgansBridge(mockClient);
    }

    @Test
    public void getAvailableOrgansShouldHaveCorrectUrlWithNoParams() {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mock(Call.class));
        ObservableList<AvailableOrganDetail> oList = FXCollections.emptyObservableList();


        bridge.getAvailableOrgansList(0, 10, null, null, null, null, null, oList);
        verify(mockClient).newCall(requestCaptor.capture());


        Request request = requestCaptor.getValue();
        Assert.assertTrue(request.url().toString().endsWith("availableOrgans?count=10&startIndex=0"));

    }

    @Test
    public void getAvailableOrgansShouldHaveCorrectUrlWithAllParams() {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mock(Call.class));
        ObservableList<AvailableOrganDetail> oList = FXCollections.emptyObservableList();


        bridge.getAvailableOrgansList(0, 10, "Liver", "a", "b", "c", "d", oList);
        verify(mockClient).newCall(requestCaptor.capture());

        Request request = requestCaptor.getValue();
        Assert.assertTrue(request.url().toString().endsWith("availableOrgans?count=10&startIndex=0&organ=Liver&region=a&bloodType=b&city=c&country=d"));
    }

    @Test
    public void getAvailableOrgansShouldNotAllowSpacesInUrl() {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mock(Call.class));
        ObservableList<AvailableOrganDetail> oList = FXCollections.emptyObservableList();


        bridge.getAvailableOrgansList(5, 11, "bone marrow", "over there", "O-", "Christchurch", "New Zealand", oList);
        verify(mockClient).newCall(requestCaptor.capture());

        Request request = requestCaptor.getValue();
        Assert.assertTrue(request.url().toString().endsWith("availableOrgans?count=11&startIndex=5&organ=bone_marrow&region=over_there&bloodType=O-&city=Christchurch&country=New_Zealand"));
    }

    @Test(expected = ApiException.class)
    public void getAvailableOrgansShouldThrowExceptionOnBadResponseCode() throws IOException {
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Call mockCall = mock(Call.class);

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ObservableList<AvailableOrganDetail> oList = FXCollections.emptyObservableList();

        //run method and catch the callback
        bridge.getAvailableOrgansList(0, 10, null, null, null, null, null, oList);
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        //run the callback and check output
        callback.onResponse(mockCall, jsonResponseMock("doesn't matter", 403));
    }

    @Test
    public void getAvailableOrgansShouldAddToObservableListOnValidResponse() throws Exception {
        //initialise toolkit so platform.runlater works
        FxToolkit.registerPrimaryStage();

        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ObservableList<AvailableOrganDetail> mockList = mock(ObservableList.class);

        //run method and catch the callback
        bridge.getAvailableOrgansList(0, 10, null, null, null, null, null, mockList);
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        //run the callback and check output
        callback.onResponse(mockCall, jsonResponseMock(Collections.singletonList(new AvailableOrganDetail()), 200));
        waitForRunLater();
        verify(mockList, times(1)).addAll(any(List.class));

    }

    @Test
    public void getMatchingOrgansShouldBuildCorrectUrl() {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mock(Call.class));


        bridge.getMatchingOrgansList(0, 10, "ABC1234",
                new AvailableOrganDetail(Organs.HEART, "", LocalDateTime.now(), "", "", Long.valueOf("0")), mock(ObservableList.class));
        verify(mockClient).newCall(requestCaptor.capture());


        Request request = requestCaptor.getValue();
        Assert.assertTrue(request.url().toString().endsWith("matchingOrgans?count=10&organ=Heart&startIndex=0&donorNhi=ABC1234"));
    }

    @Test
    public void getMatchingOrgansShouldAddToObservableListOnValidResponse() throws Exception {
        //initialise toolkit so platform.runlater works
        FxToolkit.registerPrimaryStage();

        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ObservableList<TransplantDetails> mockList = mock(ObservableList.class);

        //run method and catch the callback
        bridge.getMatchingOrgansList(0, 15, "TES4321",
                new AvailableOrganDetail(Organs.HEART, "", LocalDateTime.now(), "", "", Long.valueOf("0")), mockList);
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        //run the callback and check output
        callback.onResponse(mockCall, jsonResponseMock(Collections.singletonList(mock(TransplantDetails.class)), 200));
        waitForRunLater();
        verify(mockList, times(1)).addAll(any(List.class));
    }
}
