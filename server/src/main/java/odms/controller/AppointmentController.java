package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.socket.SocketHandler;
import odms.utils.DBManager;

@OdmsController
public class AppointmentController extends BaseController {

    private DBHandler handler;
    private JDBCDriver driver;
    private SocketHandler socketHandler;

    public AppointmentController(DBManager manager, SocketHandler socketHandler) {
        super(manager);
        handler = super.getHandler();
        driver = super.getDriver();
        this.socketHandler = socketHandler;

    }


    // TODO: waiting for an Appointment class
//    @RequestMapping(method = RequestMethod.POST, value = "")
//    public ResponseEntity postAppointment(@RequestBody Appointment newAppointment) {
//        try (Connection connection = driver.getConnection()) {
//            handler.postAppointment();
//
//        } catch (SQLException e) {
//            Log.severe("Cannot add new appointmetn to db", e);
//            throw new ServerDBException(e);
//        }
//
//        return new ResponseEntity(HttpStatus.ACCEPTED);
//    }

}
