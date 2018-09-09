package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.TestUtils.AppControllerMocker;
import odms.bridge.AppointmentsBridge;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ClinicianAppointmentControllerTest {

    private ObservableList<Appointment> appointments;
    private AppController controller = AppControllerMocker.getFullMock();
    private OkHttpClient client = mock(OkHttpClient.class);
    private Call call = mock(Call.class);
    private ClinicianAppointmentRequestLogicController clinicianAppointmentRequestLogicController;


    @Before
    public void setUp() {
        appointments = FXCollections.observableList(new ArrayList<>());
        Clinician testClinician = new Clinician("default", "0", "password");
        clinicianAppointmentRequestLogicController = new ClinicianAppointmentRequestLogicController(appointments, controller, testClinician);
        AppointmentsBridge appointmentsBridge = new AppointmentsBridge(client);

        when(controller.getAppointmentsBridge()).thenReturn(appointmentsBridge);
        when(client.newCall(any(Request.class))).thenReturn(call);

        AppController.setInstance(controller);
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
    }

    @Test
    public void testGoNextPageNoPages() {
        doNothing().when(call).enqueue(any(Callback.class));
        appointments.add(new Appointment());
        clinicianAppointmentRequestLogicController.goToNextPage();

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

    @Test
    public void testGoPrevPageNoPages() {
        doNothing().when(call).enqueue(any(Callback.class));
        appointments.add(new Appointment());
        clinicianAppointmentRequestLogicController.goToPreviousPage();

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

    @Ignore // todo: make test when cancel functionality is implemented
    @Test
    public void testAcceptAppointment() {

    }

    @Ignore // todo: make test when cancel functionality is implemented
    @Test
    public void testRejectAppointment() {

    }


}
