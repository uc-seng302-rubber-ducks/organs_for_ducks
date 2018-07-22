package odms.commons.utils.db_strategies;

import odms.commons.model.Clinician;
import odms.commons.utils.Log;

import java.sql.*;
import java.util.Collection;

public class ClinicianUpdateStrategy extends AbstractUpdateStrategy {

    private static final String CREATE_CLINICIAN_STMT = "INSERT INTO Clinician (staffId, firstName, middleName, lastName) VALUES (?, ?, ?, ?)";
    private static final String CREATE_STAFF_CONTACT_STMT = "INSERT INTO ContactDetails (fkStaffId, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADDRESS_STMT = "INSERT INTO Address (fkContactId, streetNumber, streetName, neighbourhood, city, region, country) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_CLINICIAN_STMT = "UPDATE Clinician SET firstName = ?, middleName = ?, lastName = ?, lastModified = ? WHERE staffId = ?";
    private static final String UPDATE_CLINICIAN_ADDRESS = "UPDATE ContactDetails JOIN Address ON contactId = fkContactId " +
            "SET streetNumber = ?, streetName = ?, neighbourhood = ?, city = ?, region = ?, zipCode = ?, country = ? " +
            "WHERE ContactDetails.fkStaffId = ?";
    private static final String UPDATE_CLINICIAN_PSSWRD = "UPDATE PasswordDetails SET hash = ?, salt = ? WHERE fkStaffId = ?";

    private static final String DELETE_CLINICIAN_STMT = "DELETE FROM Clinician WHERE staffId = ?";

    @Override
    public <T> void update(Collection<T> roles, Connection connection) throws SQLException {
        Collection<Clinician> clinicians = (Collection<Clinician>) roles;
        for (Clinician clinician : clinicians) {
            if (clinician.getChanges().size() <= 0) {
                continue;
            }
            PreparedStatement stmt = connection.prepareStatement("SELECT staffId FROM Clinician WHERE staffId = ?");
            stmt.setString(1, clinician.getStaffId());
            ResultSet queryResults = stmt.executeQuery();
            if (!queryResults.next() && !clinician.isDeleted()) {
                executeCreation(clinician, connection);
            } else if (clinician.isDeleted()) {
                deleteRole(clinician, connection);
            } else {
                executeUpdate(clinician, connection);
            }
        }
    }

    /**
     * Executes an update for each of items in the collection.
     * Precondition: The connection is not null and valid
     * Post-conditions: An entry that represents the object is created and stored in the database.
     *
     * @param clinician  clinician to update the database with
     * @param connection Connection to the target database
     */
    private void executeCreation(Clinician clinician, Connection connection) {
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                createClinician(clinician, connection);
                createClinicianPassword(clinician, connection);
                createClinicianContact(clinician, connection);
            } catch (SQLException sqlEx) {
                connection.prepareStatement("ROLLBACK").execute();
                System.out.println("An error occurred"); //TODO: Make this a popup
            }
            connection.prepareStatement("COMMIT");
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }


    /**
     * Creates a clinician entry in the tables
     * Preconditions: Must have an active connection to the database
     * Postconditions: The given clinician is created in the database
     *
     * @param clinician  The clinician object to create
     * @param connection Connection to the target database
     * @throws SQLException If there isn't an active connection to the database or there is an error in creating the clinician
     */
    private void createClinician(Clinician clinician, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_CLINICIAN_STMT)) {
            statement.setString(1, clinician.getStaffId());
            statement.setString(2, clinician.getFirstName());
            statement.setString(3, clinician.getMiddleName());
            statement.setString(4, clinician.getLastName());

            statement.executeUpdate();
        }
    }

    /**
     * Saves the hashed password to the PasswordDetails table in the database
     * Precondition: The connection is not null and valid
     * Post-condition: The hashed password and salt is stored in the database.
     *
     * @param clinician Clinician whose password will be stored
     * @param connection Connection to the target database
     * @throws SQLException If there is an error in storing it into the database or the connection is invalid
     */
    private void createClinicianPassword(Clinician clinician, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO PasswordDetails (fkStaffId, hash, salt) VALUES (?, ?, ?)")) {
            statement.setString(1, clinician.getStaffId());
            statement.setString(2, clinician.getPassword());
            statement.setString(3, clinician.getSalt());
            statement.executeUpdate();
        }
    }


    /**
     * Saves the contact details and work address of the given clinician in the database
     * Clinicians do not have a home phone, cell phone or email, but do have a work address
     * Precondition: The connection is valid
     * Post-condition: The work address of the clinician and its associated ContactDetails entry will be saved to the database
     *
     * @param clinician Clinician associated with the work address to be stored
     * @param connection Connection to the target database
     * @throws SQLException If there is an error in storing the details into the database or the connection is invalid
     */
    private void createClinicianContact(Clinician clinician, Connection connection) throws SQLException {
        try (PreparedStatement cdStatement = connection.prepareStatement(CREATE_STAFF_CONTACT_STMT)) {
            cdStatement.setString(1, clinician.getStaffId());
            cdStatement.setString(2, "");
            cdStatement.setString(3, "");
            cdStatement.setString(4, "");
            cdStatement.executeUpdate();

            int contactId = getContactID(clinician, connection);

            try (PreparedStatement addressStmt = connection.prepareStatement(CREATE_ADDRESS_STMT)) {

                addressStmt.setInt(1, contactId);
                addressStmt.setString(2, clinician.getStreetNumber());
                addressStmt.setString(3, clinician.getStreetName());
                addressStmt.setString(4, clinician.getNeighborhood());
                addressStmt.setString(5, clinician.getCity());
                addressStmt.setString(6, clinician.getRegion());
                addressStmt.setString(7, clinician.getCountry());

                addressStmt.executeUpdate();
            }
        }
    }

    /**
     * Retrieves the contact ID for the given clinician
     *
     * @param clinician Clinician associated with the entry to be found
     * @param connection Connection to the target database
     * @return The contact ID of the clinicians ContactDetails entry, otherwise -1 if it does not exist
     * @throws SQLException If there is an error in retrieving the contact id
     */
    private int getContactID(Clinician clinician, Connection connection) throws SQLException {
        int contactId = -1;

        try (PreparedStatement getContactId = connection.prepareStatement("SELECT contactId FROM ContactDetails WHERE fkStaffID = ?")) {
            getContactId.setString(1, clinician.getStaffId());

            try (ResultSet results = getContactId.executeQuery()) {
                if (results != null && results.next()) {
                    contactId = results.getInt(1);
                }
            }
        }

        return contactId;
    }


    /**
     * Deletes a role entry from the database that is associated with the given object
     * Preconditions: The connection is not null and valid
     * Postcondition: The entry associated with the object will be deleted.
     *
     * @param clinician  The clinician associated with the entry to be deleted
     * @param connection Connection to the target database
     */
    private void deleteRole(Clinician clinician, Connection connection) {
        String identifier;
        String sql;
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                identifier = clinician.getStaffId();
                sql = DELETE_CLINICIAN_STMT;
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, identifier);

                stmt.executeUpdate();
            } catch (SQLException sqlEx) {
                Log.severe("A fatal error in deletion, cancelling operation", sqlEx);
                connection.prepareStatement("ROLLBACK").execute();
                System.out.println("An error occurred"); //TODO: Make this a popup
            }

            connection.prepareStatement("COMMIT");
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }

    /**
     * Executes an update for an entry associated with the provided object.
     * Pre-condition: The connection is not null and valid
     * Post-condition: The entry in the database reflects the entry
     *
     * @param clinician  The clinician associated with the entry in the database
     * @param connection Connection ot the target database
     */
    private void executeUpdate(Clinician clinician, Connection connection) {

        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                updateClinicianDetails(clinician, connection);
                updateClinicianAddress(clinician, connection);
                updateClinicianPassword(clinician, connection);
            } catch (SQLException sqlEx) {
                Log.severe("A fatal error in updating, cancelling operation", sqlEx);
                connection.prepareStatement("ROLLBACK").execute();
            }

            connection.prepareStatement("COMMIT");
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }

    }

    /**
     * Updates the clinicians hashed and salted passwords within the database
     * Preconditions: Must have an active connection to the database
     * Post-conditions: The clinicians hashed and salted password is updated within the database
     *
     * @param clinician Clinician object with an updated password
     * @param connection Connection to the target database
     * @throws SQLException If there is an error updating the password, or the database connection is invalid
     */
    private void updateClinicianPassword(Clinician clinician, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CLINICIAN_PSSWRD)) {
            String password = clinician.getPassword();
            String salt = clinician.getSalt();
            statement.setString(1, password);
            statement.setString(2, salt);
            statement.setString(3, clinician.getStaffId());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the given clinicians address in the database using UPDATE_ADDRESS
     * Precondition: Must have an active connection to the database
     * Post-condition: The address of the given clinician is updated in the database
     *
     * @param clinician  Clinician object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the clinicians address details
     */
    private void updateClinicianAddress(Clinician clinician, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CLINICIAN_ADDRESS)) {

            statement.setString(1, clinician.getStreetNumber());
            statement.setString(2, clinician.getStreetName());
            statement.setString(3, clinician.getNeighborhood());
            statement.setString(4, clinician.getCity());
            statement.setString(5, clinician.getRegion());
            statement.setString(6, clinician.getZipCode());
            statement.setString(7, clinician.getCountry());
            statement.setString(8, clinician.getStaffId());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the given clinician in the database using UPDATE_CLINICIAN_STMT
     * Precondition: Must have an active connection to the database
     * Postcondition: The given clinician is updated in the database
     *
     * @param clinician  Clinician object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the clinician details
     */
    private void updateClinicianDetails(Clinician clinician, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CLINICIAN_STMT)) {

            statement.setString(1, clinician.getFirstName());
            statement.setString(2, clinician.getMiddleName());
            statement.setString(3, clinician.getLastName());
            statement.setTimestamp(4, Timestamp.valueOf(clinician.getDateLastModified()));
            statement.setString(5, clinician.getStaffId());

            statement.executeUpdate();
        }
    }
}
