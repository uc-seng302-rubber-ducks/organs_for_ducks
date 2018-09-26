package odms.controller;

import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.exception.ServerDBException;
import odms.security.IsAdmin;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;

@OdmsController
public class SQLFromAdminController extends BaseController {

    private final JDBCDriver driver;
    private final DBHandler handler;

    @Autowired
    public SQLFromAdminController(DBManager manager) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
    }

    @IsAdmin
    @RequestMapping(method= RequestMethod.POST, value = "/sql")
    public List<String> runSql(@RequestBody String query) throws SQLFeatureNotSupportedException {
        List<String> results;
        if(!query.toUpperCase().startsWith("SELECT") || query.toUpperCase().contains("SLEEP(")){
            throw new SQLFeatureNotSupportedException();
        }
        try(Connection connection = driver.getConnection()){
            results = handler.runSqlQuery(query, connection);
            return results;
        } catch (SQLException e) {
            throw new ServerDBException(e);
        }
    }

    @ExceptionHandler({SQLFeatureNotSupportedException.class})
    private void handleIllegalSQL(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "SELECT is the only value allowed at this endpoint. SLEEP is also disabled.");
    }
}
