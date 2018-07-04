package odms.commons.utils.dbStrategies;

import odms.commons.model.Clinician;
import odms.commons.utils.Log;

import java.sql.*;
import java.util.Collection;

public class ClinicianUpdateStrategy extends AbstractUpdateStrategy {

    private static final String CREATE_CLINICIAN_STMT = "INSERT INTO Clinician (staffId, firstName, middleName, lastName) VALUES (?, ?, ?, ?)";
    private static final String CREATE_STAFF_CONTACT_STMT = "INSERT INTO ContactDetails (fkStaffId, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADDRESS_STMT = "INSERT INTO Address (fkContactId, streetNumber, streetName, neighbourhood, city, region, country) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_CLINICIAN_STMT = "UPDATE Clinician SET staffId = ?, firstName = ?, middleName = ?, lastName = ?, lastModified = ? WHERE staffId = ?";
    private static final String UPDATE_CLINICIAN_ADDRESS = "UPDATE ContactDetails JOIN Address ON contactId = fkContactId " +
            "SET streetNumber = ?, streetName = ?, neighbourhood = ?, city = ?, region = ?, zipCode = ?, country = ? " +
            "WHERE ContactDetails.fkStaffId = ?";
    private static final String UPDATE_CLINICIAN_PASSWORD = "UPDATE PasswordDetails SET hash = ?, salt = ? WHERE fkStaffId = ?";

    private static final String DELETE_CLINICIAN_STMT = "DELETE FROM Clinician WHERE staffId = ?";

    @Override
    public <T> void update(Collection<T> roles, Connection connection) throws SQLException {
        Collection<Clinician> clinicians = (Collection<Clinician>) roles;
        for (Clinician clinician : clinicians) {
            if (clinician.getChanges().size() <= 0) {
                continue;
            }
            PreparedStatement stmt = connection.prepareStatement("SELECT nhi FROM clinician WHERE nhi = ?");
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
                // TODO: Create the clinician contact stuff once the abstractions are completed 25/6 - Eiran
            } catch (SQLException sqlEx) {
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
        PreparedStatement statement = connection.prepareStatement(CREATE_CLINICIAN_STMT);
        statement.setString(1, clinician.getStaffId());
        statement.setString(2, clinician.getFirstName());
        statement.setString(3, clinician.getMiddleName());
        statement.setString(4, clinician.getLastName());

        statement.executeUpdate();
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
                System.out.println("An error occured"); //TODO: Make this a popup
            }

            connection.prepareStatement("COMMIT");
            connection.close();
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
            connection.close();
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
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CLINICIAN_PASSWORD)) {

            Blob passwordBlob = connection.createBlob();
            //passwordBlob.setBytes(1, clinician.getPassword()); // todo: check if it's alright to make the password getter public, or would it be a security issue?

            Blob saltBlob = connection.createBlob();
            saltBlob.setBytes(1, clinician.getSalt());

            statement.setBlob(1, passwordBlob);
            statement.setBlob(2, saltBlob);
            statement.setString(3, clinician.getStaffId());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the given clinicians address in the database using UPDATE_ADDRESS
     * Precondition: Must have an active connection to the database
     * Postcondition: The address of the given clinician is updated in the database
     *
     * @param clinician  Clinician object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the clinicians address details
     */
    private void updateClinicianAddress(Clinician clinician, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CLINICIAN_ADDRESS)) {

//            statement.setString(1, clinician.getStreetNumber);
//            statement.setString(2, clinician.setStreetName);
//            statement.setString(3, clinician.setNeighborhood());
//            statement.setString(4, clinician.getCity());
            statement.setString(5, clinician.getRegion());
            //statement.setString(6, clinician.getZipCode());
            //statement.setString(7, clinician.getCountry());
            statement.setString(8, clinician.getStaffId());

            statement.executeUpdate();
        }
        // todo: update clinician to have an Address object - jen 30/6
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

            statement.setString(1, clinician.getStaffId());
            statement.setString(2, clinician.getFirstName());
            statement.setString(3, clinician.getMiddleName());
            statement.setString(4, clinician.getLastName());
            statement.setTimestamp(5, Timestamp.valueOf(clinician.getDateLastModified()));
            statement.setString(6, clinician.getStaffId());

            statement.executeUpdate();
        }
    }
}
