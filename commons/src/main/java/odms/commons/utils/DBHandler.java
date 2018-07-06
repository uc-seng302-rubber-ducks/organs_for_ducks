package odms.commons.utils;

import odms.commons.model.*;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.Medication;
import odms.commons.utils.dbStrategies.AbstractUpdateStrategy;
import odms.commons.utils.dbStrategies.AdminUpdateStrategy;
import odms.commons.utils.dbStrategies.ClinicianUpdateStrategy;
import odms.commons.utils.dbStrategies.UserUpdateStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

public class DBHandler {

    /**
     * SQL commands for select
     * SELECT_USER_ONE_TO_ONE_INFO_STMT is for getting all info that follows one-to-one relationship. eg: 1 user can only have 1 address.
     * the other SELECT_USER statements are for getting all info that follows one-to-many relationships. eg: 1 user can have many diseases.
     */
    private static final String SELECT_USER_ONE_TO_ONE_INFO_STMT_FILTERED = "SELECT nhi, firstName, middleName, LastName, preferedName, timeCreated, lastModified, profilePicture, gender, birthGender, smoker, " +
            "alcoholConsumption, height, weight, cde.homePhone, cde.cellPhone, cde.email, a.streetNumber, a.streetName, a.neighbourhood, a.city, " +
            "a.region, a.country, a.zipCode , contactName, contactRelationship, ecd.homePhone, ecd.cellPhone, ecd.email, ecd.streetNumber, ecd.streetName, ecd.neighbourhood, ecd.city, ecd.region, ecd.zipCode, ecd.country, " +
            "dob, dod " +
            "FROM User u " +
            "LEFT JOIN HealthDetails hd ON u.nhi = hd.fkUserNhi " +
            "LEFT JOIN ContactDetails cde ON u.nhi = cde.fkUserNhi " +
            "LEFT JOIN Address a ON u.nhi = a.fkUserNhi " +
            "LEFT JOIN EmergencyContactDetails ecd ON u.nhi = ecd.fkUserNhi " +
            "WHERE (firstName LIKE ? OR lastName LIKE ?) AND a.region LIKE ? " +
            "LIMIT ? OFFSET ?";
    private static final String SELECT_USER_ONE_TO_ONE_INFO_STMT = "SELECT nhi, firstName, middleName, LastName, preferedName, timeCreated, lastModified, profilePicture, gender, birthGender, smoker, " +
            "alcoholConsumption, height, weight, cde.homePhone, cde.cellPhone, cde.email, a.streetNumber, a.streetName, a.neighbourhood, a.city, " +
            "a.region, a.country, a.zipCode , contactName, contactRelationship, ecd.homePhone, ecd.cellPhone, ecd.email, ecd.streetNumber, ecd.streetName, ecd.neighbourhood, ecd.city, ecd.region, ecd.zipCode, ecd.country, " +
            "dob, dod " +
            "FROM User u " +
            "LEFT JOIN HealthDetails hd ON u.nhi = hd.fkUserNhi " +
            "LEFT JOIN ContactDetails cde ON u.nhi = cde.fkUserNhi " +
            "LEFT JOIN Address a ON u.nhi = a.fkUserNhi " +
            "LEFT JOIN EmergencyContactDetails ecd ON u.nhi = ecd.fkUserNhi";
    private static final String SELECT_USER_PREVIOUS_DISEASE_STMT = "SELECT diseaseName, diagnosisDate, remissionDate FROM PreviousDisease WHERE fkUserNhi = ?";
    private static final String SELECT_USER_CURRENT_DISEASE_STMT = "SELECT diseaseName, diagnosisDate, isChronic FROM CurrentDisease WHERE fkUserNhi = ?";
    private static final String SELECT_USER_MEDICATION_STMT = "SELECT medicationName, dateStartedTaking, dateStoppedTaking FROM Medication m " +
            "LEFT JOIN MedicationDates md ON m.medicationInstanceId = md.fkMedicationInstanceId " +
            "WHERE m.fkUserNhi = ?";
    private static final String SELECT_USER_MEDICAL_PROCEDURE_STMT = "SELECT procedureName, procedureDate, procedureDescription, organName, mp.fkUserNhi FROM MedicalProcedure mp " +
            "LEFT JOIN MedicalProcedureOrgan mpo ON mpo.fkUserNhi = mp.fkUserNhi " +
            "LEFT JOIN Organ o on o.organId = mpo.fkOrgansId " +
            "WHERE mp.fkUserNhi = ?";
    private static final String SELECT_USER_ORGAN_DONATION = "SELECT organName FROM OrganDonating LEFT JOIN Organ ON fkOrgansId = organId WHERE fkUserNhi = ?";
    private static final String SELECT_USER_ORGAN_RECEIVING = "SELECT organName FROM OrganAwaiting LEFT JOIN Organ ON fkOrgansId = organId WHERE fkUserNhi = ?";
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
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            return preparedStatement.executeQuery();
        } catch (SQLException e){
            throw e;
        }
    }


    /**
     * Helper function to convert datetime string from database
     * to LocalDateTime object.
     *
     * @param dateTime date string from database
     * @return LocalDateTime object
     */
    private LocalDateTime dateTimeToLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return LocalDateTime.parse(dateTime, formatter);
    }

    /**
     * Helper function to convert date string from database
     * to LocalDateTime object.
     *
     * @param date date string from database
     * @return LocalDateTime object
     */
    private LocalDate dateToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
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

            }
        }
        return null; //TODO: implement this
    }

    /**
     * Method to obtain all the users information from the database.
     * User objects are instantiated and its attributes are set based on the de-serialised information.
     *
     * @param connection A valid connection to the database
     * @return a Collection of Users
     * @throws SQLException if there are any SQL errors
     */
    public Collection<User> loadUsers(Connection connection) throws SQLException {
        Collection<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_ONE_TO_ONE_INFO_STMT)) {
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet != null && resultSet.next()) {
                    User user = new User(resultSet.getString(2), dateToLocalDateTime(resultSet.getString("dob")), resultSet.getString(1));
                    if(resultSet.getString("dod") != null){
                        user.setDateOfDeath(dateToLocalDateTime(resultSet.getString("dod")));
                    }
                    user.setMiddleName(resultSet.getString(3));
                    user.setLastName(resultSet.getString(4));
                    user.setPreferredFirstName(resultSet.getString(5));
                    user.setTimeCreated(dateTimeToLocalDateTime(resultSet.getString(6)));
                    user.setLastModified(dateTimeToLocalDateTime(resultSet.getString(7)));
                    //TODO: set user's profile picture here
                    user.setGenderIdentity(resultSet.getString(9));
                    user.setBirthGender(resultSet.getString(10));
                    user.setSmoker(1 == resultSet.getInt(11));
                    user.setAlcoholConsumption(resultSet.getString(12));
                    user.setHeight(resultSet.getDouble(13));
                    user.setWeight(resultSet.getDouble(14));
                    user.setHomePhone(resultSet.getString(15));
                    user.setCellPhone(resultSet.getString(16));
                    user.setEmail(resultSet.getString(17));
                    user.setStreetNumber(resultSet.getString(18));
                    user.setStreetName(resultSet.getString(19));
                    user.setNeighborhood(resultSet.getString(20));
                    user.setCity(resultSet.getString(21));
                    user.setRegion(resultSet.getString(22));
                    user.setCountry(resultSet.getString(23));
                    user.setZipCode(resultSet.getString(24));
                    user.getContact().setName(resultSet.getString(25));
                    user.getContact().setRelationship(resultSet.getString(26));
                    user.getContact().setHomePhoneNumber(resultSet.getString(27));
                    user.getContact().setCellPhoneNumber(resultSet.getString(28));
                    user.getContact().setEmail(resultSet.getString(29));
                    user.getContact().setStreetNumber(resultSet.getString(30));
                    user.getContact().setStreetName(resultSet.getString(31));
                    user.getContact().setNeighborhood(resultSet.getString(32));
                    user.getContact().setCity(resultSet.getString(33));
                    user.getContact().setRegion(resultSet.getString(34));
                    user.getContact().setZipCode(resultSet.getString(35));
                    user.getContact().setCountry(resultSet.getString(36));

                    try {
                        getUserPastDisease(user, connection);
                        getUserCurrentDisease(user, connection);
                        getUserMedication(user, connection);
                        getUserMedicalProcedure(user, connection);
                        getUserOrganDonateDetail(user, connection);
                        //getUserOrganReceiveDetail(user, connection);
                        users.add(user);
                    } catch (SQLException e) {
                        Log.warning("Unable to create instance of user with nhi " + user.getNhi(), e);
                        System.err.println("Unable to create instance of user with nhi " + user.getNhi() + " due to SQL Error: " + e);
                    }
                }
                return users;
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
                    Disease pastDisease = new Disease(resultSet.getString(1), false, true, dateTimeToLocalDateTime(resultSet.getString(2)).toLocalDate());
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
                    Disease currentDisease = new Disease(resultSet.getString(1), 1 == resultSet.getInt(3), false, dateTimeToLocalDateTime(resultSet.getString(2)).toLocalDate());
                    user.getCurrentDiseases().add(currentDisease);
                }
            }
        }
    }

    /**
     * gets all info of organs the user is donating.
     * Then, the organ data de-serialised and added to User object.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserOrganDonateDetail(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_ORGAN_DONATION)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    user.getDonorDetails().addOrgan(Organs.valueOf(resultSet.getString(1)));
                }
            }
        }
    }

    /**
     * gets all info of organs the user is receiving.
     * Then, the organ data de-serialised and added to User object.
     *
     * @param user       desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserOrganReceiveDetail(User user, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_USER_ORGAN_RECEIVING)) {
            stmt.setString(1, user.getNhi());
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    //user.getReceiverDetails().setOrgans(Organs.valueOf(resultSet.getString(1))); //TODO: db is not storing any info of ReceiverOrganDetailsHolder class atm.
                }
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
                    medication.addMedicationTime(dateTimeToLocalDateTime(resultSet.getString("dateStartedTaking")));

                    if(resultSet.getString("dateStoppedTaking") == null) {
                        int duplicateIndex = user.getPreviousMedication().indexOf(medication);
                        if( duplicateIndex != -1){ //if the medication instance already exist in this user's previousMedication attribute. This scenario happens when a user is currently taking a medication that had been taken before.
                            medication = user.getPreviousMedication().get(duplicateIndex);
                            medication.addMedicationTime(dateTimeToLocalDateTime(resultSet.getString("dateStartedTaking"))); //updates the medication time array
                            user.getPreviousMedication().remove(medication);
                            user.getCurrentMedication().add(medication);

                        } else {
                            user.getCurrentMedication().add(medication);
                        }

                    } else {
                        medication.addMedicationTime(dateTimeToLocalDateTime(resultSet.getString("dateStoppedTaking")));
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
                    if (medicalProcedure != null && resultSet.getString(5).equals(user.getNhi())) { //if the data of next result set belongs to the same user, that means multiple organs is affected.
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
                    clinician.setDateCreated(dateTimeToLocalDateTime(resultSet.getString(5)));
                    clinician.setDateLastModified(dateTimeToLocalDateTime(resultSet.getString(6)));
                    //TODO: for de-serialising address data, should we refactor clinician to use Address class or use workAddress class attribute.
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
                    administrator.setDateCreated(dateTimeToLocalDateTime(resultSet.getString(5)));
                    administrator.setDateLastModified(dateTimeToLocalDateTime(resultSet.getString(6)));
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
//        dbHandler.loadUsers(jdbcDriver.getTestConnection());
//        //dbHandler.loadClinicians(jdbcDriver.getTestConnection());
//        //dbHandler.loadAdmins(jdbcDriver.getTestConnection());
//        System.out.println("done");
//    }
}
