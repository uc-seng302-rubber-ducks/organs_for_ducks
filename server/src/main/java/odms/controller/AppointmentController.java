package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.database.db_strategies.AppointmentUpdateStrategy;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentStatus;
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/clinicians/{staffId}/appointments/pending")
    public int getPendingAppointments(@PathVariable String staffId) {
        try (Connection connection = driver.getConnection()) {
            return handler.getPendingAppointmentsCount(connection, staffId);
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

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/clinicians/{staffId}/appointments/{appointmentId}")
    public ResponseEntity putAppointment(@PathVariable(value = "staffId") String staffId,
                                         @PathVariable(value = "appointmentId") Integer appointmentId,
                                         @RequestBody Appointment appointment) {
        try (Connection connection = driver.getConnection()) {
            if (!validateRequestedAppointmentTime(appointment.getRequestedClinicianId(), appointment.getRequestedDate()) && !appointment.getAppointmentStatus().equals(AppointmentStatus.REJECTED) && !appointment.getAppointmentStatus().equals(AppointmentStatus.REJECTED_SEEN)) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            AppointmentUpdateStrategy appointmentStrategy = handler.getAppointmentStrategy();
            appointmentStrategy.putSingleAppointment(connection, appointment);

            socketHandler.broadcast(EventTypes.APPOINTMENT_UPDATE, Integer.toString(appointmentId), Integer.toString(appointmentId));

        } catch (SQLException s) {
            Log.severe("Cannot send updated appointment to database", s);
            throw new ServerDBException(s);
        } catch (IOException i) {
            Log.warning("Failed to broadcast update after putting an appointment", i);
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

    /**
     * Validates the Requested Appointment Booking date and time based on the following rules:
     * a) date and time must be in future
     * b) the values of minutes and seconds must be 0, only date and hours are permitted.
     * c) date and time must be between 8am-5pm
     * d) date and time must not clash with accepted appointment bookings date and time.
     *
     * @param staffId of clinician
     * @param requestedDateTime of requested appointment booking
     * @return true if validation passes based on rules stated above, false otherwise.
     * @throws SQLException if there are any database errors.
     */
    public boolean validateRequestedAppointmentTime(String staffId, LocalDateTime requestedDateTime) throws SQLException {
        int startHour = 8;
        int endHour = 17; //5pm
        int requestedTime = requestedDateTime.getHour();
        List<LocalDateTime> bookedAppointmentTimes;

        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            return false;
        }

        if (requestedDateTime.getMinute() != 0 || requestedDateTime.getSecond() != 0) {
            return false;
        }

        if (requestedTime < startHour || requestedTime > endHour) {
            return false;
        }

        try (Connection connection = driver.getConnection()) {
            bookedAppointmentTimes = handler.getBookedAppointmentTimes(connection, staffId);
            for (LocalDateTime bookedAppointmentTime : bookedAppointmentTimes) {
                if(bookedAppointmentTime.isEqual(requestedDateTime)) {
                    return false;
                }
            }
        }

        return true;
    }
}
