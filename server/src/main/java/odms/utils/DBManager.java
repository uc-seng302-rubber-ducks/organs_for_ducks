package odms.utils;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyVetoException;

/**
 * This is a simple container for DBHandler to allow dependency injection through spring. this may be helpful:
 * https://stormpath.com/blog/spring-boot-dependency-injection
 */
@Component
public class DBManager {

    private DBHandler handler;
    private JDBCDriver driver;

    @Autowired
    public DBManager() {
        this.handler = new DBHandler();
        try {
            this.driver = new JDBCDriver();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public DBHandler getHandler() {
        return handler;
    }

    public JDBCDriver getDriver() {
        return driver;
    }
}
