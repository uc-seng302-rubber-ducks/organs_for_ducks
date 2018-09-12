package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.bridge.OrgansBridge;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AvailableOrgansControllerTest {

    private AppController controller = mock(AppController.class);
    private OkHttpClient client = mock(OkHttpClient.class);
    private Call call = mock(Call.class);
    private AvailableOrgansLogicController availableOrgansLogicController;
    private ObservableList<AvailableOrganDetail> availableOrganDetails;
    private ObservableList<TransplantDetails> transplantDetails;

    @Before
    public void setUp() {
        availableOrganDetails = FXCollections.observableList(new ArrayList<>());
        transplantDetails = FXCollections.observableList(new ArrayList<>());
        availableOrgansLogicController = new AvailableOrgansLogicController(availableOrganDetails,transplantDetails);
        OrgansBridge bridge = new OrgansBridge(client);
        when(controller.getOrgansBridge()).thenReturn(bridge);
        when(client.newCall(any(Request.class))).thenReturn(call);

    }

    @Test
    public void testNextPageNoPages() {
        doNothing().when(call).enqueue(any(Callback.class));
        availableOrganDetails.add(new AvailableOrganDetail(Organs.BONE, "ABC1234", LocalDateTime.now(), "Hutt Valley", "A+",0));
        availableOrgansLogicController.goNextPage(null);
        assertTrue(availableOrganDetails.size() == 1);
        verify(controller, times(0)).getOrgansBridge();

    }

    @Test
    public void testPrevPageNoPages() {
        doNothing().when(call).enqueue(any(Callback.class));
        availableOrganDetails.add(new AvailableOrganDetail(Organs.HEART, "DEF2314", LocalDateTime.now(), "Canterbury", "B-", 0));
        availableOrgansLogicController.goPrevPage(null);
        assertTrue(availableOrganDetails.size() == 1);
        verify(controller, never()).getOrgansBridge();
    }


}
