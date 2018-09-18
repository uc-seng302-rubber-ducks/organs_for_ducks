package odms.database;

import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtils {

    private static final String SELECT_IF_USER_EXISTS_BOOL = "SELECT EXISTS(SELECT 1 FROM User WHERE nhi = ?)";
    private static final String SELECT_IF_CLINICIAN_EXISTS_BOOL = "SELECT EXISTS(SELECT 1 FROM Clinician WHERE staffId = ?)";
    private static final String SELECT_IF_ADMIN_EXISTS_BOOL = "SELECT EXISTS(SELECT 1 FROM Administrator WHERE userName = ?)";

    private DBUtils() {

    }

    /**
     * checks whether a given publicly known identifier can be found on the database
     *
     * @param conn       connection to the database to be queried
     * @param type       type of end-user to search for
     * @param identifier nhi, staff id, or username to search for
     * @return true if the identifier can be found, false otherwise
     * @throws SQLException if any errors occur during the operation
     */
    public static boolean getExists(Connection conn, Type type, String identifier) throws SQLException {
        String query = null;
        if (type.equals(Administrator.class)) {
            query = SELECT_IF_ADMIN_EXISTS_BOOL;
        } else if (type.equals(Clinician.class)) {
            query = SELECT_IF_CLINICIAN_EXISTS_BOOL;
        } else if (type.equals(User.class)) {
            query = SELECT_IF_USER_EXISTS_BOOL;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, identifier);

            try (ResultSet result = stmt.executeQuery()) {
                result.next();
                return result.getInt(1) == 1;
            }
        }
    }

    /**
     * checks whether a given unique identifier (used on db only) exists on the database
     *
     * @param conn connection to the database to be queried
     * @param type type of end-user to search for
     * @param id   uniqueId field to search for
     * @return true if the id can be found, false otherwise
     * @throws SQLException if any errors occur during the operation
     */
    public static boolean getPrivateKeyExists(Connection conn, Type type, int id) throws SQLException {
        if (!type.equals(User.class)) {
            return false;
        }

        try (PreparedStatement stmt = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM User WHERE uniqueId = ?)")) {
            stmt.setInt(1, id);

            try (ResultSet result = stmt.executeQuery()) {
                result.next();
                return result.getInt(1) == 1;
            }
        }
    }
}
