package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.database.db_strategies.AppointmentUpdateStrategy;
import odms.commons.model.Appointment;
import odms.commons.model._enum.EventTypes;
import odms.commons.model._enum.UserType;
import odms.commons.utils.Log;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

@OdmsController
public class AppointmentController extends BaseController {

    private DBHandler handler;
    private JDBCDriver driver;
    private SocketHandler socketHandler;

    @Autowired
    public AppointmentController(DBManager manager, SocketHandler socketHandler) {
        super(manager);
        handler = super.getHandler();
        driver = super.getDriver();
        this.socketHandler = socketHandler;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{nhi}/appointments/exists")
    public boolean pendingExists(@PathVariable(name = "nhi") String nhi,
                                 @RequestParam(name = "status") int statusId) {
        try (Connection connection = driver.getConnection()) {
            return handler.pendingExists(connection, nhi, statusId);
        } catch (SQLException e) {
            Log.severe("", e);
            throw new ServerDBException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{nhi}/appointments")
    public Collection<Appointment> getUserAppointments(@RequestParam(name = "count") int count,
                                                   @RequestParam(name = "startIndex") int start,
                                                   @PathVariable(name = "nhi") String nhi) {
        try (Connection connection = driver.getConnection()) {
            return handler.getAppointments(connection, nhi, UserType.USER, count, start);
        } catch (SQLException e) {
            Log.severe("Got bad response from DB. SQL error code: " + e.getErrorCode(), e);
            throw new ServerDBException(e);
        }
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/clinicians/{staffId}/appointments")
    public Collection<Appointment> getClinicianAppointments(@RequestParam(name = "count") int count,
                                                   @RequestParam(name = "startIndex") int start,
                                                   @PathVariable(name = "staffId") String staffId) {
        try (Connection connection = driver.getConnection()) {
            return handler.getAppointments(connection, staffId, UserType.CLINICIAN, count, start);
        } catch (SQLException e) {
            Log.severe("Got bad response from DB. SQL error code: " + e.getErrorCode(), e);
            throw new ServerDBException(e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/appointments")
    public ResponseEntity postAppointment(@RequestBody Appointment newAppointment) {
        try (Connection connection = driver.getConnection()) {
            AppointmentUpdateStrategy appointmentStrategy = handler.getAppointmentStrategy();
            appointmentStrategy.postSingleAppointment(connection, newAppointment);

            String appointmentId = Integer.toString(handler.getAppointmentId(connection, newAppointment));
            socketHandler.broadcast(EventTypes.APPOINTMENT_UPDATE, appointmentId, appointmentId);
            // TODO: still needs the client side broadcast implementation

        } catch (SQLException e) {
            Log.severe("Cannot add new appointment to database", e);
            throw new ServerDBException(e);
        } catch (IOException ex) {
            Log.warning("Failed to broadcast update after posting an appointment", ex);
        }

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/appointments")
    public ResponseEntity deleteAppointment(@RequestBody Appointment appointmentToDelete) {
        try (Connection connection = driver.getConnection()) {
            handler.deleteAppointment(appointmentToDelete, connection);

            String appointmentId = Integer.toString(appointmentToDelete.getAppointmentId());
            socketHandler.broadcast(EventTypes.APPOINTMENT_UPDATE, appointmentId, appointmentId);

        } catch (SQLException e) {
            Log.severe("Cannot delete appointment at db", e);
            throw new ServerDBException(e);
        } catch (IOException ex) {
            Log.warning("Failed to broadcast update after deleting an appointment", ex);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
