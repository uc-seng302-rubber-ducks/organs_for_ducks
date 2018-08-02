package odms.controller;

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
    }

    @IsAdmin
    @RequestMapping(method= RequestMethod.POST, value = "/sql")
    public List<String> runSql(@RequestBody String query){
        List<String> results;
        try(Connection connection = driver.getConnection()){
            results = handler.runSqlQuery(query, connection);
            return results;
        } catch (SQLException e) {
            throw new ServerDBException(e);
        }
    }
}
