package odms.controller;

import odms.commons.model._enum.EventTypes;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.controller.user.details.ModifyingController;
import odms.exception.ServerDBException;
import odms.security.IsAdmin;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

@OdmsController
public class CountriesController extends ModifyingController {

    JDBCDriver driver;
    DBHandler handler;

    public CountriesController(DBManager manager) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
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
            super.broadcast(EventTypes.COUNTRY_UPDATE,"","");
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (SQLException e) {
            Log.severe("Could not update countries", e);
            throw new ServerDBException(e);
        }
    }

}
