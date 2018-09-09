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

    @RequestMapping(method = RequestMethod.GET, value = "/appointments")
    public Collection<Appointment> getAppointments(@RequestParam(name = "count") int count) {
        try (Connection connection = driver.getConnection()) {
            return null; //TODO: Replace this when DB stuff is done. - E
        } catch (SQLException e) {
            Log.severe("Got bad response from DB. SQL error code: " + e.getErrorCode(), e);
            throw new ServerDBException(e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/appointment")
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
    @RequestMapping(method = RequestMethod.GET, value = "/appointments/clinicians/{staffId}")
    public Collection<Appointment> getClinicianAppointments(@RequestParam(name = "startIndex") int startIndex,
                                                            @RequestParam(name = "count") int count,
                                                            @PathVariable(value = "staffId") String staffId) {
        try (Connection connection = driver.getConnection()) {
            return handler.getAppointments(connection, staffId, UserType.CLINICIAN, count, startIndex);
        } catch (SQLException e) {
            Log.severe("Unable to get clinician requested appointments with staff id: "+staffId+". SQL error code: " + e.getErrorCode(), e);
            throw new ServerDBException(e);
        }
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/clinicians/{staffId}/appointments/{appointmentId}")
    public ResponseEntity putAppointment(@PathVariable(value = "staffId") String staffId,
                                         @PathVariable(value = "appointmentId") Integer appointmentId,
                                         @RequestBody Appointment appointment) {
        try (Connection connection = driver.getConnection()) {
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

    @RequestMapping(method = RequestMethod.DELETE, value = "/appointment")
    public ResponseEntity deleteAppointment(@RequestBody Appointment appointmentToDelete) {
        try (Connection connection = driver.getConnection()) {
            handler.deleteAppointment(appointmentToDelete, connection);

            String appointmentId = Integer.toString(handler.getAppointmentId(connection, appointmentToDelete));
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
    private boolean validateRequestedAppointmentTime(String staffId, LocalDateTime requestedDateTime) throws SQLException {
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
