package odms.controller;

import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.utils.DBManager;
import org.springframework.web.bind.annotation.RestController;

/**
 * base for following rest controllers to inherit from
 */
@RestController
public abstract class BaseController {

    private JDBCDriver driver;
    private DBHandler handler;


    public BaseController(DBManager manager) {
        this.handler = manager.getHandler();
        this.driver = manager.getDriver();
    }

    public DBHandler getHandler() {
        return handler;
    }

    public JDBCDriver getDriver() {
        return driver;
    }
}