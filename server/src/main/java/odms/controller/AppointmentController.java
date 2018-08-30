package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.database.db_strategies.AppointmentUpdateStrategy;
import odms.commons.model.Appointment;
import odms.commons.model._enum.EventTypes;
import odms.commons.model._enum.UserType;
import odms.commons.utils.Log;
import odms.exception.ServerDBException;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(method = RequestMethod.GET, value = "/appointments")
    public Collection<Appointment> getAppointments(@RequestParam(name = "startIndex") int startIndex,
                                                   @RequestParam(name = "count") int count,
                                                   @RequestParam(name = "user") String user,
                                                   @RequestParam(name = "userType") int userType) {
        try (Connection connection = driver.getConnection()) {
            UserType type = null;
            for (UserType ut : UserType.values()) {
                if (ut.getValue() == userType) type = ut;
            }

            Log.info("Getting all appointments for user " + user);
            return handler.getAppointments(connection, user, type, count, startIndex);
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

}
