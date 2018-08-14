package odms.commons.utils.db_strategies;

import odms.commons.model.Disease;
import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ContactDetails;
import odms.commons.model.datamodel.ExpiryReason;
import odms.commons.model.datamodel.Medication;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;
import odms.commons.utils.Log;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserUpdateStrategy extends AbstractUpdateStrategy {

    //<editor-fold desc="constants">
    private static final String CREATE_USER_STMT = "INSERT INTO User (nhi, firstName, middleName, lastName, preferedName, dob, dod, timeCreated, lastModified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_USER_CONTACT_STMT = "INSERT INTO ContactDetails (fkUserNhi, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADDRESS_STMT = "INSERT INTO Address (fkContactId, streetNumber, streetName, neighbourhood, city, region, country, fkUserNhi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_HEALTH_DETAILS = "INSERT INTO HealthDetails (fkUserNhi, gender, birthGender, smoker, alcoholConsumption, height, weight, bloodType) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_EMERGENCY_STMT = "INSERT INTO EmergencyContactDetails (fkContactId, contactName, contactRelationship, fkUserNhi) VALUES (?, ?, ?, ?)";
    private static final String GET_LATEST_CONTACT_ENTRY = "SELECT MAX(contactId) AS contactId FROM ContactDetails WHERE fkUserNhi=?";
    private static final String CREATE_NEW_MEDICATION = "INSERT INTO Medication (fkUserNhi, medicationName) VALUES (?, ?)";
    private static final String CREATE_NEW_MEDICATION_TIME = "INSERT INTO MedicationDates (fkMedicationInstanceId, dateStartedTaking, dateStoppedTaking) VALUES (?, ?, ?)";
    private static final String CREATE_NEW_PROCEDURE = "INSERT INTO MedicalProcedure (fkUserNhi, procedureName, procedureDescription, procedureDate) VALUES (?, ?, ?, ?)";
    private static final String CREATE_CURRENT_DISEASE = "INSERT INTO CurrentDisease (fkUserNhi, diseaseName, diagnosisDate, isChronic) VALUES (?, ?, ?, ?)";
    private static final String CREATE_PREVIOUS_DISEASE = "INSERT INTO PreviousDisease (fkUserNhi, diseaseName, diagnosisDate) VALUES (?, ?, ?)";
    private static final String CREATE_AFFECTED_ORGAN = "INSERT INTO MedicalProcedureOrgan (fkOrgansId, fkProcedureId) VALUES (?, ?)";
    private static final String CREATE_DONATING_ORGAN = "INSERT INTO OrganDonating (fkUserNhi, fkOrgansId) VALUES (?, ?)";
    private static final String CREATE_RECEIVING_ORGAN = "INSERT INTO OrganAwaiting (fkUserNhi, fkOrgansId) VALUES (?, ?)";
    private static final String CREATE_EXPIRY_DETAILS = "INSERT INTO OrganExpiryDetails(fkStaffId, fkDonatingId, timeOfExpiry, reason) VALUES (?,?,?,?)";

    private static final String UPDATE_USER_STMT = "UPDATE User SET nhi = ?, firstName = ?, middleName = ?, lastName = ?, preferedName = ?, dob = ?, dod = ?, lastModified = ? WHERE nhi = ?";
    private static final String UPDATE_USER_HEALTH_STMT = "UPDATE HealthDetails SET gender = ?, birthGender = ?, smoker = ?, alcoholConsumption = ?, height = ?, weight = ?, bloodType = ? WHERE fkUserNhi = ?";

    private static final String UPDATE_USER_CONTACT_STMT = "UPDATE ContactDetails JOIN Address ON contactId = fkContactId " +
            "SET streetNumber = ?, streetName = ?, neighbourhood = ?, city = ?, region = ?, zipCode = ?, country = ?, homePhone = ?, cellPhone = ?, email = ? " +
            "WHERE ContactDetails.fkUserNhi = ? AND contactId != ?";
    private static final String UPDATE_EC_STMT = "UPDATE ContactDetails JOIN EmergencyContactDetails ON contactId = EmergencyContactDetails.fkContactId " +
            "JOIN Address ON contactId = Address.fkContactId " +
            "SET contactName = ?, contactRelationship = ?, homePhone = ?, cellPhone = ?, email = ?, streetNumber = ?, streetName = ?, neighbourhood = ?, city = ?, region = ?, zipCode = ?, country = ? " +
            "WHERE EmergencyContactDetails.fkUserNhi = ?";

    private static final String DELETE_USER_STMT = "DELETE FROM User WHERE nhi = ?";
    private static final String CREATE_RECEIVING_ORGAN_DATE = "INSERT INTO OrganAwaitingDates (fkAwaitingId, dateRegistered, dateDeregistered) VALUES (?, ?, ?)";
    private static final String GET_RECEIVER_ID = "SELECT awaitingId FROM OrganAwaiting WHERE fkUserNhi = ? AND fkOrgansId = ?";
    private static final String GET_DONATING_ID = "SELECT donatingId FROM OrganDonating WHERE fkUserNhi = ? AND fkOrgansId = ?";
    public static final String START_TRANSACTION = "START TRANSACTION";
    public static final String ROLLBACK = "ROLLBACK";
    public static final String COMMIT = "COMMIT";
    private static final String CREATE_DEATH_DETAILS = "INSERT INTO DeathDetails (fkUserNhi, momentOfDeath, city, region, country) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_DEATH_DETAILS = "UPDATE DeathDetails SET momentOfDeath = ?, city = ?, region = ?, country = ? WHERE fkUserNhi = ?";
    //</editor-fold>

    @Override
    public <T> void update(Collection<T> roles, Connection connection) throws SQLException {
        Collection<User> users = (Collection<User>) roles;
        for (User user : users) {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT nhi FROM User WHERE nhi = ?")) {
                stmt.setString(1, user.getNhi());
                try (ResultSet queryResults = stmt.executeQuery()) {
                    if (!queryResults.next() && !user.isDeleted()) {
                        executeCreation(user, connection);
                    } else if (user.isDeleted()) {
                        deleteRole(user, connection);
                    } else {
                        executeUpdate(user, connection);
                    }
                }
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
    private void executeCreation(User user, Connection connection) throws SQLException {
        connection.prepareStatement(START_TRANSACTION).execute();
        try {
            createUser(user, connection);
            createEmergencyContact(user.getNhi(), user, connection);
            createContact(user.getNhi(), user.getContactDetails(), connection);
            createHealthDetails(user.getNhi(), user, connection);
            executeUpdate(user, connection);
            createDeathDetails(user, connection);
        } catch (SQLException sqlEx) {
            connection.prepareStatement(ROLLBACK).execute();
            throw sqlEx;
        }
        connection.prepareStatement(COMMIT).execute();
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
        try (PreparedStatement stmt = connection.prepareStatement(CREATE_USER_STMT)) {
            stmt.setString(1, user.getNhi());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getMiddleName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPreferredFirstName());
            stmt.setDate(6, Date.valueOf(user.getDateOfBirth()));
            if (user.getDateOfDeath() != null) {
                stmt.setDate(7, Date.valueOf(user.getDateOfDeath()));
            } else {
                stmt.setNull(7, Types.DATE);
            }
            stmt.setTimestamp(8, Timestamp.valueOf(user.getTimeCreated()));
            stmt.setTimestamp(9, Timestamp.valueOf(user.getLastModified()));

            stmt.executeUpdate();
        }
    }

    /**
     * Creates a contact object with for the given user using the CREATE_USER_CONTACT_STMT.
     * Must have an active connection to the database (created through connect())
     *
     * @param userNhi    nhi of the user to associate the contact object with.
     * @param contactDetails       user to create the associated contact for
     * @param connection Connection to the target database
     * @throws SQLException if there is a problem with creating the contact
     */
    private void createContact(String userNhi, ContactDetails contactDetails, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(CREATE_USER_CONTACT_STMT)) {
            stmt.setString(1, userNhi);
            stmt.setString(2, contactDetails.getHomePhoneNumber());
            stmt.setString(3, contactDetails.getEmail());
            stmt.setString(4, contactDetails.getCellPhoneNumber());
            stmt.executeUpdate();

            int contactId = getContactId(userNhi, connection);

            try (PreparedStatement createAddrStatement = connection.prepareStatement(CREATE_ADDRESS_STMT)) {
                createAddrStatement.setInt(1, contactId);
                createAddrStatement.setString(2, contactDetails.getStreetNumber());
                createAddrStatement.setString(3, contactDetails.getStreetName());
                createAddrStatement.setString(4, contactDetails.getNeighborhood());
                createAddrStatement.setString(5, contactDetails.getCity());
                createAddrStatement.setString(6, contactDetails.getRegion());
                createAddrStatement.setString(7, contactDetails.getCountry());
                createAddrStatement.setString(8, userNhi);

                createAddrStatement.executeUpdate();
            }
        }
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
    private int getContactId(String userNhi, Connection connection) throws SQLException {
        try (PreparedStatement getContactId = connection.prepareStatement(GET_LATEST_CONTACT_ENTRY)) {
            getContactId.setString(1, userNhi);
            try (ResultSet results = getContactId.executeQuery()) {
                results.next();
                return results.getInt("contactId");
            }
        }
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
        try (PreparedStatement stmt = connection.prepareStatement(CREATE_HEALTH_DETAILS)) {
            stmt.setString(1, userNhi);
            stmt.setString(2, user.getGenderIdentity());
            stmt.setString(3, user.getBirthGender());
            stmt.setBoolean(4, user.isSmoker());
            stmt.setString(5, user.getAlcoholConsumption());
            stmt.setDouble(6, user.getHeight());
            stmt.setDouble(7, user.getWeight());
            stmt.setString(8, user.getBloodType());

            stmt.executeUpdate();
        }
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
        createContact(userNhi, user.getContact(), connection);
        int contactId = getContactId(userNhi, connection);

        try (PreparedStatement stmt = connection.prepareStatement(CREATE_EMERGENCY_STMT)) {
            stmt.setInt(1, contactId);
            stmt.setString(2, user.getContact().getName());
            stmt.setString(3, user.getContact().getRelationship());
            stmt.setString(4, user.getNhi());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a user entry from the database that is associated with the given object
     * Preconditions: The connection is not null and valid
     * Postcondition: The entry associated with the object will be deleted.
     *
     * @param user       The user associated with the entry to be deleted
     * @param connection Connection to the target database
     */
    private void deleteRole(User user, Connection connection) throws SQLException {
        connection.prepareStatement(START_TRANSACTION).execute();
        try {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_USER_STMT)) {
                stmt.setString(1, user.getNhi());
                stmt.executeUpdate();
            }
        } catch (SQLException sqlEx) {
            Log.severe("A fatal error in deletion, cancelling operation", sqlEx);
            connection.prepareStatement(ROLLBACK).execute();
            throw sqlEx;
        }
        connection.prepareStatement(COMMIT).execute();
    }

    /**
     * Executes an update for an entry associated with the provided object.
     * Pre-condition: The connection is not null and valid
     * Post-condition: The entry in the database reflects the entry
     *
     * @param user       The user associated with the entry in the database
     * @param connection Connection ot the target database
     */
    private void executeUpdate(User user, Connection connection) throws SQLException {
        connection.prepareStatement(START_TRANSACTION).execute();
        try {
            updateUserDetails(user, connection);
            updateUserContactDetails(user, connection);
            updateEmergencyContact(user, connection);
            updateUserHealthDetails(user, connection);
            updateUserDonatingOrgans(user, connection);
            updateUserReceivingOrgans(user, connection);
            updateUserMedicalProcedures(user, connection);
            updateUserDiseases(user, connection);
            updateMedications(user, connection);
            updateDeathDetails(user, connection);
        } catch (SQLException sqlEx) {
            Log.severe("A fatal error in deletion, cancelling operation", sqlEx);
            connection.prepareStatement(ROLLBACK).execute();
            throw sqlEx;
        }
        connection.prepareStatement(COMMIT).execute();
    }

    /**
     * @param user
     * @param connection
     */
    private void updateUserReceivingOrgans(User user, Connection connection) throws SQLException {
        deleteFieldsOfUser("OrganAwaiting", user.getNhi(), connection);
        for (Organs organ : user.getReceiverDetails().getOrgans().keySet()) {
            try (PreparedStatement createReceivingOrgans = connection.prepareStatement(CREATE_RECEIVING_ORGAN)) {
                createReceivingOrgans.setString(1, user.getNhi());
                createReceivingOrgans.setInt(2, organ.getDbValue());

                createReceivingOrgans.executeUpdate();
            }
            int receiverId = getReceiverId(user.getNhi(), organ, connection);
            createReceivingOrganDates(receiverId, user.getReceiverDetails().getOrgans().get(organ), connection);
        }
    }

    /**
     * Obtains the stored id of the nhi-organ pair in the database
     *
     * @param nhi        the nhi of the nhi-organ pair
     * @param organ      the organ value of the nhi-organ pair
     * @param connection A non null and active connection to the database
     * @return the receiver id of the nhi-organ pair
     * @throws SQLException if there is an error with the database
     */
    private int getReceiverId(String nhi, Organs organ, Connection connection) throws SQLException {
        try (PreparedStatement getReceiverId = connection.prepareStatement(GET_RECEIVER_ID)) {
            getReceiverId.setString(1, nhi);
            getReceiverId.setInt(2, organ.getDbValue());
            try (ResultSet resultSet = getReceiverId.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("awaitingId");
            }
        }
    }

    /**
     * Populates the user's receiving organs dates into OrganAwaitingDates
     *
     * @param receiverId                  the receiverId of obtained from getReceiverId method
     * @param receiverOrganDetailsHolders A collection of holders for date data
     * @param connection                  A non null and active connection to the database
     */
    private void createReceivingOrganDates(int receiverId, Collection<ReceiverOrganDetailsHolder> receiverOrganDetailsHolders, Connection connection) throws SQLException {
        for (ReceiverOrganDetailsHolder holder : receiverOrganDetailsHolders) {
            try (PreparedStatement createReceivingOrganDates = connection.prepareStatement(CREATE_RECEIVING_ORGAN_DATE)) {
                createReceivingOrganDates.setInt(1, receiverId);
                createReceivingOrganDates.setDate(2, Date.valueOf(holder.getStartDate()));
                if (holder.getStopDate() != null) {
                    createReceivingOrganDates.setDate(3, Date.valueOf(holder.getStopDate()));
                } else {
                    createReceivingOrganDates.setNull(3, Types.DATE);
                }

                createReceivingOrganDates.executeUpdate();
            }
        }
    }

    /**
     * Updates the organs that the user is donating.
     * Calls the updateUserOrganExpiry method after organs for donation
     * are updated.
     *
     * @param user       user to associate the organs with.
     * @param connection A non null active connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void updateUserDonatingOrgans(User user, Connection connection) throws SQLException {
        deleteFieldsOfUser("OrganDonating", user.getNhi(), connection);
        for (Organs organ : user.getDonorDetails().getOrgans()) {
            try (PreparedStatement createDonatingOrgans = connection.prepareStatement(CREATE_DONATING_ORGAN)) {
                createDonatingOrgans.setString(1, user.getNhi());
                createDonatingOrgans.setInt(2, organ.getDbValue());

                createDonatingOrgans.executeUpdate();
            }
        }
        updateUserOrganExpiry(user, connection);
    }

    /**
     * updates the organ expiry details.
     *
     * @param user       user to associate the organs expiry details with.
     * @param connection A non null active connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void updateUserOrganExpiry(User user, Connection connection) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM OrganExpiryDetails WHERE fkDonatingId IN (SELECT donatingId FROM OrganDonating WHERE fkUserNhi = ?)")) {
            deleteStatement.setString(1, user.getNhi());
            deleteStatement.execute();
        }
        for (Map.Entry<Organs, ExpiryReason> organsExpiry: user.getDonorDetails().getOrganMap().entrySet()) {
            if (organsExpiry.getValue() != null) {
                int organDBValue = organsExpiry.getKey().getDbValue();
                int donatingId = getDonatingId(user.getNhi(), organDBValue, connection);
                try (PreparedStatement createExpiryDetails = connection.prepareStatement(CREATE_EXPIRY_DETAILS)) {
                    createExpiryDetails.setString(1, organsExpiry.getValue().getClinicianId());
                    createExpiryDetails.setInt(2, donatingId);
                    createExpiryDetails.setTimestamp(3, Timestamp.valueOf(organsExpiry.getValue().getTimeOrganExpired()));
                    createExpiryDetails.setString(4, organsExpiry.getValue().getReason());
                    createExpiryDetails.executeUpdate();
                }
            }
        }
    }

    /**
     * Retrieves the foreign key donating id referencing
     * the Organ Donating table
     *
     * @param userNhi NHI of user
     * @param organDBValue the database value of organ
     * @param connection Connection to the database
     * @return The foreign key donating id referencing the Organ Donating table
     * @throws SQLException If there is an issue retrieving the contact id
     */
    private int getDonatingId(String userNhi, int organDBValue, Connection connection) throws SQLException {
        int donatingId = -1;
        try (PreparedStatement statement = connection.prepareStatement(GET_DONATING_ID)) {

            statement.setString(1, userNhi);
            statement.setInt(2, organDBValue);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet != null && resultSet.next()) {
                    donatingId = resultSet.getInt(1);
                }
            }
        }
        return donatingId;
    }

    /**
     * Retrieves the emergency contact foreign key to the contact details table
     *
     * @param user User object to find the specific entry
     * @param connection Connection to the database
     * @return The foreign key contact id referencing the ContactDetails table
     * @throws SQLException If there is an issue retrieving the contact id
     */
    private int getEmergencyContactId(User user, Connection connection) throws SQLException {
        int contactId = -1;
        try (PreparedStatement statement = connection.prepareStatement("SELECT fkContactId FROM EmergencyContactDetails WHERE fkUserNhi = ?")) {

            statement.setString(1, user.getNhi());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet != null && resultSet.next()) {
                    contactId = resultSet.getInt(1);
                }
            }
        }
        return contactId;
    }

    /**
     * Updates the contact details and address of the given user in the database
     *
     * @param user       User object with details to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the users contact details
     */
    private void updateUserContactDetails(User user, Connection connection) throws SQLException {
        int id = getEmergencyContactId(user, connection);

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
            statement.setInt(12, id);

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
        deleteFieldsOfUser("MedicalProcedure", user.getNhi(), connection);
        for (MedicalProcedure procedure : user.getMedicalProcedures()) {
            createMedicalProcedure(user.getNhi(), procedure, connection);
            int procedureId = getMedicalProcedureId(user.getNhi(), procedure, connection);
            createOrgansAffected(procedureId, procedure.getOrgansAffected(), connection);
        }
    }

    /**
     * Takes a list of organs and a procedure id, and populates the MedicalProcedureOrgan table with the procedure id and the id of each organ in the provided list
     *
     * @param procedureId    id of the procedure, obtained first through getMedicalProcedureId
     * @param organsAffected list of organs affected by the medical procedure
     * @param connection     a non null, active and valid connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void createOrgansAffected(int procedureId, List<Organs> organsAffected, Connection connection) throws SQLException {
        for (Organs organ : organsAffected) {
            try (PreparedStatement insertOrgansAffected = connection.prepareStatement(CREATE_AFFECTED_ORGAN)) {
                insertOrgansAffected.setInt(1, organ.getDbValue());
                insertOrgansAffected.setInt(2, procedureId);

                insertOrgansAffected.executeUpdate();
            }
        }
    }

    /**
     * Obtains the procedure id of a given medical procedure from the database. Only works if the medical procedure already exists in the database
     *
     * @param userNhi    user of which the procedure belongs to
     * @param procedure  medical procedure to find within the database
     * @param connection A non null, active and valid connection to the database
     * @return the integer id of the procedure
     * @throws SQLException if there is an error in the database
     */
    private int getMedicalProcedureId(String userNhi, MedicalProcedure procedure, Connection connection) throws SQLException {
        try (PreparedStatement getProcedureId = connection.prepareStatement("SELECT procedureId FROM MedicalProcedure WHERE fkUserNhi = ? AND procedureName = ? AND procedureDate = ?")) {
            getProcedureId.setString(1, userNhi);
            getProcedureId.setString(2, procedure.getSummary());
            getProcedureId.setDate(3, Date.valueOf(procedure.getProcedureDate()));

            try (ResultSet results = getProcedureId.executeQuery()) {
                results.next();
                return results.getInt("procedureId");
            }
        }
    }

    /**
     * Creates a medical procedure in the database that is linked to the provided user nhi
     * @param nhi user's nhi to associate the medical procedure with
     * @param procedure procedure to create in the database
     * @param connection a non null, active and valid connection to the database
     * @throws SQLException If there is an error with the database
     */
    private void createMedicalProcedure(String nhi, MedicalProcedure procedure, Connection connection) throws SQLException {
        try (PreparedStatement createProcedure = connection.prepareStatement(CREATE_NEW_PROCEDURE)) {
            createProcedure.setString(1, nhi);
            createProcedure.setString(2, procedure.getSummary());
            createProcedure.setString(3, procedure.getDescription());
            createProcedure.setDate(4, Date.valueOf(procedure.getProcedureDate()));
            createProcedure.executeUpdate();
        }
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
            if (user.getDateOfDeath() != null) {
                statement.setDate(7, Date.valueOf(user.getDateOfDeath()));
            } else {
                statement.setNull(7, Types.DATE);
            }
            statement.setTimestamp(8, Timestamp.valueOf(user.getLastModified()));
            statement.setString(9, user.getNhi());

            statement.executeUpdate();
        }
    }

    /**
     * Updates the database with the user's previous and current diseases
     * @param user user for which to update the diseases in the database for
     * @param connection A non null and active connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void updateUserDiseases(User user, Connection connection) throws SQLException {
        deleteFieldsOfUser("CurrentDisease", user.getNhi(), connection);

        for (Disease disease : user.getCurrentDiseases()) {
            if (!disease.isDeleted()) {
                createCurrentDiseases(user.getNhi(), disease, connection);
            }
        }

        deleteFieldsOfUser("PreviousDisease", user.getNhi(), connection);

        for (Disease disease : user.getPastDiseases()) {
            if (!disease.isDeleted()) {
                createPreviousDisease(user.getNhi(), disease, connection);
            }
        }
    }

    /**
     * Updates the database with the users new death details
     * @param user for which to update the death details for
     * @param connection A non null and active connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void updateDeathDetails(User user, Connection connection) throws SQLException {

        Timestamp sqlDeathMoment = null;
        LocalDateTime deathMoment = user.getDeathDetails().createMomentOfDeath(user.getDateOfDeath(), user.getTimeOfDeath());
        if (deathMoment != null) {
            sqlDeathMoment = java.sql.Timestamp.valueOf(deathMoment);
        }


        try (PreparedStatement createDeathDetails  = connection.prepareStatement(UPDATE_DEATH_DETAILS)) {
            createDeathDetails.setTimestamp(1, sqlDeathMoment);
            createDeathDetails.setString(2, user.getDeathCity());
            createDeathDetails.setString(3, user.getDeathRegion());
            createDeathDetails.setString(4, user.getDeathCountry());
            createDeathDetails.setString(5, user.getNhi());

            createDeathDetails.executeUpdate();
        }
    }

    /**
     * Creates a disease in the CurrentDisease table associated with the provided user nhi
     * @param userNhi user to associate the disease with
     * @param disease disease to insert into the database
     * @param connection an active and valid connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void createCurrentDiseases(String userNhi, Disease disease, Connection connection) throws SQLException {
        try (PreparedStatement createDisease = connection.prepareStatement(CREATE_CURRENT_DISEASE)) {
            createDisease.setString(1, userNhi);
            createDisease.setString(2, disease.getName());
            createDisease.setDate(3, Date.valueOf(disease.getDiagnosisDate()));
            createDisease.setBoolean(4, disease.getIsChronic());

            createDisease.executeUpdate();
        }
    }

    /**
     * Creates a disease in the PastDisease table associated with the provided user nhi
     * @param userNhi user to associate the disease with
     * @param disease disease to insert into the database
     * @param connection an active and valid connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void createPreviousDisease(String userNhi, Disease disease, Connection connection) throws SQLException {
        try (PreparedStatement createDisease = connection.prepareStatement(CREATE_PREVIOUS_DISEASE)) {
            createDisease.setString(1, userNhi);
            createDisease.setString(2, disease.getName());
            createDisease.setDate(3, Date.valueOf(disease.getDiagnosisDate()));

            createDisease.executeUpdate();
        }
    }

    /**
     * Deletes all entries associated with the user in a provided table. SHOULD NEVER BE EXPOSED TO USERS
     * @param tableName table to delete from
     * @param user user for which to delete all corresponding entries of the provided table for
     *             e.g. providing table A and user ABC1234 will delete all the entries in table A associated with ABC1234
     * @param connection A non null and active connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void deleteFieldsOfUser(String tableName, String user, Connection connection) throws SQLException {
        try (PreparedStatement deleteDiseases = connection.prepareStatement("DELETE FROM " + tableName + " WHERE fkUserNhi = ?")) {
            deleteDiseases.setString(1, user);
            deleteDiseases.executeUpdate();
        }
    }

    /**
     * Deletes and repopulates the medication entries for a user
     * @param user user to repopulate the medication table for
     * @param connection A non null and active connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void updateMedications(User user, Connection connection) throws SQLException {
        deleteFieldsOfUser("Medication", user.getNhi(), connection);

        for (Medication med : user.getCurrentMedication()) {
            if (!med.isDeleted()) {
                createMedication(user.getNhi(), med, connection);
                int medicationInstanceId = getMedicationInstanceId(user.getNhi(), med, connection);
                createMedicationTimeRows(medicationInstanceId, med, connection);
            }
        }

        for (Medication med : user.getPreviousMedication()) {
            if (!med.isDeleted()) {
                createMedication(user.getNhi(), med, connection);
                int medicationInstanceId = getMedicationInstanceId(user.getNhi(), med, connection);
                createMedicationTimeRows(medicationInstanceId, med, connection);
            }
        }
    }

    /**
     * Obtains the medication instance id of a given medication from the database. Only works if the medication already exists in the database
     * @param userNhi the user associated with the medication searched for
     * @param med the medication searched for that is already present in the database
     * @param connection A non null and active connection to the database
     * @return the medication instance id of a medication associated with the provided user
     * @throws SQLException if there is an error with the database
     */
    private int getMedicationInstanceId(String userNhi, Medication med, Connection connection) throws SQLException {
        try (PreparedStatement getMedication = connection.prepareStatement("SELECT medicationInstanceId FROM Medication WHERE fkUserNhi = ? AND medicationName = ?")) {
            getMedication.setString(1, userNhi);
            getMedication.setString(2, med.getMedName());

            try (ResultSet results = getMedication.executeQuery()) {
                results.next();
                return results.getInt("medicationInstanceId");
            }
        }
    }

    /**
     * Creates the rows for all the medication times for an associated medication in MedicationDates
     * @param medicationInstanceId medication instance id of the medication stored in the database obtained through getMedicationInstanceId
     * @param med medication to create times for
     * @param connection A non null and active connection to the database
     * @throws SQLException if there is an error with the database
     */
    private void createMedicationTimeRows(int medicationInstanceId, Medication med, Connection connection) throws SQLException {
        Iterator<LocalDateTime> iter = med.getMedicationTimes().iterator();
        while (iter.hasNext()) {
            try (PreparedStatement addTimeEntry = connection.prepareStatement(CREATE_NEW_MEDICATION_TIME)) {
                addTimeEntry.setInt(1, medicationInstanceId);
                addTimeEntry.setTimestamp(2, Timestamp.valueOf(iter.next()));
                if (iter.hasNext()) {
                    addTimeEntry.setTimestamp(3, Timestamp.valueOf(iter.next()));
                } else {
                    addTimeEntry.setNull(3, Types.TIMESTAMP);
                }
                addTimeEntry.executeUpdate();
            }
        }
    }

    /**
     * Creates a medication entry in the database that is associated to the provided user
     * @param userNhi the user to associate the medication with.
     * @param med the medication to create in the database
     * @param connection A non null and active connection to the database
     * @throws SQLException If there is an error in the database
     */
    private void createMedication(String userNhi, Medication med, Connection connection) throws SQLException {
        try (PreparedStatement createMedication = connection.prepareStatement(CREATE_NEW_MEDICATION)) {
            createMedication.setString(1, userNhi);
            createMedication.setString(2, med.getMedName());

            createMedication.executeUpdate();
        }
    }

    /**
     * Creates or updates a deathDetails entry in the deathDetails table
     * @param user to update or create for
     * @param connection A non null and active connection to the database
     * @throws SQLException If there is an error in the database
     */
    private void createDeathDetails(User user, Connection connection) throws SQLException {

        Timestamp sqlDeathMoment = null;
        LocalDateTime deathMoment = user.getDeathDetails().createMomentOfDeath(user.getDateOfDeath(), user.getTimeOfDeath());
        if (deathMoment != null) {
            sqlDeathMoment = java.sql.Timestamp.valueOf(deathMoment);
        } else {
            return;
        }

        try (PreparedStatement createDeathDetails  = connection.prepareStatement(CREATE_DEATH_DETAILS)) {

            createDeathDetails.setString(1, user.getNhi());
            createDeathDetails.setTimestamp(2, sqlDeathMoment);
            createDeathDetails.setString(3, user.getDeathCity());
            createDeathDetails.setString(4, user.getDeathRegion());
            createDeathDetails.setString(5, user.getDeathCountry());

            createDeathDetails.executeUpdate();
        }
    }
}
