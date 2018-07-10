package odms.commons.utils;

import odms.commons.model.*;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.Medication;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;
import odms.commons.utils.db_strategies.AbstractUpdateStrategy;
import odms.commons.utils.db_strategies.AdminUpdateStrategy;
import odms.commons.utils.db_strategies.ClinicianUpdateStrategy;
import odms.commons.utils.db_strategies.UserUpdateStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class DBHandler {

    /**
     * SQL commands for select
     * SELECT_USER_ONE_TO_ONE_INFO_STMT is for getting all info that follows one-to-one relationship. eg: 1 user can only have 1 address.
     * the other SELECT_USER statements are for getting all info that follows one-to-many relationships. eg: 1 user can have many diseases.
     */
    private static final String SELECT_USER_ONE_TO_ONE_INFO_STMT_FILTERED = "SELECT nhi FROM `User` a " +
            "WHERE (firstName LIKE ? OR lastName LIKE ?) AND a.region LIKE ? " +
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
            "LEFT JOIN Organ o on o.organId = mpo.fkOrgansId " +
            "WHERE mp.fkUserNhi = ? " +
            "ORDER BY procedureDate";
    private static final String SELECT_USER_ORGAN_DONATING_STMT = "SELECT organName FROM OrganDonating LEFT JOIN Organ ON fkOrgansId = organId WHERE fkUserNhi = ?";
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
            "streetNumber, streetName, neighbourhood, city, region, country " +
            "FROM Clinician cl " +
            "LEFT JOIN Address a ON cl.staffId = a.fkStaffId " +
            "WHERE firstName LIKE ? OR lastName LIKE ? AND region LIKE ? " +
            "LIMIT ? OFFSET ?";
    private static final String SELECT_ADMIN_ONE_TO_ONE_INFO_STMT = "SELECT userName, firstName, middleName, lastName, timeCreated, lastModified  FROM Administrator";
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
        try{
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                return preparedStatement.executeQuery();
            }
        } catch (SQLException e){
            Log.severe("Exception in executing statement " + statement, e);
            throw e;
        }
    }

    /**
     * Method with less filtering parameters to obtain all the users information from the database based on filtering provided.
     * User objects are instantiated and its attributes are set based on the de-serialised information.
     * @param connection A valid connection to the database
     * @param count number of items returned
     * @param startIndex number of items to skip
     * @return a Collection of Users
     * @throws SQLException if there are any SQL errors
     */
    public Collection<User> getUsers(Connection connection, int count, int startIndex) throws SQLException {
        return this.getUsers(connection, count, startIndex, "", "");
    }

    /**
     * Method to obtain all the users information from the database based on filtering provided.
     * User objects are instantiated and its attributes are set based on the de-serialised information.
     * @param connection A valid connection to the database
     * @param count number of items returned
     * @param startIndex number of items to skip
     * @param name name of a user
     * @param region region of a user
     * @return a Collection of Users
     * @throws SQLException if there are any SQL errors
     */
    public Collection<User> getUsers(Connection connection, int count, int startIndex, String name, String region) throws SQLException {
        Collection<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_ONE_TO_ONE_INFO_STMT_FILTERED)) {
            statement.setString(1, name + "%");
            statement.setString(2, name + "%");
            statement.setString(3, region + "%");
            statement.setInt(4, count);
            statement.setInt(5, startIndex);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(getOneUser(connection, resultSet.getString("nhi")));
                }
            }
        }
        return users;
    }

    /**
     * Gets info of a single user based on user NHI provided
     * @param connection A valid connection to the database
     * @param nhi user's NHI
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
                    } catch (SQLException e) {
                        Log.warning("Unable to create instance of user with nhi " + user.getNhi(), e);
                        System.err.println("Unable to create instance of user with nhi " + user.getNhi() + " due to SQL Error: " + e);
                    }
                }
            }
        }

        return user;
    }

    /**
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
                    user.setStreetNumber(resultSet.getString("streetNumber"));
                    user.setStreetName(resultSet.getString("streetName"));
                    user.setNeighborhood(resultSet.getString("neighbourhood"));
                    user.setCity(resultSet.getString("city"));
                    user.setRegion(resultSet.getString("region"));
                    user.setCountry(resultSet.getString("country"));
                    user.setZipCode(resultSet.getString("zipCode"));
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
                    user.getContact().setStreetNumber(resultSet.getString("streetNumber"));
                    user.getContact().setStreetName(resultSet.getString("streetName"));
                    user.getContact().setNeighborhood(resultSet.getString("neighbourhood"));
                    user.getContact().setCity(resultSet.getString("city"));
                    user.getContact().setRegion(resultSet.getString("region"));
                    user.getContact().setZipCode(resultSet.getString("zipCode"));
                    user.getContact().setCountry(resultSet.getString("country"));
                }
            }
        }
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
                    user.getDonorDetails().addOrgan(Organs.valueOf(resultSet.getString("organName")));
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

                    if (resultSet.getTimestamp("dateStoppedTaking").toLocalDateTime() == null) {
                        int medicationInstanceIndex = user.getPreviousMedication().indexOf(medication);
                        if( medicationInstanceIndex != -1){ //if the medication instance already exist in this user's previousMedication attribute. This scenario happens when a user is currently taking a medication that had been taken before.
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
     * Loads the clinicians from the database.
     *
     * @param connection a Connection to the target database
     * @return the Collection of clinicians
     */
    public Collection<Clinician> loadClinicians(Connection connection) throws SQLException {
        Collection<Clinician> clinicians = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_CLINICIAN_ONE_TO_ONE_INFO_STMT)) {

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet != null && resultSet.next()) {
                    Clinician clinician = new Clinician();
                    clinician.setStaffId(resultSet.getString(1));
                    clinician.setFirstName(resultSet.getString(2));
                    clinician.setMiddleName(resultSet.getString(3));
                    clinician.setLastName(resultSet.getString(4));
                    clinician.setDateCreated(resultSet.getTimestamp(5).toLocalDateTime());
                    clinician.setDateLastModified(resultSet.getTimestamp(6).toLocalDateTime());
                    //TODO: implement addressStrategy for clinicians (same functionality as getUserAddress method)
                    clinicians.add(clinician);
                }

                return clinicians;
            }
        }
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
     * @return the Collection of administrators
     */
    public Collection<Administrator> loadAdmins(Connection connection) throws SQLException {
        Collection<Administrator> administrators = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ADMIN_ONE_TO_ONE_INFO_STMT)) {
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet != null && resultSet.next()) {
                    Administrator administrator = new Administrator();
                    administrator.setUserName(resultSet.getString(1));
                    administrator.setFirstName(resultSet.getString(2));
                    administrator.setMiddleName(resultSet.getString(3));
                    administrator.setLastName(resultSet.getString(4));
                    administrator.setDateCreated(resultSet.getTimestamp(5).toLocalDateTime());
                    administrator.setDateLastModified(resultSet.getTimestamp(6).toLocalDateTime());
                    administrators.add(administrator);
                }

                return administrators;
            }
        }
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

    //TODO: Please dont remove this main until db handler is fully developed
//    public static void main(String [ ] args) throws SQLException{
//        DBHandler dbHandler = new DBHandler();
//        JDBCDriver jdbcDriver = new JDBCDriver();
//        User user = dbHandler.getOneUser(jdbcDriver.getTestConnection(), "ABC1234");
////        Collection<User>users = dbHandler.loadUsers(jdbcDriver.getTestConnection());
////        System.out.println();
////        for (User user : users) {
////            System.out.println(user.getNhi()+" "+ user.getReceiverDetails().getOrgans().size());
////            if(user.getReceiverDetails().getOrgans().get(Organs.PANCREAS)!= null) {
////                for (ReceiverOrganDetailsHolder r :
////                        user.getReceiverDetails().getOrgans().get(Organs.PANCREAS)) {
////                    System.out.println(r.getStartDate());
////                }
////            }
////        }
//        //dbHandler.loadClinicians(jdbcDriver.getTestConnection());
//        //dbHandler.loadAdmins(jdbcDriver.getTestConnection());
//        System.out.println(user.getMiddleName());
//    }
}
