package odms.commons.utils.dbStrategies;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Strategy to properly encapsulate how each different role is meant to be updated differently
 */
public abstract class AbstractUpdateStrategy {
    /**
     * Method to update the provided collection of objects
     *
     * @param roles      Collection of roles to update
     * @param connection Connection to the target database
     */
    public abstract <T> void update(Collection<T> roles, Connection connection) throws SQLException;
}
