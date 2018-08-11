package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.bridge.AvailableOrgansBridge;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AvailableOrgansControllerTest {

    private AppController controller = mock(AppController.class);
    private OkHttpClient client = mock(OkHttpClient.class);
    private Call call = mock(Call.class);
    private AvailableOrgansLogicController availableOrgansLogicController;
    private ObservableList<AvailableOrganDetail> availableOrganDetails;


    @Before
    public void setUp() {
        availableOrganDetails = FXCollections.observableList(new ArrayList<>());
        availableOrgansLogicController = new AvailableOrgansLogicController(availableOrganDetails);
        AvailableOrgansBridge bridge = new AvailableOrgansBridge(client);
        when(controller.getAvailableOrgansBridge()).thenReturn(bridge);
        when(client.newCall(any(Request.class))).thenReturn(call);

    }

    @Test
    public void testNextPageNoPages() {
        doNothing().when(call).enqueue(any(Callback.class));
        availableOrganDetails.add(new AvailableOrganDetail(Organs.BONE, "ABC1234", LocalDateTime.now(), "Hutt Valley", "A+"));
        availableOrgansLogicController.goNextPage();
        assertTrue(availableOrganDetails.size() == 1);
        verify(controller, times(0)).getAvailableOrgansBridge();

    }

    @Test
    public void testPrevPageNoPages() {
        doNothing().when(call).enqueue(any(Callback.class));
        availableOrganDetails.add(new AvailableOrganDetail(Organs.HEART, "DEF2314", LocalDateTime.now(), "Canterbury", "B-"));
        availableOrgansLogicController.goPrevPage();
        assertTrue(availableOrganDetails.size() == 1);
        verify(controller, never()).getAvailableOrgansBridge();
    }


}