package odms.controller;

import odms.commons.model.Administrator;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.exception.ServerDBException;
import odms.security.IsAdmin;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@OdmsController
public class SQLFromAdminController extends BaseController {

    private final JDBCDriver driver;
    private final DBHandler handler;

    @Autowired
    public SQLFromAdminController(DBManager manager) throws SQLException {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
        if (!handler.getExists(driver.getConnection(), Administrator.class, "default")) {
            Administrator administrator = new Administrator("default", "default", "", "", "admin");
            handler.saveAdministrator(administrator, driver.getConnection());
        }
    }

    @IsAdmin
    @RequestMapping(method= RequestMethod.POST, value = "/sql")
    public List runSql(@RequestBody String query){
        List<String> results = new ArrayList<>();
        try(Connection connection = driver.getConnection()){
            return handler.runSqlQuery(query, connection);
        } catch (SQLException e) {
            throw new ServerDBException(e);
        }
    }
}
