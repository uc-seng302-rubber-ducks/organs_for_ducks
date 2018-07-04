package seng302.utils.dbStrategies;

import seng302.model.Disease;
import seng302.model.MedicalProcedure;
import seng302.model.User;
import seng302.utils.Log;

import java.sql.*;
import java.util.Collection;

public class UserUpdateStrategy extends AbstractUpdateStrategy {

    private static final String CREATE_USER_STMT = "INSERT INTO User (nhi, firstName, middleName, lastName, preferedName, dob, dod, timeCreated, lastModified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_USER_CONTACT_STMT = "INSERT INTO ContactDetails (fkUserNhi, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADDRESS_STMT = "INSERT INTO Address (fkContactId, streetNumber, streetName, neighbourhood, city, region, country) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_HEALTH_DETAILS = "INSERT INTO HealthDetails (fkUserNhi, gender, birthGender, smoker, alcoholConsumption, height, weight) VALUES (?, ?, ?, ?, ?, ?, ?)"; // TODO: Include blood type 24/6 - Eiran
    private static final String CREATE_EMERGENCY_STMT = "INSERT INTO EmergencyContactDetails (fkContactId, contactName, contactRelationship) VALUES (?, ?, ?)";
    private static final String GET_LATEST_CONTACT_ENTRY = "SELECT MAX(contactId) AS contactId FROM ContactDetails WHERE fkUserNhi=?";
    private static final String CREATE_NEW_MEDICATION = "INSERT INTO Medication (fkUserNhi, medicationName) VALUES (?, ?)";
    private static final String CREATE_NEW_PROCEDURE = "INSERT INTO Procedure (fkUserNhi, procedureName, procedureDescription, procedureDate) VALUES (?, ?, ?)";

    private static final String UPDATE_USER_STMT = "UPDATE User SET nhi = ?, firstName = ?, middleName = ?, lastName = ?, preferredName = ?, dob = ?, dod = ?, lastModified = ? WHERE nhi = ?";
    private static final String UPDATE_USER_HEALTH_STMT = "UPDATE HealthDetails SET gender = ?, birthGender = ?, smoker = ?, alcoholConsumption = ?, height = ?, weight = ?, bloodType = ? WHERE fkUserNhi = ?";
    private static final String UPDATE_MEDICATION_DATE_STMT = "UPDATE MedicationDates SET dateStartedTaking = ?, dateStoppedTaking = ? WHERE fkMedicationInstanceId = ?";
    private static final String UPDATE_PROCEDURE_STMT = "UPDATE MedicalProcedure SET procedureName = ?, procedureDate = ?, procedureDescription = ? WHERE fkUserNhi = ?"; // needs an id
    private static final String UPDATE_USER_CONTACT_STMT = "UPDATE ContactDetails JOIN Address ON contactId = fkContactId " +
            "SET streetNumber = ?, streetName = ?, neighbourhood = ?, city = ?, region = ?, zipCode = ?, country = ?, homePhone = ?, cellPhone = ?, email = ? " +
            "WHERE ContactDetails.fkUserNhi = ?";
    private static final String UPDATE_EC_STMT = "UPDATE EmergencyContactDetails JOIN Address ON emergencyContactId = fkEmergencyContactId " +
            "SET contactName = ?, contactRelationship = ?, homePhone = ?, cellPhone = ?, email = ?, streetNumber = ?, streetName = ?, neighbourhood = ?, city = ?, region = ?, zipCode = ?, country = ? " +
            "WHERE EmergencyContactDetails.fkUserNhi = ?";

    private static final String DELETE_USER_STMT = "DELETE FROM User WHERE nhi = ?";
    private static final String DELETE_PROCEDURE_STMT = "DELETE FROM MedicalProcedure WHERE procedureDate = ? AND procedureName = ? AND fkUserNhi = ?";
    private static final String DELETE_USER_DISEASE_STMT = "DELETE FROM CurrentDisease WHERE diseaseName = ? AND diagnosisDate = ? AND fkUserNhi = ?";
    private static final String DELETE_PAST_DISEASE_STMT = "DELETE FROM PreviousDisease WHERE diseaseName = ? AND diagnosisDate = ? AND fkUserNhi = ?";
    private static final String DELETE_MEDICATION_STMT = "DELETE FROM Medication WHERE medicationName = ? AND fkUserNhi = ?";

    @Override
    public <T> void update(Collection<T> roles, Connection connection) throws SQLException {
        Collection<User> users = (Collection<User>) roles;
        for (User user : users) {
            if (user.getChanges().size() <= 0) {
                continue;
            }
            PreparedStatement stmt = connection.prepareStatement("SELECT nhi FROM User WHERE nhi = ?");
            stmt.setString(1, user.getNhi());
            ResultSet queryResults = stmt.executeQuery();
            if (!queryResults.next() && !user.isDeleted()) {
                executeCreation(user, connection);
            } else if (user.isDeleted()) {
                deleteRole(user, connection);
            } else {
                executeUpdate(user, connection);
            }
        }
    }

    /**
     * Executes an update for each of items in the collection.
     * Precondition: The object must be of a type User
     * Post-conditions: An entry that represents the user is created and stored in the database.
     *
     * @param user       User to create
     * @param connection Connection to the target database
     */
    private void executeCreation(User user, Connection connection) {
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                createUser(user, connection);
                createEmergencyContact(user.getNhi(), user, connection);
                createContact(user.getNhi(), user, connection);
                createHealthDetails(user.getNhi(), user, connection);
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
     * Creates an user entry in the tables.
     * Must have an active connection to the database (created through connect())
     *
     * @param user       user object to place into the entry
     * @param connection Connection to the target database
     * @throws SQLException if there is an issue with the execution of the of the statement.
     */
    private void createUser(User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(CREATE_USER_STMT);
        stmt.setString(1, user.getNhi());
        stmt.setString(2, user.getFirstName());
        stmt.setString(3, user.getMiddleName());
        stmt.setString(4, user.getLastName());
        stmt.setString(5, user.getPreferredFirstName());
        stmt.setDate(6, Date.valueOf(user.getDateOfBirth()));
        stmt.setDate(7, Date.valueOf(user.getDateOfDeath()));
        stmt.setTimestamp(8, Timestamp.valueOf(user.getTimeCreated()));
        stmt.setTimestamp(9, Timestamp.valueOf(user.getLastModified()));

        stmt.executeUpdate();
    }

    /**
     * Creates a contact object with for the given user using the CREATE_USER_CONTACT_STMT.
     * Must have an active connection to the database (created through connect())
     *
     * @param userNhi    nhi of the user to associate the contact object with.
     * @param user       user to create the associated contact for
     * @param connection Connection to the target database
     * @throws SQLException if there is a problem with creating the contact
     */
    private void createContact(String userNhi, User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(CREATE_USER_CONTACT_STMT);
        stmt.setString(1, userNhi);
        stmt.setString(2, user.getHomePhone());
        stmt.setString(3, user.getEmail());
        stmt.setString(4, user.getCellPhone());
        stmt.executeUpdate();

        String contactId = getContactId(userNhi, connection);

        PreparedStatement createAddrStatement = connection.prepareStatement(CREATE_ADDRESS_STMT);
        createAddrStatement.setString(1, contactId);
    }

    /**
     * Gets the most recently created contact object for a given userNhi.
     * Preconditions: A contact object for the user exists.
     * There is an active connection to the database created via connect()
     * Postconditions: The contactId is returned
     *
     * @param userNhi    NHI of the user to get the latest contact info for
     * @param connection Connection to the target database
     * @return The contact id of the latest contact entry for the specified user
     * @throws SQLException if a latest entry doesn't exist or the connection is null.
     */
    private String getContactId(String userNhi, Connection connection) throws SQLException {
        PreparedStatement getContactId = connection.prepareStatement(GET_LATEST_CONTACT_ENTRY);
        getContactId.setString(1, userNhi);
        ResultSet results = getContactId.executeQuery();
        if (results == null) {
            throw new SQLException("Required Contact Entry was not found");
        }
        return results.getString("contactId");
    }

    /**
     * Creates a health details entry in the database with the associated user
     * Must have an active connection to the database (created through connect())
     *
     * @param userNhi    NHI of the user to associate the health details with.
     * @param user       user to create the associated contact for
     * @param connection Connection to the target database
     * @throws SQLException if there is a problem when creating the health details
     */
    private void createHealthDetails(String userNhi, User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(CREATE_HEALTH_DETAILS);
        stmt.setString(1, userNhi);
        stmt.setString(2, user.getGenderIdentity());
        stmt.setString(3, user.getBirthGender());
        stmt.setBoolean(4, user.isSmoker());
        stmt.setString(5, user.getAlcoholConsumption());
        stmt.setDouble(6, user.getHeight());
        stmt.setDouble(7, user.getWeight());

        stmt.executeUpdate();
    }

    /**
     * Creates and links the emergency contact details to the user.
     * Must have an active connection to the database (created through connect())
     * Must be called before createContact, otherwise it may link to the wrong contact object.
     *
     * @param userNhi    The NHI of the user to link the contact details for
     * @param user       The user for which the emergency contact is to be created.
     * @param connection Connection to the target database
     * @throws SQLException If there is an error in creating the emergency contact.
     */
    private void createEmergencyContact(String userNhi, User user, Connection connection) throws SQLException {
        //TODO: Make the create contact method take in a contact object and make call to create emergency contact here. 25/6 - Eiran
        String contactId = getContactId(userNhi, connection);

        PreparedStatement stmt = connection.prepareStatement(CREATE_EMERGENCY_STMT);
        stmt.setString(1, contactId);
        stmt.setString(2, user.getContact().getName());
        stmt.setString(3, user.getContact().getRelationship());

        stmt.executeUpdate();
    }

    /**
     * Deletes a user entry from the database that is associated with the given object
     * Preconditions: The connection is not null and valid
     * Postcondition: The entry associated with the object will be deleted.
     *
     * @param user       The user associated with the entry to be deleted
     * @param connection Connection to the target database
     */
    private void deleteRole(User user, Connection connection) {
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                PreparedStatement stmt = connection.prepareStatement(DELETE_USER_STMT);
                stmt.setString(1, user.getNhi());

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
     * @param user       The user associated with the entry in the database
     * @param connection Connection ot the target database
     */
    private void executeUpdate(User user, Connection connection) {
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                updateUserDetails(user, connection);
                updateUserContactDetails(user, connection);
                updateEmergencyContact(user, connection);
                updateUserHealthDetails(user, connection);
                updateUserMedicalProcedures(user, connection);
                deleteUsersDetails(user, connection);
            } catch (SQLException sqlEx) {
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
     * Updates the contact details and address of the given user in the database
     *
     * @param user       User object with details to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the users contact details
     */
    private void updateUserContactDetails(User user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_CONTACT_STMT)) {

            statement.setString(1, user.getStreetNumber());
            statement.setString(2, user.getStreetName());
            statement.setString(3, user.getNeighborhood());
            statement.setString(4, user.getCity());
            statement.setString(5, user.getRegion());
            statement.setString(6, user.getZipCode());
            statement.setString(7, user.getCountry());
            statement.setString(8, user.getHomePhone());
            statement.setString(9, user.getCellPhone());
            statement.setString(10, user.getEmail());
            statement.setString(11, user.getNhi());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the emergency contact of the given user in the database
     *
     * @param user       User object
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the emergency contact details
     */
    private void updateEmergencyContact(User user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_EC_STMT)) {

            statement.setString(1, user.getContact().getName());
            statement.setString(2, user.getContact().getRelationship());
            statement.setString(3, user.getContact().getHomePhoneNumber());
            statement.setString(4, user.getContact().getCellPhoneNumber());
            statement.setString(5, user.getContact().getEmail());
            statement.setString(6, user.getContact().getStreetNumber());
            statement.setString(7, user.getContact().getStreetName());
            statement.setString(8, user.getContact().getNeighborhood());
            statement.setString(9, user.getContact().getCity());
            statement.setString(10, user.getContact().getRegion());
            statement.setString(11, user.getContact().getZipCode());
            statement.setString(12, user.getContact().getCountry());
            statement.setString(13, user.getNhi());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the given users health details in the database
     * Precondition: Must have an active connection to the database
     * Postcondition: The health details of the given user are updated
     *
     * @param user       User object with details to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the users health details
     */
    private void updateUserHealthDetails(User user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_HEALTH_STMT)) {

            statement.setString(1, user.getGenderIdentity());
            statement.setString(2, user.getBirthGender());
            statement.setBoolean(3, user.isSmoker());
            statement.setString(4, user.getAlcoholConsumption());
            statement.setDouble(5, user.getHeight());
            statement.setDouble(6, user.getWeight());
            statement.setString(7, user.getBloodType());
            statement.setString(8, user.getNhi());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the given users medical procedures in the database
     *
     * @param user       User object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the users medical procedures
     */
    private void updateUserMedicalProcedures(User user, Connection connection) throws SQLException {

        // loop through the users procedures and update each of them

    }

    /**
     * Updates the given user in the database using UPDATE_USER_STMT
     * Precondition: Must have an active connection to the database
     * Postcondition: The given user is updated in the database
     *
     * @param user       User object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the user details
     */
    private void updateUserDetails(User user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_STMT)) {

            statement.setString(1, user.getNhi());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getMiddleName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getPreferredFirstName());
            statement.setDate(6, Date.valueOf(user.getDateOfBirth()));
            statement.setDate(7, Date.valueOf(user.getDateOfDeath()));
            statement.setTimestamp(8, Timestamp.valueOf(user.getLastModified()));
            statement.setString(9, user.getNhi());

            statement.executeUpdate();
        }
    }

    /**
     * Deletes finer user details such as medications, procedures and diseases
     * Preconditions: There is an active connection to the database
     * Post-conditions: The changes made locally are reflected on the database
     *
     * @param user       user to delete the details of
     * @param connection Connection to the target database
     * @throws SQLException If there is an error deleting the details.
     */
    private void deleteUsersDetails(User user, Connection connection) throws SQLException {
        deleteUserProcedures(user, connection);
        deleteUserMedications(user, connection);
        deleteUserDiseases(user.getNhi(), user.getPastDiseases(), DELETE_PAST_DISEASE_STMT, connection);
        deleteUserDiseases(user.getNhi(), user.getCurrentDiseases(), DELETE_USER_DISEASE_STMT, connection);
    }

    /**
     * Deletes diseases that the user has had on the database to reflect the data on the application.
     * Pre-conditions: There is an active connection to the database, the provided collection is not null
     * Post-conditions: The data on the database reflects the data locally.
     *
     * @param userNhi    Nhi of the user to delete the diseases from
     * @param diseases   The user for which the previous disease belonged to.
     * @param statement  The sql delete statement to use. Can either be DELETE_PAST_DISEASE_STMT if the collection is of the user's past diseases
     *                   Or DELETE_USER_DISEASE_STMT if the collection is of the user's current diseases
     * @param connection Connection to the target database
     * @throws SQLException if the deletion fails or the connection is invalid
     */
    private void deleteUserDiseases(String userNhi, Collection<Disease> diseases, String statement, Connection connection) throws SQLException {
        for (Disease d : diseases) {
            if (d.isDeleted()) {
                PreparedStatement deleteDisease = connection.prepareStatement(statement);

                deleteDisease.setString(1, d.getName());
                deleteDisease.setDate(2, Date.valueOf(d.getDiagnosisDate()));
                deleteDisease.setString(3, userNhi);

                deleteDisease.executeUpdate();
            }
        }
    }

    /**
     * Deletes all medications marked for deletion for a given user
     * Pre-conditions: The user must not be null and there must be an active connection to the database
     * Post-condition: All marked medications will be deleted from the database
     *
     * @param user       User to delete all marked medications for. Cannot be null
     * @param connection Connection to the target database
     */
    private void deleteUserMedications(User user, Connection connection) {
        //TODO: Medications need to be an object as they cannot be marked for deletion in their current state.
    }

    /**
     * Delete procedures that are present in the database but not in the local user storage
     * Pre-conditions: There is an active connection to the database
     * Post-condition: The procedures that have been deleted locally are deleted in the database
     *
     * @param user       User to check and delete procedures for
     * @param connection Connection to the target database
     * @throws SQLException if the connection to the database is invalid
     */
    private void deleteUserProcedures(User user, Connection connection) throws SQLException {
        for (MedicalProcedure p : user.getMedicalProcedures()) {
            if (p.isDeleted()) {
                PreparedStatement deleteProcedure = connection.prepareStatement(DELETE_PROCEDURE_STMT);
                deleteProcedure.setString(1, p.getSummary());
                deleteProcedure.setDate(2, Date.valueOf(p.getProcedureDate()));
                deleteProcedure.setString(3, user.getNhi());

                deleteProcedure.executeUpdate();
            }
        }
    }
}
