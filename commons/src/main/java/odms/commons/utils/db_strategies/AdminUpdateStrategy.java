package odms.commons.utils.db_strategies;

import odms.commons.model.Administrator;
import odms.commons.utils.Log;

import java.sql.*;
import java.util.Collection;

public class AdminUpdateStrategy extends AbstractUpdateStrategy {

    private static final String CREATE_ADMIN_STMT = "INSERT INTO Administrator (username, firstName, middleName, lastName) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_ADMIN_STMT = "UPDATE Administrator SET firstName = ?, middleName = ?, lastName = ?, lastModified = ? WHERE username = ?";
    private static final String UPDATE_ADMIN_PSSWRD = "UPDATE PasswordDetails SET hash = ?, salt = ? WHERE fkAdminUserName = ?";

    private static final String DELETE_ADMIN_STMT = "DELETE FROM Administrator WHERE username = ?";

    @Override
    public <T> void update(Collection<T> roles, Connection connection) throws SQLException {
        Collection<Administrator> admins = (Collection<Administrator>) roles;
        for (Administrator admin : admins) {
            if (admin.getChanges().isEmpty()) {
                continue;
            }
            try (PreparedStatement stmt = connection.prepareStatement("SELECT username FROM Administrator WHERE username = ?")) {
                stmt.setString(1, admin.getUserName());
                try (ResultSet queryResults = stmt.executeQuery()) {
                    if (!queryResults.next() && !admin.isDeleted()) {
                        executeCreation(admin, connection);
                    } else if (admin.isDeleted()) {
                        deleteRole(admin, connection);
                    } else {
                        executeUpdate(admin, connection);
                    }
                }
            }
        }
    }

    /**
     * Executes an update for an entry associated with the provided object.
     * Pre-condition: The connection is not null and valid
     * Post-condition: The entry in the database reflects the entry
     *
     * @param admin      The admin associated with the entry in the database
     * @param connection Connection ot the target database
     */
    private void executeUpdate(Administrator admin, Connection connection) throws SQLException {
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                updateAdminDetails(admin, connection);
                updateAdminPassword(admin, connection);
            } catch (SQLException sqlEx) {
                connection.prepareStatement("ROLLBACK").execute();
            }
            connection.prepareStatement("COMMIT");
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            throw sqlEx;
        }

    }

    /**
     * Executes an update for each of items in the collection.
     * Precondition: The connection is not null and valid
     * Post-conditions: An entry that represents the object is created and stored in the database.
     *
     * @param admin      admin object to update the database with
     * @param connection Connection to the target database
     */
    private void executeCreation(Administrator admin, Connection connection) {
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                createAdmin(admin, connection);
                createPassword(admin, connection);
            } catch (SQLException sqlEx) {
                connection.prepareStatement("ROLLBACK").execute();
                throw sqlEx;
            }
            connection.prepareStatement("COMMIT");
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }

    /**
     * Saves the hashed password to the PasswordDetails table in the database
     * Precondition: The connection is not null and valid
     * Post-condition: The hashed password and salt is stored in the database.
     *
     * @param admin      Administrator to store the password of
     * @param connection Connection to the target database
     * @throws SQLException If there is an error in storing it into the database or the connection is invalid
     */
    private void createPassword(Administrator admin, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO PasswordDetails (fkAdminUserName, hash, salt) VALUES (?, ?, ?)")) {
            statement.setString(1, admin.getUserName());

            Blob hashBlob = connection.createBlob();
            hashBlob.setBytes(1, admin.getPassword());

            Blob saltBlob = connection.createBlob();
            saltBlob.setBytes(1, admin.getSalt());

            statement.setBlob(2, hashBlob);
            statement.setBlob(3, saltBlob);

            statement.executeUpdate();
        }
    }

    /**
     * Creates an admin entry in the database tables using CREATE_ADMIN_STMT
     * Preconditions: Must have an active connection to the database
     * Post-conditions: The given admin is created in the database
     *
     * @param admin      administrator object to create.
     * @param connection Connection to the target database
     * @throws SQLException If there isn't an active connection to the database or there is an error creating the administrator
     */
    private void createAdmin(Administrator admin, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_ADMIN_STMT)) {

            statement.setString(1, admin.getUserName());
            statement.setString(2, admin.getFirstName());
            statement.setString(3, admin.getMiddleName());
            statement.setString(4, admin.getLastName());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the given administrator in the database
     * Precondition: Must have an active connection to the database
     * Post-condition: The given admin is updated in the database
     *
     * @param admin      Administrator object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the admin details
     */
    private void updateAdminDetails(Administrator admin, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_ADMIN_STMT)) {

            statement.setString(1, admin.getUserName());
            statement.setString(2, admin.getFirstName());
            statement.setString(3, admin.getMiddleName());
            statement.setString(4, admin.getLastName());
            statement.setTimestamp(5, Timestamp.valueOf(admin.getDateLastModified()));
            statement.setString(6, admin.getUserName());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the admins hashed and salted passwords within the database
     * Preconditions: Must have an active connection to the database
     * Post-conditions: The admins hashed and salted password is updated within the database
     *
     * @param admin Administrator object with an updated password
     * @param connection Connection to the target database
     * @throws SQLException If there is an error updating the password, or the database connection is invalid
     */
    private void updateAdminPassword(Administrator admin, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_ADMIN_PSSWRD)) {

            Blob passwordBlob = connection.createBlob();
            passwordBlob.setBytes(1, admin.getPassword());

            Blob saltBlob = connection.createBlob();
            saltBlob.setBytes(1, admin.getSalt());

            statement.setBlob(1, passwordBlob);
            statement.setBlob(2, saltBlob);
            statement.setString(3, admin.getUserName());

            statement.executeUpdate();
        }
    }

    /**
     * Deletes a role entry from the database that is associated with the given object
     * Preconditions: The connection is not null and valid
     * Postcondition: The entry associated with the object will be deleted.
     *
     * @param admin      The admin associated with the entry to be deleted
     * @param connection Connection to the target database
     */
    private void deleteRole(Administrator admin, Connection connection) {
        String identifier;
        String sql;
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                identifier = admin.getUserName();
                sql = DELETE_ADMIN_STMT;
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, identifier);

                stmt.executeUpdate();
            } catch (SQLException sqlEx) {
                Log.severe("A fatal error in deletion, cancelling operation", sqlEx);
                connection.prepareStatement("ROLLBACK").execute();
                System.out.println("An error occured"); //TODO: Make this a popup
            }

            connection.prepareStatement("COMMIT");
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }
}
