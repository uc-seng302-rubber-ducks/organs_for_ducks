package odms.controller;

import odms.commons.model._enum.EventTypes;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.exception.ServerDBException;
import odms.security.IsAdmin;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

@OdmsController
public class CountriesController extends BaseController {

    JDBCDriver driver;
    DBHandler handler;
    SocketHandler socketHandler;

    public CountriesController(DBManager manager, SocketHandler socketHandler) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
        this.socketHandler = socketHandler;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/countries")
    public Set getAllowedCountries(){
        try (Connection connection =  driver.getConnection()){
            return handler.getAllowedCountries(connection);
        } catch (SQLException e) {
            Log.severe("Could not get countries", e);
            throw new ServerDBException(e);
        }
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.PUT, value = "/countries")
    public ResponseEntity putCountries(@RequestBody Set<String> countries){
        try(Connection connection = driver.getConnection()) {
            handler.putAllowedCountries(connection, countries);
            socketHandler.broadcast(EventTypes.COUNTRY_UPDATE,"","");

        } catch (SQLException e) {
            Log.severe("Could not update countries", e);
            throw new ServerDBException(e);
        } catch (IOException ex) {
            Log.warning("Failed to broadcast update after putting countries", ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
