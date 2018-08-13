package odms.commons.utils;

import odms.commons.model.*;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.*;
import odms.commons.utils.db_strategies.AbstractUpdateStrategy;
import odms.commons.utils.db_strategies.AdminUpdateStrategy;
import odms.commons.utils.db_strategies.ClinicianUpdateStrategy;
import odms.commons.utils.db_strategies.UserUpdateStrategy;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DBHandler {

    /**
     * SQL commands for select
     * SELECT_USER_ONE_TO_ONE_INFO_STMT is for getting all info that follows one-to-one relationship. eg: 1 user can only have 1 address.
     * the other SELECT_USER statements are for getting all info that follows one-to-many relationships. eg: 1 user can have many diseases.
     */
    private static final String SELECT_USER_ONE_TO_ONE_INFO_STMT_FILTERED = "SELECT nhi " +
            "FROM User u LEFT JOIN Address a ON a.fkUserNhi = u.nhi " +
            "LEFT JOIN HealthDetails hd ON u.nhi = hd.fkUserNhi " +
            "WHERE (u.firstName LIKE ? OR u.lastName LIKE ?) " +
            "AND (a.region LIKE ? OR a.region IS NULL) " +
            "AND (hd.gender LIKE ? OR hd.gender IS NULL) " +
            "AND (a.fkContactId != (SELECT contactId FROM ContactDetails cd JOIN EmergencyContactDetails ecd ON cd.contactId = ecd.fkContactId WHERE ecd.fkUserNhi = nhi) OR a.fkContactId IS NULL) " +
            "LIMIT ? OFFSET ?";
    private static final String SELECT_ONE_USER_INFO_STMT_FILTERED = "SELECT nhi, firstName, middleName, LastName, preferedName, timeCreated, lastModified, profilePicture, gender, birthGender, smoker, " +
            "alcoholConsumption, height, weight, dob, dod, bloodType " +
            "FROM `User` u " +
            "LEFT JOIN HealthDetails hd ON u.nhi = hd.fkUserNhi " +
            "WHERE nhi = ?";
    private static final String SELECT_USER_ONE_TO_ONE_INFO_STMT = "SELECT nhi, firstName, middleName, LastName, preferedName, timeCreated, lastModified, profilePicture, gender, birthGender, dob, dod " +
            "FROM User u " +
            "LEFT JOIN HealthDetails hd ON u.nhi = hd.fkUserNhi ";
    private static final String SELECT_USER_PREVIOUS_DISEASE_STMT = "SELECT diseaseName, diagnosisDate FROM PreviousDisease WHERE fkUserNhi = ? " +
            "ORDER BY diagnosisDate";
    private static final String SELECT_USER_CURRENT_DISEASE_STMT = "SELECT diseaseName, diagnosisDate, isChronic FROM CurrentDisease WHERE fkUserNhi = ? " +
            "ORDER BY diagnosisDate";
    private static final String SELECT_USER_MEDICATION_STMT = "SELECT medicationName, dateStartedTaking, dateStoppedTaking FROM Medication m " +
            "LEFT JOIN MedicationDates md ON m.medicationInstanceId = md.fkMedicationInstanceId " +
            "WHERE m.fkUserNhi = ? ORDER BY dateStartedTaking";
    private static final String SELECT_USER_MEDICAL_PROCEDURE_STMT = "SELECT procedureName, procedureDate, procedureDescription, organName FROM MedicalProcedure mp " +
            "LEFT JOIN MedicalProcedureOrgan mpo ON mpo.fkProcedureId = mp.procedureId " +
            "LEFT JOIN Organ o ON o.organId = mpo.fkOrgansId " +
            "WHERE mp.fkUserNhi = ? " +
            "ORDER BY procedureDate";
    private static final String SELECT_USER_ORGAN_DONATING_STMT = "SELECT * FROM OrganDonating LEFT JOIN Organ ON fkOrgansId = organId LEFT JOIN OrganExpiryDetails ON fkDonatingId = donatingId WHERE fkUserNhi = ?";
    private static final String SELECT_USER_ORGAN_AWAITING_STMT = "SELECT dateRegistered, dateDeregistered, organId, organName " +
            "FROM OrganAwaiting " +
            "LEFT JOIN OrganAwaitingDates ON OrganAwaiting.awaitingId = OrganAwaitingDates.fkAwaitingId " +
            "LEFT JOIN Organ ON fkOrgansId = organId " +
            "WHERE fkUserNhi = ? " +
            "ORDER BY organId, dateRegistered";
    private static final String SELECT_USER_CONTACT_DETAILS_ADDRESS_STMT = "SELECT homePhone, cellPhone, email, streetNumber, streetName, neighbourhood, city, region, zipCode, country " +
            "FROM ContactDetails cd " +
            "LEFT JOIN Address a ON a.fkContactId = cd.contactId " +
            "WHERE cd.contactId NOT IN(SELECT fkContactId FROM EmergencyContactDetails) " +
            "AND cd.fkUserNhi = ?";
    private static final String SELECT_USER_EMERGENCY_CONTACT_DETAILS_ADDRESS_STMT = "SELECT contactName, contactRelationship, homePhone, cellPhone, email, " +
            "streetNumber, streetName, neighbourhood, city, region, zipCode, country " +
            "FROM EmergencyContactDetails ecd " +
            "LEFT JOIN ContactDetails cd ON ecd.fkContactId = cd.contactId " +
            "LEFT JOIN Address a ON a.fkContactId = cd.contactId " +
            "WHERE cd.fkUserNhi = ?";
    private static final String SELECT_CLINICIAN_ONE_TO_ONE_INFO_STMT = "SELECT staffId, firstName, middleName, lastName, timeCreated, lastModified, " +
            "streetNumber, streetName, neighbourhood, city, region, country, zipCode " +
            "FROM Clinician cl " +
            "LEFT JOIN Address a ON cl.staffId = a.fkStaffId " +
            "WHERE (firstName LIKE ? OR firstName IS NULL OR lastName LIKE ? OR lastName IS NULL )AND region LIKE ? or region IS NULL " +
            "LIMIT ? OFFSET ?";
    private static final String SELECT_ADMIN_ONE_TO_ONE_INFO_STMT = "SELECT userName, firstName, middleName, lastName, timeCreated, lastModified  FROM Administrator " +
            "WHERE (firstName LIKE ? OR firstName IS NULL )" +
            "OR (middleName LIKE ? OR middleName IS NULL)" +
            "OR (lastName LIKE ? OR lastName IS NULL)" +
            "LIMIT ? OFFSET ?";
    private static final String SELECT_SINGLE_ADMIN_ONE_TO_ONE_INFO_STMT = "SELECT userName, firstName, middleName, lastName, timeCreated, lastModified  FROM Administrator WHERE userName = ?";
    private static final String SELECT_PASS_DETAILS = "SELECT hash,salt FROM PasswordDetails WHERE fkAdminUserName = ? OR fkStaffId = ?";
    private static final String SELECT_USER_PROFILE_PHOTO_STMT = "SELECT profilePicture FROM User WHERE nhi = ?";
    private static final String SELECT_CLINICIAN_PROFILE_PHOTO_STMT = "SELECT profilePicture FROM Clinician WHERE staffId = ?";
    private static final String UPDATE_USER_PROFILE_PHOTO_STMT = "UPDATE User SET profilePicture= ?, pictureFormat = ? WHERE nhi = ?";
    private static final String UPDATE_CLINICIAN_PROFILE_PHOTO_STMT = "UPDATE Clinician SET profilePicture= ?, pictureFormat = ? WHERE staffId = ?";
    private static final String SELECT_ONE_CLINICIAN = "SELECT * FROM Clinician LEFT JOIN Address ON staffId = fkStaffId WHERE staffId = ?";
    private static final String SELECT_IF_USER_EXISTS_BOOL = "SELECT EXISTS(SELECT 1 FROM User WHERE nhi = ?)";
    private static final String SELECT_IF_CLINICIAN_EXISTS_BOOL = "SELECT EXISTS(SELECT 1 FROM Clinician WHERE staffId = ?)";
    private static final String SELECT_IF_ADMIN_EXISTS_BOOL = "SELECT EXISTS(SELECT 1 FROM Administrator WHERE userName = ?)";
    private static final String SELECT_AVAILABLE_ORGANS = "select * from OrganDonating " +
            "JOIN DeathDetails ON OrganDonating.fkUserNhi = DeathDetails.fkUserNhi " +
            "JOIN Organ ON OrganDonating.fkOrgansId = organId " +
            "JOIN HealthDetails ON OrganDonating.fkUserNhi = HealthDetails.fkUserNhi " +
            "WHERE (bloodType LIKE ? OR bloodType IS NULL)" +
            "AND (organName LIKE ? OR organName IS NULL )" +
            "AND (DeathDetails.region LIKE ? or DeathDetails.region IS NULL)" +
            "AND (DeathDetails.city LIKE ? or DeathDetails.city IS NULL)" +
            "AND (DeathDetails.country LIKE ? or DeathDetails.country IS NULL)" +
            "LIMIT ? OFFSET ?";
    private static final String SELECT_DEATH_DETAILS_STMT = "SELECT * FROM DeathDetails WHERE fkUserNhi = ?";
    private AbstractUpdateStrategy updateStrategy;


    /**
     * Takes a generic, valid SQL String as an argument and executes it and returns the result
     *
     * @param statement Statement to run
     * @param conn      connection to process it on
     * @return Result of execution
     * @throws SQLException Thrown on bad statement
     */
    public ResultSet executeStatement(String statement, Connection conn) throws SQLException {
        try {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                return preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            Log.severe("Exception in executing statement " + statement, e);
            throw e;
        }
    }

    /**
     * Method with less filtering parameters to obtain all the users information from the database based on filtering provided.
     * User objects are instantiated and its attributes are set based on the de-serialised information.
     *
     * @param connection A valid connection to the database
     * @param count      number of items returned
     * @param startIndex number of items to skip
     * @return a Collection of Users
     * @throws SQLException if there are any SQL errors
     */
    public Collection<User> getUsers(Connection connection, int count, int startIndex) throws SQLException {
        return this.getUsers(connection, count, startIndex, "", "", "");
    }

    /**
     * Method to obtain all the users information from the database based on filtering provided.
     * User objects are instantiated and its attributes are set based on the de-serialised information.
     *
     * @param connection A valid connection to the database
     * @param count      number of items returned
     * @param startIndex number of items to skip
     * @param name       name of a user
     * @param region     region of a user
     * @param gender     gender of the user
     * @return a Collection of Users
     * @throws SQLException if there are any SQL errors
     */
    public Collection<User> getUsers(Connection connection, int count, int startIndex, String name, String region, String gender) throws SQLException {
        Collection<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_ONE_TO_ONE_INFO_STMT_FILTERED)) {
            statement.setString(1, name + "%");
            statement.setString(2, name + "%");
            statement.setString(3, region + "%");
            statement.setString(4, gender + "%");
            statement.setInt(5, count);
            statement.setInt(6, startIndex);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(getOneUser(connection, resultSet.getString("nhi")));
                }
            }
        }

        return users;
    }


    /**
     * gets the info of a single administrator
     *
     * @param connection A valid connection to the database
     * @param username   the username of the User
     * @return an Afministrator
     * @throws SQLException if there are errors with the SQL statements
     */
    public Administrator getOneAdministrator(Connection connection, String username) throws SQLException {
        Administrator administrator = null;
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SINGLE_ADMIN_ONE_TO_ONE_INFO_STMT)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    administrator = createAdmin(resultSet);
                }
            }

        }
        return administrator;
    }

    /**
     * Gets info of a single clinician based on clinician staffId provided
     *
     * @param connection A valid connection to the database
     * @param staffId    staffId of the clinician
     * @return a clinician object, null if such clinician is not found
     * @throws SQLException if there are any SQL errors
     */
    public Clinician getOneClinician(Connection connection, String staffId) throws SQLException {
        Clinician clinician = new Clinician();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE_CLINICIAN)) {
            statement.setString(1, staffId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    clinician = getClinicianBasicDetails(resultSet);
                    clinician.setMiddleName(resultSet.getString("middleName"));
                    clinician.setLastName(resultSet.getString("lastName"));
                    clinician.setDateLastModified(resultSet.getTimestamp("lastModified").toLocalDateTime());
                    clinician.setDateCreated(resultSet.getTimestamp("timeCreated").toLocalDateTime());
                    clinician.setStreetNumber(resultSet.getString("streetNumber"));
                    clinician.setStreetName(resultSet.getString("streetName"));
                    clinician.setNeighborhood(resultSet.getString("neighbourhood"));
                    clinician.setCity(resultSet.getString("city"));
                    clinician.setRegion(resultSet.getString("region"));
                    clinician.setCountry(resultSet.getString("country"));
                    clinician.setZipCode(resultSet.getString("zipCode"));
                }
            }
        }
        return clinician;
    }


    /**
     * Gets info of a single user based on user NHI provided
     *
     * @param connection A valid connection to the database
     * @param nhi        user's NHI
     * @return a user object, null if such user is not found
     * @throws SQLException if there are any SQL errors
     */
    public User getOneUser(Connection connection, String nhi) throws SQLException {
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE_USER_INFO_STMT_FILTERED)) {
            statement.setString(1, nhi);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    user = getUserBasicDetails(resultSet);
                    user.setTimeCreated(resultSet.getTimestamp("timeCreated").toLocalDateTime());
                    user.setLastModified(resultSet.getTimestamp("lastModified").toLocalDateTime());
                    user.setGenderIdentity(resultSet.getString("gender"));
                    user.setBirthGender(resultSet.getString("birthGender"));
                    user.setSmoker(1 == resultSet.getInt("smoker"));
                    user.setAlcoholConsumption(resultSet.getString("alcoholConsumption"));
                    user.setHeight(resultSet.getDouble("height"));
                    user.setWeight(resultSet.getDouble("weight"));
                    user.setBloodType(resultSet.getString("bloodType"));

                    try {
                        getUserPastDisease(user, connection);
                        getUserCurrentDisease(user, connection);
                        getUserMedication(user, connection);
                        getUserMedicalProcedure(user, connection);
                        getUserOrganDonateDetail(user, connection);
                        getUserOrganReceiveDetail(user, connection);
                        getUserContact(user, connection);
                        getUserEmergencyContact(user, connection);
                        getDeathDetails(user, connection);
                    } catch (SQLException e) {
                        Log.warning("Unable to create instance of user with nhi " + user.getNhi(), e);
                        throw e;
                    }
                }
            }
        }

        return user;
    }

    /**
     * get the basic details of a user from a result set
     *
     * @param resultSet result set with the cursor pointing at the desired row
     * @return a Clinician
     * @throws SQLException if there is an error extracting information from the resultSet
     */
    private Clinician getClinicianBasicDetails(ResultSet resultSet) throws SQLException {
        return new Clinician(resultSet.getString("firstName"), resultSet.getString("staffId"), "");
    }

    /**
     * get the users health details
     *
     * @param resultSet result set with the cursor pointing at the desired row
     * @return A user with a first name, middle name, last name, date of birth and date of death
     * @throws SQLException if there is an error extracting information from the resultSet
     */
    private User getUserBasicDetails(ResultSet resultSet) throws SQLException {
        User user = new User(resultSet.getString("firstName"), resultSet.getDate("dob").toLocalDate(), resultSet.getString("nhi"));
        if (resultSet.getString("dod") != null) {
            user.setDateOfDeath(resultSet.getDate("dod").toLocalDate());
        }
        user.setMiddleName(resultSet.getString("middleName"));
        user.setLastName(resultSet.getString("lastName"));
        return user;
    }

    /**
     * Method to obtain an overview of all the users information from the database.
     * User objects are instantiated and its attributes are set based on the de-serialised information.
     *
     * @param connection A valid connection to the database
     * @return a Collection of Users
     * @throws SQLException if there are any SQL errors
     */
    public Collection<User> getUserOverviews(Connection connection) throws SQLException {
        Collection<User> users = new ArrayList<>();
        try (PreparedStatement fetchUserOverview = connection.prepareStatement(SELECT_USER_ONE_TO_ONE_INFO_STMT)) {
            try (ResultSet results = fetchUserOverview.executeQuery()) {
                while (results.next()) {
                    User user = getUserBasicDetails(results);
                    getUserOrganDonateDetail(user, connection);
                    getUserOrganReceiveDetail(user, connection);
                    users.add(user);
                }
            }
        }

        return users;
    }

    /**
     * gets all info of user's contact details (includes the address).
     * Then, the data are de-serialised and added to User object.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserContact(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_CONTACT_DETAILS_ADDRESS_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    user.setHomePhone(resultSet.getString("homePhone"));
                    user.setCellPhone(resultSet.getString("cellPhone"));
                    user.setEmail(resultSet.getString("email"));
                    getAddressResults(user.getContactDetails(), resultSet);
                }
            }
        }
    }

    /**
     * gets all info of user's emergency contact details (includes the address).
     * Then, the data are de-serialised and added to User object.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserEmergencyContact(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_EMERGENCY_CONTACT_DETAILS_ADDRESS_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    user.getContact().setName(resultSet.getString("contactName"));
                    user.getContact().setRelationship(resultSet.getString("contactRelationship"));
                    user.getContact().setHomePhoneNumber(resultSet.getString("homePhone"));
                    user.getContact().setCellPhoneNumber(resultSet.getString("cellPhone"));
                    user.getContact().setEmail(resultSet.getString("email"));
                    getAddressResults(user.getContact(), resultSet);

                }
            }
        }
    }

    /**
     * gets all the info for the address for contact details and sets it
     *
     * @param contactDetails the contact details
     * @param resultSet      result set with the cursor pointing at the desired row
     * @throws SQLException exception thrown during the transaction
     */
    private void getAddressResults(ContactDetails contactDetails, ResultSet resultSet) throws SQLException {
        contactDetails.setStreetNumber(resultSet.getString("streetNumber"));
        contactDetails.setStreetName(resultSet.getString("streetName"));
        contactDetails.setNeighborhood(resultSet.getString("neighbourhood"));
        contactDetails.setCity(resultSet.getString("city"));
        contactDetails.setRegion(resultSet.getString("region"));
        contactDetails.setZipCode(resultSet.getString("zipCode"));
        contactDetails.setCountry(resultSet.getString("country"));
    }

    /**
     * gets all info of past diseases of a user.
     * Then, Disease objects are instantiated and its attributes are set based on the de-serialised information.
     * Finally, the Disease objects are added to User object.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserPastDisease(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_PREVIOUS_DISEASE_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    Disease pastDisease = new Disease(resultSet.getString("diseaseName"), false, true, resultSet.getDate("diagnosisDate").toLocalDate());
                    user.getPastDiseases().add(pastDisease);
                }
            }
        }
    }

    /**
     * gets all info of current diseases of a user.
     * Then, Disease objects are instantiated and its attributes are set based on the de-serialised information.
     * Finally, the Disease objects are added to User object.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserCurrentDisease(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_CURRENT_DISEASE_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    Disease currentDisease = new Disease(resultSet.getString("diseaseName"), resultSet.getBoolean("isChronic"), false, resultSet.getDate("diagnosisDate").toLocalDate());
                    user.getCurrentDiseases().add(currentDisease);
                }
            }
        }
    }

    /**
     * gets all info of organs the user is donating.
     * Then, the organ data are de-serialised and added to User object.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserOrganDonateDetail(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_ORGAN_DONATING_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    ExpiryReason expiryReason = new ExpiryReason(resultSet.getString("fkStaffId"),
                            resultSet.getTimestamp("timeOfExpiry") != null ? resultSet.getTimestamp("timeOfExpiry").toLocalDateTime() : null,
                            resultSet.getString("reason"));
                    String organ = resultSet.getString("organName");
                    user.getDonorDetails().addOrgan(Organs.valueOf(organ), expiryReason);
                }
            }
        }
    }

    /**
     * gets all info of organs the user is receiving.
     * Then, the organ data de-serialised and added to User object.
     * Note: Receiving = Awaiting
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserOrganReceiveDetail(User user, Connection connection) throws SQLException {
        ArrayList<ReceiverOrganDetailsHolder> receiverOrganDetailsHolders = new ArrayList<>();
        Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> organs = new EnumMap<>(Organs.class);

        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_ORGAN_AWAITING_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    ReceiverOrganDetailsHolder receiverOrganDetailsHolder = new ReceiverOrganDetailsHolder(resultSet.getDate("dateRegistered").toLocalDate(), null, null);
                    if (resultSet.getString("dateDeregistered") != null) {
                        receiverOrganDetailsHolder.setStopDate(resultSet.getDate("dateDeregistered").toLocalDate());
                    }

                    for (Organs organ : Organs.values()) {
                        if (organ.getDbValue() == resultSet.getInt("organId")) {
                            if (organs.containsKey(organ)) {
                                organs.get(organ).add(receiverOrganDetailsHolder);
                            } else {
                                receiverOrganDetailsHolders.add(receiverOrganDetailsHolder);
                                organs.put(organ, receiverOrganDetailsHolders);
                            }
                        }
                    }
                    receiverOrganDetailsHolders = new ArrayList<>(); //WARNING
                }
                user.getReceiverDetails().setOrgans(organs);
            }
        }
    }

    /**
     * gets all info of Medication of a user.
     * then Medication objects are instantiated.
     * Finally, user's currentMedication and previousMedication attributes are populated based
     * on whether the dateStoppedTaking column from database is null or not.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserMedication(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_MEDICATION_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    Medication medication = new Medication(resultSet.getString("medicationName"));
                    medication.addMedicationTime(resultSet.getTimestamp("dateStartedTaking").toLocalDateTime());

                    if (resultSet.getTimestamp("dateStoppedTaking") == null) {
                        int medicationInstanceIndex = user.getPreviousMedication().indexOf(medication);
                        if (medicationInstanceIndex != -1) { //if the medication instance already exist in this user's previousMedication attribute. This scenario happens when a user is currently taking a medication that had been taken before.
                            medication = user.getPreviousMedication().get(medicationInstanceIndex);
                            medication.addMedicationTime(resultSet.getTimestamp("dateStartedTaking").toLocalDateTime()); //updates the medication time array
                            user.getPreviousMedication().remove(medication);
                            user.getCurrentMedication().add(medication);
                        } else {
                            user.getCurrentMedication().add(medication);
                        }

                    } else {
                        medication.addMedicationTime(resultSet.getTimestamp("dateStoppedTaking").toLocalDateTime());
                        user.getPreviousMedication().add(medication);
                    }
                }
            }
        }
    }

    /**
     * gets all info of Medical procedure of a user
     * Then, MedicalProcedure objects are instantiated and its attributes are set based on the de-serialised information.
     * Finally, the MedicalProcedure objects are added to User object.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserMedicalProcedure(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_MEDICAL_PROCEDURE_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                MedicalProcedure medicalProcedure = null;
                while (resultSet != null && resultSet.next()) {
                    if (medicalProcedure != null) {
                        medicalProcedure.addOrgan(Organs.valueOf(resultSet.getString(4)));

                    } else {
                        medicalProcedure = new MedicalProcedure(resultSet.getDate(2).toLocalDate(), resultSet.getString(1), resultSet.getString(3), null);
                        medicalProcedure.addOrgan(Organs.valueOf(resultSet.getString(4)));
                        user.getMedicalProcedures().add(medicalProcedure);
                    }
                }
            }
        }
    }

    /**
     * Method to save all the users to the database.
     *
     * @param connection connection to the database to be accessed
     * @param users      A non null collection of users to save to the database
     */
    public void saveUsers(Collection<User> users, Connection connection) {
        updateStrategy = new UserUpdateStrategy();
        updateDatabase(users, connection);
    }

    /**
     * Method to save a single user to the database
     *
     * @param connection connection to the database to be accessed
     * @param user       A non null user to save to the database
     */
    public void saveUser(User user, Connection connection) {
        updateStrategy = new UserUpdateStrategy();
        Collection<User> users = Collections.singletonList(user);
        updateDatabase(users, connection);
    }

    public Collection<Clinician> loadClinicians(Connection connection, int startIndex, int count) throws SQLException {
        return loadClinicians(connection, startIndex, count, "", "");
    }

    /**
     * Loads the clinicians from the database.
     *
     * @param connection a Connection to the target database
     * @param startIndex starting value to receive from
     * @param count count of clinicians
     * @param name name of the clinicians
     * @param region region the clinician resides in
     * @return the Collection of clinicians
     */
    public Collection<Clinician> loadClinicians(Connection connection, int startIndex, int count, String name, String region) throws SQLException {
        Collection<Clinician> clinicians = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_CLINICIAN_ONE_TO_ONE_INFO_STMT)) {
            statement.setString(1, name + "%");
            statement.setString(2, name + "%");
            statement.setString(3, region + "%");
            statement.setInt(4, count);
            statement.setInt(5, startIndex);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet != null && resultSet.next()) {
                    Clinician clinician = new Clinician();
                    clinician.setStaffId(resultSet.getString("staffId"));
                    clinician.setFirstName(resultSet.getString("firstName"));
                    clinician.setMiddleName(resultSet.getString("middleName"));
                    clinician.setLastName(resultSet.getString("lastName"));
                    clinician.setDateCreated(resultSet.getTimestamp("timeCreated").toLocalDateTime());
                    clinician.setDateLastModified(resultSet.getTimestamp("lastModified").toLocalDateTime());
                    getAddressResults(clinician.getWorkContactDetails(), resultSet);
                    clinicians.add(clinician);
                }
                return clinicians;
            }
        }
    }

    /**
     * Method to save a single Clinician to the database
     *
     * @param clinician  a non null clinician to save to the database
     * @param connection a Connection to the target database
     */
    public void saveClinician(Clinician clinician, Connection connection) {
        updateStrategy = new ClinicianUpdateStrategy();
        Collection<Clinician> clinicians = Collections.singletonList(clinician);
        updateDatabase(clinicians, connection);
    }

    /**
     * Updates the clinicians stored in active memory.
     *
     * @param clinicians Collection of clinicians to update.
     * @param connection connection to the targeted database
     */
    public void saveClinicians(Collection<Clinician> clinicians, Connection connection) {
        updateStrategy = new ClinicianUpdateStrategy();
        updateDatabase(clinicians, connection);
    }

    /**
     * Loads the administrators from the database.
     *
     * @param connection Connection to the target database
     * @param startIndex starting value to receive from
     * @param count count of administrators
     * @param name name of the administrators
     * @return the Collection of administrators
     * @throws SQLException if there are errors with the SQL statements
     */
    public Collection<Administrator> loadAdmins(Connection connection, int startIndex, int count, String name) throws SQLException {
        Collection<Administrator> administrators = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ADMIN_ONE_TO_ONE_INFO_STMT)) {
            statement.setString(1,name + "%");
            statement.setString(2,name + "%");
            statement.setString(3,name + "%");
            statement.setInt(4,count);
            statement.setInt(5,startIndex);
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet != null && resultSet.next()) {
                    Administrator administrator = createAdmin(resultSet);
                    administrators.add(administrator);
                }

                return administrators;
            }
        }
    }

    /**
     * creates a admin from a result set
     *
     * @param resultSet result set with the cursor pointing at the desired row
     * @return A new Administrator
     * @throws SQLException when there are any SQL errors
     */
    private Administrator createAdmin(ResultSet resultSet) throws SQLException {
        Administrator administrator = new Administrator();
        administrator.setUserName(resultSet.getString(1));
        administrator.setFirstName(resultSet.getString(2));
        administrator.setMiddleName(resultSet.getString(3));
        administrator.setLastName(resultSet.getString(4));
        administrator.setDateCreated(resultSet.getTimestamp(5).toLocalDateTime());
        administrator.setDateLastModified(resultSet.getTimestamp(6).toLocalDateTime());
        return administrator;
    }

    /**
     * Updates the administrators stored in active memory.
     *
     * @param administrators Collection of admins to update.
     * @param connection     Connection to the target database
     */
    public void saveAdministrators(Collection<Administrator> administrators, Connection connection) {
        updateStrategy = new AdminUpdateStrategy();
        updateDatabase(administrators, connection);
    }

    /**
     * Updates the administrators stored in active memory.
     *
     * @param administrator Admin to update.
     * @param connection    Connection to the target database
     */
    public void saveAdministrator(Administrator administrator, Connection connection) {
        updateStrategy = new AdminUpdateStrategy();
        Collection<Administrator> administrators = Collections.singletonList(administrator);
        updateDatabase(administrators, connection);
    }

    /**
     * Executes an update for each of items in the collection. The Collection must be of a type User, Clinician or Administrator
     *
     * @param collection collection of objects to update the database with
     * @param connection Connection to the target database
     * @param <T>        User, Clinician or Administrator
     */
    private <T> void updateDatabase(Collection<T> collection, Connection connection) {
        try {
            updateStrategy.update(collection, connection);
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
        }
    }

    /**
     * Checks to see whether a correct password has been entered
     *
     * @param connection connection ot the test database
     * @param guess      Guess at the password
     * @param id         ID to check password for
     * @param loginType  type of login ot check for
     * @return if the password is correct
     */
    public boolean isValidLogIn(Connection connection, String guess, String id, String loginType) {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PASS_DETAILS)) {
            if (loginType.equalsIgnoreCase("admin")) {
                statement.setString(1, id);
                statement.setString(2, "");
            } else {
                statement.setString(1, "");
                statement.setString(2, id);
            }
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                String hash = rs.getString("hash");
                String salt = rs.getString("salt");
                return PasswordManager.isExpectedPassword(guess, salt, hash);
            } catch (SQLException e) {
                Log.warning("Could not log in", e);
                return false;
            }
        } catch (SQLException e) {
            Log.warning("Could not log in", e);
            return false;
        }
    }

    /**
     * replaces an existing clinician with a new version
     * finds old administrator by username and marks it for deletion, then passes it and the new administrator to
     *
     * @param connection    connection to the target database
     * @param username      (old) username of administrator
     * @param administrator administrator to be put into database
     * @throws SQLException exception thrown during the transaction
     */
    public void updateAdministrator(Connection connection, String username, Administrator administrator) throws SQLException {
        Administrator toReplace = getOneAdministrator(connection, username);
        Collection<Administrator> administrators = new ArrayList<>();
        if (toReplace != null && !toReplace.getUserName().equals(administrator.getUserName())) {
            toReplace.setDeleted(true);
            administrators.add(toReplace);
        }
        administrator.addChange(new Change("Saved"));
        administrators.add(administrator);
        saveAdministrators(administrators, connection);
    }

    /**
     * finds a single Administrator and sets their deleted flag to true, then updates the Administrator on the db
     *
     * @param connection connection to the target database
     * @param username   username of the Administrator to be deleted
     * @throws SQLException exception thrown during the transaction
     */
    public void deleteAdministrator(Connection connection, String username) throws SQLException {
        Administrator toDelete = getOneAdministrator(connection, username);
        if (toDelete != null) {
            toDelete.setDeleted(true);
            saveAdministrators(Collections.singletonList(toDelete), connection);
        }
    }


    /**
     * replaces an existing clinician with a new version
     * finds old clinician by staffId and marks it for deletion, then passes it and the new clinician to
     *
     * @param connection connection to the target database
     * @param staffId    (old) staffId of clinician
     * @param clinician  clinician to be put into database
     * @throws SQLException exception thrown during the transaction
     */
    public void updateClinician(Connection connection, String staffId, Clinician clinician) throws SQLException {
        Clinician toReplace = getOneClinician(connection, staffId);
        Collection<Clinician> clinicians = new ArrayList<>();
        if (toReplace != null && !staffId.equals(clinician.getStaffId())) {
            toReplace.setDeleted(true);
            clinicians.add(toReplace);
        }
        clinician.addChange(new Change("Saved"));
        clinicians.add(clinician);
        Log.info(clinicians.toString());
        saveClinicians(clinicians, connection);
    }

    /**
     * finds a single Clinician and sets their deleted flag to true, then updates the Clinician on the db
     *
     * @param connection connection to the target database
     * @param staffId    staffId of the clinician to be deleted
     * @throws SQLException exception thrown during the transaction
     */
    public void deleteClinician(Connection connection, String staffId) throws SQLException {
        Clinician toDelete = getOneClinician(connection, staffId);
        if (toDelete != null) {
            toDelete.setDeleted(true);
            saveClinicians(Collections.singletonList(toDelete), connection);
        }
    }

    /**
     * replaces an existing user with a new version
     * finds old user by nhi and marks it for deletion, then passes it and the new user to
     *
     * @param conn connection to the target database
     * @param nhi  (old) nhi of user
     * @param user user to be put into database
     * @throws SQLException exception thrown during the transaction
     */
    public void updateUser(Connection conn, String nhi, User user) throws SQLException {
        User toReplace = getOneUser(conn, nhi);
        if (toReplace != null) {
            toReplace.setDeleted(true);
        }
        user.addChange(new Change("Saved"));
        Collection<User> users = new ArrayList<>(Arrays.asList(toReplace, user));
        saveUsers(users, conn);
    }

    /**
     * finds a single user and sets their deleted flag to true, then updates the user on the db
     *
     * @param conn connection to the target database
     * @param nhi  nhi of the user to be deleted
     * @throws SQLException exception thrown during the transaction
     */
    public void deleteUser(Connection conn, String nhi) throws SQLException {
        User toDelete = getOneUser(conn, nhi);
        if (toDelete != null) {
            toDelete.setDeleted(true);
            saveUsers(Collections.singleton(toDelete), conn);
        }
    }

    /**
     * gets all relevant details relating to users waiting to receive an organ transplant
     *
     * @param conn       connection to the target database
     * @param startIndex row number to start reading from
     * @param count      number of results to return
     * @param name       string query to search in the name (first, middle, or last). should be an empty string if no restriction to be made
     * @param region     restrict results to a given region. search for regions LIKE the given string. should be an empty string if no restriction to be made
     * @param organs     restrict results to a given set of organs. should be null if no restriction to be made
     * @return list of transplant details matching the above criteria
     * @throws SQLException exception thrown during the transaction
     * @see TransplantDetails
     */
    public List<TransplantDetails> getTransplantDetails(Connection conn, int startIndex, int count, String name, String region, String[] organs) throws SQLException {
        StringBuilder queryString = new StringBuilder("SELECT U.nhi, U.firstName, U.middleName, U.lastName, O.organName, Dates.dateRegistered, Q.region, DD.momentOfDeath from OrganAwaiting JOIN Organ O ON OrganAwaiting.fkOrgansId = O.organId\n" +
                "  LEFT JOIN User U ON OrganAwaiting.fkUserNhi = U.nhi\n" +
                "  LEFT JOIN DeathDetails DD ON DD.fkUserNhi = U.nhi\n" +
                "  LEFT JOIN  (SELECT Address.fkUserNhi, Address.region from Address JOIN ContactDetails Detail ON Address.fkContactId = Detail.contactId\n" +
                "  WHERE Address.fkContactId NOT IN (SELECT EmergencyContactDetails.fkContactId FROM EmergencyContactDetails)) Q ON U.nhi = Q.fkUserNhi\n" +
                "  LEFT JOIN OrganAwaitingDates Dates ON awaitingId = Dates.fkAwaitingId\n" +
                "    WHERE Dates.dateDeregistered IS NULL AND ((U.firstName LIKE ? OR U.middleName LIKE ? OR U.lastName LIKE ?) AND (IFNULL(Q.region, '') LIKE ?)\n");
        if (organs != null && organs.length > 0) {
            queryString.append("AND O.organName IN(");

            queryString.append("'").append(organs[0]).append("'");
            for (int i = 1; i < organs.length; i++) {
                queryString.append(", ");
                queryString.append("'").append(organs[i]).append("'");
            }
            queryString.append(" )");
        }
        queryString.append(") LIMIT ? OFFSET ?");
        try (PreparedStatement stmt = conn.prepareStatement(queryString.toString())) {
            //first, middle, and last names
            stmt.setString(1, name + "%");
            stmt.setString(2, name + "%");
            stmt.setString(3, name + "%");
            stmt.setString(4, region + "%");

            stmt.setInt(5, count);
            stmt.setInt(6, startIndex);

            List<TransplantDetails> detailsList = new ArrayList<>();
            try (ResultSet results = stmt.executeQuery()) {
                while (results.next()) {
                    if (results.getTimestamp(8) == null) {
                        String nameBuilder = results.getString(2) +
                                " " +
                                results.getString(3) +
                                " " +
                                results.getString(4);
                        Organs selectedOrgan = Organs.valueOf(results.getString(5));
                        LocalDate dateRegistered = results.getDate(6).toLocalDate();
                        detailsList.add(new TransplantDetails(
                                results.getString(1),
                                nameBuilder,
                                selectedOrgan, dateRegistered, results.getString(7)));
                    }
                }
                return detailsList;
            }
        }
    }

    /**
     * Gets the user's profile photo on the database based on user's ID.
     *
     * @param <T> generic for type of the user
     * @param role user's role. e.g. Clinician.class
     * @param roleId id of user
     * @param connection connection to the target database
     * @return Profile Picture of type ImageStream if such user exists or has a profile picture, null otherwise.
     * @throws SQLException exception thrown during the transaction
     */
    public <T> byte[] getProfilePhoto(Class<T> role, String roleId, Connection connection) throws SQLException {
        String select_stmt;
        Blob profilePicture = null;

        if (role.isAssignableFrom(User.class)) {
            select_stmt = SELECT_USER_PROFILE_PHOTO_STMT;
        } else if (role.isAssignableFrom(Clinician.class)){
            select_stmt = SELECT_CLINICIAN_PROFILE_PHOTO_STMT;
        } else {
            throw new UnsupportedOperationException("Role does not support profile picture");
        }
        try (PreparedStatement selectProfilePhoto = connection.prepareStatement(select_stmt)) {
            selectProfilePhoto.setString(1, roleId);
            try (ResultSet resultSet = selectProfilePhoto.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    profilePicture = resultSet.getBlob("profilePicture");
                }
            }
        }
        if(profilePicture == null){
            return null;
        }
        return profilePicture.getBytes(1, (int) profilePicture.length());
    }

    /**
     * adds or updates user's profile photo on the database with the photo passed in.
     * pre-condition: user's profile photo, user id and type of user must be provided.
     * post-condition: adds or updates user's profile photo on database.
     *
     * @param role user's role. e.g. Clinician.class
     * @param roleId id of user
     * @param profilePhoto photo passed in
     * @param connection connection to the target database
     * @param <T> generic for type of the user
     * @throws SQLException exception thrown during the transaction
     */
    public <T> void updateProfilePhoto(Class<T> role, String roleId, InputStream profilePhoto, String imageType, Connection connection) throws SQLException {
        String update_stmt;

        if (role.isAssignableFrom(User.class)) {
            update_stmt = UPDATE_USER_PROFILE_PHOTO_STMT;
        } else if (role.isAssignableFrom(Clinician.class)){
            update_stmt = UPDATE_CLINICIAN_PROFILE_PHOTO_STMT;
        } else {
            throw new UnsupportedOperationException("Role does not support profile picture");
        }
        try (PreparedStatement updateProfilePhoto = connection.prepareStatement(update_stmt)) {
            updateProfilePhoto.setBlob(1, profilePhoto);
            updateProfilePhoto.setString(2, imageType);
            updateProfilePhoto.setString(3, roleId);
            updateProfilePhoto.executeUpdate();
        }
    }


    /**
     * queries the database as to whether an end-user* exists or not
     * * end-user meaning Admin, Clinician, or User
     *
     * @param conn       connection to the target database
     * @param type       type of the end-user as defined above
     * @param identifier username, (string version of a) staff id, or NHI
     *                   The staff id, while in string form must be a valid integer
     * @return true if the identifier can be found in the relevant table
     * @throws SQLException exception thrown during the transaction
     */
    public boolean getExists(Connection conn, Type type, String identifier) throws SQLException {
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
     * Uses the provided connection and queries data base of countries to retrieve the ones that are allowed to be used
     * as a place of residence.
     *
     * @param connection connection to the database
     * @return A set of countries
     * @throws SQLException Thrown on bad connection ot the database
     */
    public Set getAllowedCountries(Connection connection) throws SQLException {
        Set countries = new HashSet();
        try(PreparedStatement statement = connection.prepareStatement("SELECT countryName FROM Countries WHERE allowed = 1")){
            try(ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return countries;
                }
                do {
                    countries.add(rs.getString("countryName"));
                } while (rs.next());
            }
        }

        return countries;
    }

    /**
     *Puts the allowed countries onto the database
     *
     * @param connection connection to the database
     * @param countries set of countries to add
     * @throws SQLException thrown on invalid sql
     */
    public void putAllowedCountries(Connection connection, Set<String> countries) throws SQLException {
        String putStatment = "INSERT INTO Countries(countryName,allowed) VALUES (?,1)";
        try(PreparedStatement statement = connection.prepareStatement("DELETE FROM Countries")){
            statement.execute();
            for(String country : countries) {
                try (PreparedStatement put = connection.prepareStatement(putStatment)){
                    put.setString(1, country);
                    put.execute();
                }

            }
        }
    }

    /**
     * Gets the image type that an image was sent with
     * @param t type of role to query for
     * @param userId id of the wanted profile pictures owner
     * @param connection connection to the database
     * @return the content type header string
     * @throws SQLException on a bad db connection
     */
    public String getFormat(Type t, String userId, Connection connection) throws SQLException {

        String statement = null;

        if(t.equals(User.class)){
            statement = "SELECT pictureFormat FROM User WHERE nhi = ?";
        } else if (t.equals(Clinician.class)){
            statement = "SELECT pictureFormat FROM Clinician WHERE staffId = ?";
        }
        if( statement == null){
            return "";
        }
        try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
            preparedStatement.setString(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    return "";
                }
            }
        }
    }

    public List<String> runSqlQuery(String query, Connection connection) throws SQLException {
        List<String> results  = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()){
                    return results;
                }
                do {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    StringBuilder sb = new StringBuilder();
                    int columns = rsmd.getColumnCount();
                    for(int i = 1; i <= columns; i++){
                        String columnName = rs.getString(i);
                        sb.append(rsmd.getColumnName(i)).append(" ").append(columnName).append("\n");
                    }
                    results.add(sb.toString());
                } while (rs.next());
            }
        }
        return results;
    }

   public List<AvailableOrganDetail> getAvailableOrgans(int startIndex,
                                                        int count,
                                                        String organ,
                                                        String region,
                                                        String bloodType,
                                                        String city,
                                                        String country,
                                                        Connection connection) throws SQLException {
        List<AvailableOrganDetail> results = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVAILABLE_ORGANS)){
            preparedStatement.setString(1,bloodType + "%");
            preparedStatement.setString(2,organ + "%");
            preparedStatement.setString(3,region + "%");
            preparedStatement.setString(4,city + "%");
            preparedStatement.setString(5,country + "%");
            preparedStatement.setInt(6, count);
            preparedStatement.setInt(7,startIndex);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()) {
                    AvailableOrganDetail organDetail = new AvailableOrganDetail(Organs.valueOf(resultSet.getString("organName")),
                            resultSet.getString("fkUserNhi"),
                            resultSet.getTimestamp("momentOfDeath").toLocalDateTime(),
                            resultSet.getString("region"),
                            resultSet.getString("bloodType"));
                    if (organDetail.isOrganStillValid()) {
                        results.add(organDetail);
                    }
                }
            }

        }
        return results;

    }

    public void getDeathDetails(User user, Connection connection) throws  SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_DEATH_DETAILS_STMT)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    Timestamp momentOfDeath = resultSet.getTimestamp("momentOfDeath");
                    if (momentOfDeath != null) {
                        user.getDeathDetails().setMomentOfDeath(momentOfDeath.toLocalDateTime()); //FIX
                    } else {
                        user.getDeathDetails().setMomentOfDeath(null);
                    }
                    user.setDeathCity(resultSet.getString("city"));
                    user.setDeathRegion(resultSet.getString("region"));
                    user.setDeathCountry(resultSet.getString("country"));
                }
            }
        }
    }
}
