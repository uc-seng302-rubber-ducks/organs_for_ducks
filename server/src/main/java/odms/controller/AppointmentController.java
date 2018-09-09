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
import java.util.Collection;

@OdmsController
public class AppointmentController extends BaseController {

    private DBHandler handler;
    private JDBCDriver driver;
    private SocketHandler socketHandler;
    private static final String BAD_DB_RESPONSE = "Got bad response from DB. SQL error code: ";

    @Autowired
    public AppointmentController(DBManager manager, SocketHandler socketHandler) {
        super(manager);
        handler = super.getHandler();
        driver = super.getDriver();
        this.socketHandler = socketHandler;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{nhi}/appointments/exists")
    public boolean userAppointmentStatusExists(@PathVariable(name = "nhi") String nhi,
                                 @RequestParam(name = "status") int statusId) {
        try (Connection connection = driver.getConnection()) {
            return handler.checkAppointmentStatusExists(connection, nhi, statusId, UserType.USER);
        } catch (SQLException e) {
            Log.severe("", e);
            throw new ServerDBException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/clinicians/{staffId}/appointments/exists")
    public boolean clinicianAppointmentStatusExists(@PathVariable(name = "staffId") String staffId,
                                 @RequestParam(name = "status") int statusId) {
        try (Connection connection = driver.getConnection()) {
            return handler.checkAppointmentStatusExists(connection, staffId, statusId, UserType.CLINICIAN);
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
            Log.severe(BAD_DB_RESPONSE + e.getErrorCode(), e);
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
            Log.severe("Unable to get clinician requested appointments with staff id: "+staffId+". SQL error code: " + e.getErrorCode(), e);
            throw new ServerDBException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{nhi}/appointments/unseen")
    public Appointment getUnseenUserAppointments(@PathVariable(name = "nhi") String nhi) {
        try (Connection connection = driver.getConnection()) {
            return handler.getUnseenAppointment(connection, nhi);
        } catch (SQLException e) {
            Log.severe(BAD_DB_RESPONSE + e.getErrorCode(), e);
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

        } catch (SQLException e) {
            Log.severe("Cannot add new appointment to database", e);
            throw new ServerDBException(e);
        } catch (IOException ex) {
            Log.warning("Failed to broadcast update after posting an appointment", ex);
        }

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }


    @RequestMapping(method = RequestMethod.PATCH, value = "/appointments/{appointmentId}/status")
    public ResponseEntity patchAppointmentStatus(@RequestBody int statusId,
                                                 @PathVariable(name = "appointmentId") int appointmentId) {
        try (Connection connection = driver.getConnection()) {
            AppointmentUpdateStrategy appointmentUpdateStrategy = handler.getAppointmentStrategy();

            if (checkStatusUpdateAllowed(appointmentId, statusId)) {
                appointmentUpdateStrategy.patchAppointmentStatus(connection, statusId, appointmentId);

                deleteRejectedSeen(connection, appointmentUpdateStrategy, statusId, appointmentId);

                String idString = Integer.toString(appointmentId);
                socketHandler.broadcast(EventTypes.APPOINTMENT_UPDATE, idString, idString);
            } else {
                Log.warning("A user tried to update an appointment status that they are not allowed to.");
            }
            // TODO: still needs the client side broadcast implementation
        } catch (SQLException e) {
            Log.severe("Cannot patch appointment status to database", e);
            throw new ServerDBException(e);
        } catch (IOException ex) {
            Log.warning("Failed to broadcast update after patching an appointment", ex);
        }
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    /**
     * If the appointment status is being changed to rejected seen, this function deletes that appointment from the database
     * @param statusId Id of the status the appointment is being changed to. The function will do nothing if this is not 7
     * @param appointmentId Id of the appointment to delete id the status is correct
     */
    private void deleteRejectedSeen(Connection connection, AppointmentUpdateStrategy appointmentUpdateStrategy, int statusId, int appointmentId) {
        if (statusId == AppointmentStatus.REJECTED_SEEN.getDbValue()) {
            try {
                appointmentUpdateStrategy.deleteRejectedSeenStatus(connection, appointmentId);
            } catch (SQLException e) {
                Log.severe("Could not delete an appointment after it was set to rejected seen", e);
                throw new ServerDBException(e);
            }
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/users/{nhi}/appointments/cancelled")
    public ResponseEntity deleteUsersCancelledAppointments(@PathVariable(name = "nhi") String nhi) throws SQLException {
        try (Connection connection = driver.getConnection()) {
            AppointmentUpdateStrategy updateStrategy = handler.getAppointmentStrategy();
            updateStrategy.deleteCancelledAppointments(connection, nhi, UserType.USER);

            socketHandler.broadcast(EventTypes.APPOINTMENT_UPDATE, "", "");
        } catch (SQLException ex) {
            Log.severe("Cannot delete user " + nhi + "'s cancelled appointments in db", ex);
            throw new ServerDBException(ex);
        } catch (IOException ex) {
            Log.warning("Failed to broadcast update after deleting multiple appointments", ex);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/clinicians/{staffId}/appointments/cancelled")
    public ResponseEntity deleteCliniciansCancelledAppointments(@PathVariable(name = "staffId") String staffId) throws SQLException {
        try (Connection connection = driver.getConnection()) {
            AppointmentUpdateStrategy updateStrategy = handler.getAppointmentStrategy();
            updateStrategy.deleteCancelledAppointments(connection, staffId, UserType.CLINICIAN);

            socketHandler.broadcast(EventTypes.APPOINTMENT_UPDATE, "", "");
        } catch (SQLException ex) {
            Log.severe("Cannot delete clinician " + staffId + "'s cancelled appointments in db", ex);
            throw new ServerDBException(ex);
        } catch (IOException ex) {
            Log.warning("Failed to broadcast update after deleting multiple appointments", ex);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/appointments")
    public ResponseEntity deleteAppointment(@RequestBody Appointment appointmentToDelete) {
        try (Connection connection = driver.getConnection()) {
            handler.deleteAppointment(appointmentToDelete, connection);

            String appointmentId = Integer.toString(appointmentToDelete.getAppointmentId());
            socketHandler.broadcast(EventTypes.APPOINTMENT_UPDATE, appointmentId, appointmentId);

        } catch (SQLException e) {
            Log.severe("Cannot delete appointment in db", e);
            throw new ServerDBException(e);
        } catch (IOException ex) {
            Log.warning("Failed to broadcast update after deleting an appointment", ex);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Checks the appointment previous status to confirm that the status update is valid.
     *
     * @param apptId Id of the appointment to check
     * @return true if the status update is valid, false otherwise
     */
    public boolean checkStatusUpdateAllowed(int apptId, int statusId) {
        if (statusId == AppointmentStatus.CANCELLED_BY_USER.getDbValue() || statusId == AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue()) {
            return true;
        } else {
            int acceptedId = 2;
            int acceptedSeenId = 7;
            int rejectedId = 3;
            int rejectedSeenId = 8;
            int cancelledByUserId = AppointmentStatus.CANCELLED_BY_USER.getDbValue();
            int cancelledByUserSeenId = AppointmentStatus.CANCELLED_BY_USER_SEEN.getDbValue();
            int cancelledByClinicianId = AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue();
            int cancelledByClinicianSeenId = AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN.getDbValue();
            Integer currentStatus = null;
            try (Connection connection = driver.getConnection()) {
                currentStatus = handler.getAppointmentStatus(connection, apptId);
            } catch (SQLException e) {
                Log.severe("Cannot update appointment status at db", e);
                throw new ServerDBException(e);
            }
            // This logic statement ensures that the user can only edit the status if it is going from X to X_SEEN e.g. ACCEPTED to ACCEPTED_SEEN
            return ((statusId == acceptedSeenId && currentStatus == acceptedId) ||
                    (statusId == rejectedSeenId && currentStatus == rejectedId) ||
                    (statusId == cancelledByUserSeenId && currentStatus == cancelledByUserId) ||
                    (statusId == cancelledByClinicianSeenId && currentStatus == cancelledByClinicianId));
        }
    }
}
