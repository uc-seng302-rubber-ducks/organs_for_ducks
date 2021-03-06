package odms.database.db_strategies;

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
     * @param <T>        Type of the collection being updated
     * @throws SQLException if the update cannot be performed
     */
    public abstract <T> void update(Collection<T> roles, Connection connection) throws SQLException;
}
