package odms.utils;

import odms.commons.utils.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This is a simple container for DBHandler to allow dependency injection through spring. this may be helpful:
 * https://stormpath.com/blog/spring-boot-dependency-injection
 */
@Component
public class DBManager {

    private DBHandler handler;

    @Autowired
    public DBManager() {
        this.handler = new DBHandler();
    }

    public DBHandler getHandler() {
        return handler;
    }
}
