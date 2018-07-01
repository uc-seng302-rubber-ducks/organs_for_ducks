package seng302.utils;

//import com.mysql.jdbc.PreparedStatement;

import seng302.model.*;
import seng302.model._enum.Organs;

import java.io.InvalidClassException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

public class DBHandler {

    /**
     * SQL commands for executing creates
     */
    //<editor-fold desc="INSERT Queries">
    private static final String CREATE_USER_STMT = "INSERT INTO User (nhi, firstName, middleName, lastName, preferedName, dob, dod, timeCreated, lastModified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_USER_CONTACT_STMT = "INSERT INTO ContactDetails (fkUserNhi, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_STAFF_CONTACT_STMT = "INSERT INTO ContactDetails (fkStaffId, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_CLINICIAN_STMT = "INSERT INTO Clinician (staffId, firstName, middleName, lastName) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADMIN_STMT = "INSERT INTO Administrator (username, firstName, middleName, lastName) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADDRESS_STMT = "INSERT INTO Address (fkContactId, streetNumber, streetName, neighbourhood, city, region, country) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_HEALTH_DETAILS = "INSERT INTO HealthDetails (fkUserNhi, gender, birthGender, smoker, alcoholConsumption, height, weight) VALUES (?, ?, ?, ?, ?, ?, ?)"; // TODO: Include blood type 24/6 - Eiran
    private static final String CREATE_EMERGENCY_STMT = "INSERT INTO EmergencyContactDetails (fkContactId, contactName, contactRelationship) VALUES (?, ?, ?)";
    private static final String GET_LATEST_CONTACT_ENTRY = "SELECT MAX(contactId) AS contactId FROM ContactDetails WHERE fkUserNhi=?";
    private static final String CREATE_NEW_MEDICATION = "INSERT INTO Medication (fkUserNhi, medicationName) VALUES (?, ?)";
    private static final String CREATE_NEW_PROCEDURE = "INSERT INTO Procedure (fkUserNhi, procedureName, procedureDescription, procedureDate) VALUES (?, ?, ?)";
    //</editor-fold>

    /**
     * SQL commands for select
     * SELECT_USER_ONE_TO_ONE_INFO_STMT is for getting all info that follows one-to-one relationship. eg: 1 user can only have 1 address.
     * the other SELECT_USER statements are for getting all info that follows one-to-many relationships. eg: 1 user can have many diseases.
     */
    private static final String SELECT_USER_ONE_TO_ONE_INFO_STMT = "SELECT nhi, firstName, middleName, LastName, preferedName, timeCreated, lastModified, profilePicture, gender, birthGender, " +
            "smoker, alcoholConsumption, height, weight, homePhone, cellPhone, email, streetNumber, streetName, neighbourhood, city, region, country " +
            "FROM User u " +
            "LEFT JOIN HealthDetails hd ON u.nhi = hd.fkUserNhi " +
            "LEFT JOIN ContactDetails cde ON u.nhi = cde.fkUserNhi " +
            "LEFT JOIN Address a ON u.nhi = a.fkUserNhi";

    private static final String SELECT_USER__PREVIOUS_DISEASE_STMT = "SELECT diseaseName, diagnosisDate, remissionDate FROM PreviousDisease WHERE fkUserNhi = ?";
    private static final String SELECT_USER__CURRENT_DISEASE_STMT = "SELECT diseaseName, diagnosisDate, isChronic FROM CurrentDisease WHERE fkUserNhi = ?";
    private static final String SELECT_USER_MEDICATION_STMT = "SELECT medicationName, dateStartedTaking, dateStoppedTaking FROM Medication m " +
            "LEFT JOIN MedicationDates md ON m.medicationInstanceId = md.fkMedicationInstanceId " +
            "WHERE m.fkUserNhi = ?";

    private static final String SELECT_USER_MEDICAL_PROCEDURE_STMT = "SELECT procedureName, procedureDate, procedureDescription, organName, mp.fkUserNhi FROM MedicalProcedure mp " +
            "LEFT JOIN MedicalProcedureOrgan mpo ON mpo.fkUserNhi = mp.fkUserNhi " +
            "LEFT JOIN Organ o on o.organId = mpo.organsId " +
            "WHERE mp.fkUserNhi = ?";

    private static final String SELECT_USER_ORGAN_DONATION = "SELECT organName FROM HealthOrganDonate LEFT JOIN Organ ON fkOrgansId = organId WHERE fkUserNhi = ?";
    private static final String SELECT_USER_ORGAN_RECEIVING = "SELECT organName FROM HealthOrganReceive LEFT JOIN Organ ON fkOrgansId = organId WHERE fkUserNhi = ?";


    private static final String SELECT_CLINICIAN_ONE_TO_ONE_INFO_STMT = "SELECT staffId, firstName, middleName, lastName, timeCreated, lastModified, " +
            "streetNumber, streetName, neighbourhood, city, region, country " +
            "FROM Clinician cl " +
            "LEFT JOIN Address a ON cl.staffId = a.fkStaffId ";

    private static final String SELECT_ADMIN_ONE_TO_ONE_INFO_STMT = "SELECT userName, firstName, middleName, lastName, timeCreated, lastModified  FROM Administrator";

    /**
     * SQL Queries for updates
     */
    private static final String UPDATE_USER_STMT = "UPDATE User SET nhi = ?, firstName = ?, middleName = ?, lastName = ?, preferredName = ?, dob = ?, dod = ?, lastModified = ? WHERE nhi = ?";
    private static final String UPDATE_ADDRESS = "UPDATE Address SET streetNumber = ?, streetName = ?, neighbourhood = ?, city = ?, region = ?, country = ? WHERE fkContactId = ? AND fkUserNhi = ? AND fkStaffId = ?";
    private static final String UPDATE_USER_CONTACT_STMT = "UPDATE ContactDetails SET homePhone = ?, email = ?, cellPhone = ? WHERE fkUserNhi = ? AND contactId = ?"; // make this more generic?
    private static final String UPDATE_EC_STMT = "UPDATE EmergencyContactDetails SET contactName = ?, contactRelationship = ? WHERE fkContactId = ?";
    private static final String UPDATE_USER_HEALTH_STMT = "UPDATE HealthDetails SET gender = ?, birthGender = ?, smoker = ?, alcoholConsumption = ?, height = ?, weight = ? WHERE fkUserNhi = ?";
    private static final String UPDATE_MEDICATION_DATE_STMT = "UPDATE MedicationDates SET dateStartedTaking = ?, dateStoppedTaking = ? WHERE fkMedicationInstanceId = ?";
    private static final String UPDATE_PROCEDURE_STMT = "UPDATE MedicalProcedure SET procedureName = ?, procedureDate = ?, procedureDescription = ? WHERE fkUserNhi = ?"; // needs an id
    private static final String UPDATE_CLINICIAN_STMT = "UPDATE Clinician SET staffId = ?, firstName = ?, middleName = ?, lastName = ?, lastModified = ? WHERE staffId = ?"; // make generic to work for both admin and clinician?
    private static final String UPDATE_ADMIN_STMT = "UPDATE Admin SET username = ?, firstName = ?, middleName = ?, lastName = ?, lastModified = ? WHERE username = ?";
    private static final String UPDATE_PASSWORD = ""; // make generic?

    /**
     * SQL Queries for deletes
     */
    private static final String DELETE_USER_STMT = "DELETE FROM User WHERE nhi = ?";
    private static final String DELETE_PROCEDURE_STMT = "DELETE FROM MedicalProcedure WHERE procedureDate = ? AND procedureName = ? AND fkUserNhi = ?";
    private static final String DELETE_CLINICIAN_STMT = "DELETE FROM Clinician WHERE staffId = ?";
    private static final String DELETE_ADMIN_STMT = "DELETE FROM Administrators WHERE username = ?";
    private static final String DELETE_USER_DISEASE_STMT = "DELETE FROM CurrentDisease WHERE diseaseName = ? AND diagnosisDate = ? AND fkUserNhi = ?";
    private static final String DELETE_PAST_DISEASE_STMT = "DELETE FROM PreviousDisease WHERE diseaseName = ? AND diagnosisDate = ? AND fkUserNhi = ?";
    private static final String DELETE_MEDICATION_STMT = "DELETE FROM Medication WHERE medicationName = ? AND fkUserNhi = ?";



    /**
     * Helper function to convert date string from database
     * to LocalDateTime object.
     * @param date date string from database
     * @return LocalDateTime object
     */
    private LocalDateTime dateToLocalDateTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * Method to obtain all the users information from the database. Opens and closes it's own connection to the database
     * User objects are instantiated and its attributes are set based on the de-serialised information.
     *
     * @param connection A valid connection to the database
     * @return a Collection of Users
     */
    public Collection<User> getAllUsers(Connection connection) throws SQLException {
        Collection<User> users = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement(SELECT_USER_ONE_TO_ONE_INFO_STMT);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet != null && resultSet.next()) {
            User user = new User();
            user.setNhi(resultSet.getString(1));
            user.setFirstName(resultSet.getString(2));
            user.setMiddleName(resultSet.getString(3));
            user.setLastName(resultSet.getString(4));
            user.setPreferredFirstName(resultSet.getString(5));
            user.setTimeCreated(dateToLocalDateTime(resultSet.getString(6)));
            user.setLastModified(dateToLocalDateTime(resultSet.getString(7)));
            //TODO: set user's profile picture here
            user.setGenderIdentity(resultSet.getString(9));
            user.setBirthGender(resultSet.getString(10));
            user.setSmoker(1==resultSet.getInt(11));
            user.setAlcoholConsumption(resultSet.getString(12));
            user.setHeight(resultSet.getDouble(13));
            user.setWeight(resultSet.getDouble(14));
            user.setHomePhone(resultSet.getString(15));
            user.setCellPhone(resultSet.getString(16));
            user.setEmail(resultSet.getString(17));

            //TODO: need to get an understanding of how the sample data for  Address, ContactDetails and EmergencyContactDetails db tables before i can de-serialise emergency contact details (Aaron)
//            user.getContact().setEmail();
//            user.getContact().setHomePhoneNumber();
//            user.getContact().setCellPhoneNumber();
//            user.getContact().setName();
//            user.getContact().setAddress();
//            user.getContact().setRelationship();

//            user.setStreetNumber(resultSet.getString(resultSet.getString(18))); //todo: change db address table's streetnumber column to varchar type
            user.setStreetName(resultSet.getString(19));
            user.setNeighborhood(resultSet.getString(20));
            user.setCity(resultSet.getString(21));
            user.setRegion(resultSet.getString(22));
            user.setCountry(resultSet.getString(23));
//            user.setZipCode(resultSet.getString()); //todo: db address table needs to have zip table
            user.getReceiverDetails();

            try {
                getUserPastDisease(user, connection);
                getUserCurrentDisease(user, connection);
                //getUserMedication(user, connection);
                getUserMedicalProcedure(user, connection);
                getUserOrganDonateDetail(user, connection);
                //getUserOrganReceiveDetail(user, connection);
                users.add(user);
            }
            catch (SQLException e) {
                Log.warning("Unable to create instance of user with nhi "+user.getNhi(), e);
                System.err.println("Unable to create instance of user with nhi "+user.getNhi()+" due to SQL Error: "+e);
            }
        }
        connection.close();
        return users;
    }

    /**
     * gets all info of past diseases of a user.
     * Then, Disease objects are instantiated and its attributes are set based on the de-serialised information.
     * Finally, the Disease objects are added to User object.
     * @param user desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserPastDisease(User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(SELECT_USER__PREVIOUS_DISEASE_STMT);
        stmt.setString(1, user.getNhi());
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet != null && resultSet.next()) { //TODO: how do we de-serialise remission date?
            Disease pastDisease = new Disease(resultSet.getString(1), false, true, dateToLocalDateTime(resultSet.getString(2)).toLocalDate());
            user.getPastDiseases().add(pastDisease);
        }
    }

    /**
     * gets all info of current diseases of a user.
     * Then, Disease objects are instantiated and its attributes are set based on the de-serialised information.
     * Finally, the Disease objects are added to User object.
     * @param user desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserCurrentDisease(User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(SELECT_USER__CURRENT_DISEASE_STMT);
        stmt.setString(1, user.getNhi());
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet != null && resultSet.next()) {
            Disease currentDisease = new Disease(resultSet.getString(1), 1==resultSet.getInt(3), false, dateToLocalDateTime(resultSet.getString(2)).toLocalDate());
            user.getCurrentDiseases().add(currentDisease);
        }
    }

    /**
     * gets all info of organs the user is donating.
     * Then, the organ data de-serialised and added to User object.
     * @param user desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserOrganDonateDetail(User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(SELECT_USER_ORGAN_DONATION);
        stmt.setString(1, user.getNhi());
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet != null && resultSet.next()) {
            user.getDonorDetails().addOrgan(Organs.valueOf(resultSet.getString(1)));
        }
    }

    /**
     * gets all info of organs the user is receiving.
     * Then, the organ data de-serialised and added to User object.
     * @param user desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserOrganReceiveDetail(User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(SELECT_USER_ORGAN_RECEIVING);
        stmt.setString(1, user.getNhi());
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet != null && resultSet.next()) {
            //user.getReceiverDetails().setOrgans(Organs.valueOf(resultSet.getString(1))); //TODO: db is not storing any info of ReceiverOrganDetailsHolder class atm.
        }
    }

    /**
     * gets all info of Medication of a user
     *
     * TODO: update javadoc once method is completed
     * @param user desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserMedication(User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(SELECT_USER_MEDICATION_STMT);
        stmt.setString(1, user.getNhi());
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet != null && resultSet.next()) {
            if(resultSet.getString(3) == null){ // dateStoppedTaking is null
                //TODO: create method or medication class to deserealise name and date of current medication

            } else {
                //TODO: create method or medication class to deserealise name and date of previous medication
            }
        }
    }

    /**
     * gets all info of Medical procedure of a user
     * Then, MedicalProcedure objects are instantiated and its attributes are set based on the de-serialised information.
     * Finally, the MedicalProcedure objects are added to User object.
     * @param user desired user
     * @param connection opened connection to database
     * @throws SQLException when there are any SQL errors
     */
    private void getUserMedicalProcedure(User user, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(SELECT_USER_MEDICAL_PROCEDURE_STMT);
        stmt.setString(1, user.getNhi());
        ResultSet resultSet = stmt.executeQuery();
        MedicalProcedure medicalProcedure = null;
        while (resultSet != null && resultSet.next()) {
            if (medicalProcedure != null && resultSet.getString(5).equals(user.getNhi())){ //if the data of next result set belongs to the same user, that means multiple organs is affected.
                medicalProcedure.addOrgan(Organs.valueOf(resultSet.getString(4)));

            } else {
                medicalProcedure = new MedicalProcedure(resultSet.getDate(2).toLocalDate(), resultSet.getString(1), resultSet.getString(3), null);
                medicalProcedure.addOrgan(Organs.valueOf(resultSet.getString(4)));
                user.getMedicalProcedures().add(medicalProcedure);
            }
        }
    }

    /**
     * Method to save all the users to the database.
     *
     * @param connection connection to the database to be accessed
     * @param users A non null collection of users to save to the database
     */
    public void saveUsers(Collection<User> users, Connection connection) {
        try {
            updateDatabase(users, connection);
        } catch (InvalidClassException invalidEx) {
            //Should never happen, but if it does, system failure
            invalidEx.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Loads the clinicians from the database. Opens and closes its own connection to the database
     *
     * @param connection a Connection to the target database
     * @return the Collection of clinicians
     */
    public Collection<Clinician> loadClinicians(Connection connection) throws SQLException {
        Collection<Clinician> clinicians = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement(SELECT_CLINICIAN_ONE_TO_ONE_INFO_STMT);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet != null && resultSet.next()) {
            Clinician clinician = new Clinician();
            clinician.setStaffId(resultSet.getString(1));
            clinician.setFirstName(resultSet.getString(2));
            clinician.setMiddleName(resultSet.getString(3));
            clinician.setLastName(resultSet.getString(4));
            clinician.setDateCreated(dateToLocalDateTime(resultSet.getString(5)));
            clinician.setDateLastModified(dateToLocalDateTime(resultSet.getString(6)));
            //TODO: for de-serialising address data, should we refactor clinician to use Address class or use workAddress class attribute.
            //clinician.setPassword(resultSet.getString(10)); //TODO since the database stores the hash and salt, do we store those directly without having to use setPassword?
            clinicians.add(clinician);
        }

        connection.close();
        return clinicians;
    }

    /**
     * Updates the clinicians stored in active memory.
     *
     * @param clinicians Collection of clinicians to update.
     * @param connection connection to the targeted database
     */
    public void saveClinicians(Collection<Clinician> clinicians, Connection connection) {
        try {
            updateDatabase(clinicians, connection);
        } catch (InvalidClassException invalidEx) {
            //Should never happen, but if it does, system failure
            invalidEx.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Loads the administrators from the database. Opens and closes its own connection to the database
     *
     * @param connection Connection to the target database
     * @return the Collection of administrators
     */
    public Collection<Administrator> loadAdmins(Connection connection) throws SQLException {
        Collection<Administrator> administrators = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement(SELECT_ADMIN_ONE_TO_ONE_INFO_STMT);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet != null && resultSet.next()) {
            Administrator administrator = new Administrator();
            administrator.setUserName(resultSet.getString(1));
            administrator.setFirstName(resultSet.getString(2));
            administrator.setMiddleName(resultSet.getString(3));
            administrator.setLastName(resultSet.getString(4));
            administrator.setDateCreated(dateToLocalDateTime(resultSet.getString(5)));
            administrator.setDateLastModified(dateToLocalDateTime(resultSet.getString(6)));
            //administrator.setPassword(resultSet.getString(10)); //TODO since the database stores the hash and salt, do we store those directly without having to use setPassword?
            administrators.add(administrator);
        }

        connection.close();
        return administrators;
    }

    /**
     * Updates the administrators stored in active memory.
     *
     * @param administrators Collection of admins to update.
     * @param connection Connection to the target database
     */
    public void saveAdministrators(Collection<Administrator> administrators, Connection connection) {
        try {
            updateDatabase(administrators, connection);
        } catch (InvalidClassException invalidEx) {
            //Should never happen, but if it does, system failure
            invalidEx.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Executes an update for each of items in the collection. The Collection must be of a type User, Clinician or Administrator
     *
     * @param collection collection of objects to update the database with
     * @param connection Connection to the target database
     * @param <T>        User, Clinician or Administrator
     * @throws InvalidClassException if the collection does not hold Users, Clinicians or Administrators
     */
    private <T> void updateDatabase(Collection<T> collection, Connection connection) throws InvalidClassException {
        String identifier;
        String identifierName;
        String table;
        boolean toDelete; // Flag that marks whether the current role is to be deleted.
        try {
            for (T object : collection) {

                if (object instanceof Administrator) {
                    Administrator admin = (Administrator) object;
                    if (admin.getChanges().size() <= 0) {
                        continue;
                    }
                    table = "Administrator";
                    identifierName = "userName";
                    identifier = admin.getUserName();
                    toDelete = admin.isDeleted();
                } else if (object instanceof Clinician) {
                    Clinician clinician = (Clinician) object;
                    if (clinician.getChanges().size() <= 0) {
                        continue;
                    }
                    table = "Clinician";
                    identifierName = "staffId";
                    identifier = clinician.getStaffId();
                    toDelete = clinician.isDeleted();
                } else if (object instanceof User) {
                    User user = (User) object;
                    if (user.getChanges().size() <= 0) {
                        continue;
                    }
                    table = "User";
                    identifierName = "nhi";
                    identifier = user.getNhi();
                    toDelete = user.isDeleted();
                } else {
                    throw new InvalidClassException("Object is not of type Users, Clinicians or Administrators");
                }

                PreparedStatement stmt = connection.prepareStatement(String.format("SELECT %s FROM %s WHERE %s = ?", identifierName, table, identifierName));
                stmt.setString(4, identifier);
                ResultSet queryResults = stmt.executeQuery();
                if (!queryResults.next() && !toDelete) {
                    executeCreation(object, connection);
                } else if (toDelete) {
                    deleteRole(object, connection);
                } else {
                    executeUpdate(object, connection);
                }
            }
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }

    /**
     * Executes an update for an entry associated with the provided object.
     * Pre-condition: The object must be of type User, Clinician or Admin
     * Post-condition: The entry in the database reflects the entry
     *
     * @param object The object associated with the entry in the database
     * @param connection Connection ot the target database
     * @param <T>    Admin, Clinician or User
     */
    private <T> void executeUpdate(T object, Connection connection) throws InvalidClassException {


        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                if (object instanceof Administrator) {
                    Administrator admin = (Administrator) object;
                    updateAdminDetails(admin, connection);

                } else if (object instanceof Clinician) {
                    Clinician clinician = (Clinician) object;
                    updateClinicianDetails(clinician, connection);
                    updateClinicianAddress(clinician, connection);

                } else if (object instanceof User) {
                    User user = (User) object;
                    updateUserDetails(user, connection);
                    updateUserAddress(user, connection);
                    updateUserContactDetails(user, connection);
                    updateEmergencyContact(user, connection);
                    updateUserHealthDetails(user, connection);
                    updateUserMedicalProcedures(user, connection);
                    deleteUsersDetails(user, connection);

                } else {
                    throw new InvalidClassException("Provided role is not of type Users, Clinicians or Administrators");
                }
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
     * Updates the contact details of the given user in the database
     *
     * @param user User object with details to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the users contact details
     */
    private void updateUserContactDetails(User user, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_USER_CONTACT_STMT);

        statement.setString(1, user.getHomePhone());
        statement.setString(2, user.getCellPhone());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getNhi());
        // todo: get contact id

        statement.executeUpdate();
    }

    /**
     * Updates the emergency contact of the given user in the database
     *
     * @param user User object
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the emergency contact details
     */
    private void updateEmergencyContact(User user, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_EC_STMT);

        statement.setString(1, user.getContact().getName());
        statement.setString(2, user.getContact().getRelationship());
        // todo: get contact id

        statement.executeUpdate();
    }


    /**
     * Updates the given users address details in the database
     *
     * @param user User object with details to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the user address
     */
    private void updateUserAddress(User user, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_ADDRESS);

        statement.setString(1, user.getStreetNumber()); // todo: update the Address table to take street number as a string instead of an integer - jen 30/6
        statement.setString(2, user.getStreetName());
        statement.setString(3, user.getNeighborhood());
        statement.setString(4, user.getCity());
        statement.setString(5, user.getRegion());
        statement.setString(6, user.getCountry());
        //statement.setString(7, ); // todo: get contact id
        statement.setString(8, user.getNhi());
        statement.setString(9, null);

        statement.executeUpdate();
    }

    /**
     * Updates the given users health details in the database
     * Precondition: Must have an active connection to the database
     * Postcondition: The health details of the given user are updated
     *
     * @param user User object with details to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the users health details
     */
    private void updateUserHealthDetails(User user, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_USER_HEALTH_STMT);

        statement.setString(1, user.getBirthGender());
        statement.setString(2, user.getGenderIdentity());
        statement.setBoolean(3, user.isSmoker());
        statement.setString(4, user.getAlcoholConsumption());
        statement.setDouble(5, user.getHeight());
        statement.setDouble(6, user.getWeight());
        statement.setString(7, user.getNhi());
        // todo: add a blood type/reference to the table - Jen 30/6

        statement.executeUpdate();
    }

    /**
     * Updates the given users medical procedures in the database
     *
     *
     * @param user User object to be updated
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
     * @param user User object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the user details
     */
    private void updateUserDetails(User user, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_USER_STMT);

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


    /**
     * Updates the given clinicians address in the database using UPDATE_ADDRESS
     * Precondition: Must have an active connection to the database
     * Postcondition: The address of the given clinician is updated in the database
     *
     * @param clinician Clinician object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the clinicians address details
     */
    private void updateClinicianAddress(Clinician clinician, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_ADDRESS);

        // todo: update clinician to have an Address object - jen 30/6
    }

    /**
     * Updates the given clinician in the database using UPDATE_CLINICIAN_STMT
     * Precondition: Must have an active connection to the database
     * Postcondition: The given clinician is updated in the database
     *
     * @param clinician Clinician object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the clinician details
     */
    private void updateClinicianDetails(Clinician clinician, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_CLINICIAN_STMT);

        statement.setString(1, clinician.getStaffId());
        statement.setString(2, clinician.getFirstName());
        statement.setString(3, clinician.getMiddleName());
        statement.setString(4, clinician.getLastName());
        statement.setTimestamp(5, Timestamp.valueOf(clinician.getDateLastModified()));
        statement.setString(6, clinician.getStaffId());

        statement.executeUpdate();
    }

    /**
     * Updates the given administrator in the database
     * Precondition: Must have an active connection to the database
     * Postcondition: The given admin is updated in the database
     *
     * @param admin Administrator object to be updated
     * @param connection Connection to the target database
     * @throws SQLException If there is an issue updating the admin details
     */
    private void updateAdminDetails(Administrator admin, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_ADMIN_STMT);

        statement.setString(1, admin.getUserName());
        statement.setString(2, admin.getFirstName());
        statement.setString(3, admin.getMiddleName());
        statement.setString(4, admin.getLastName());
        statement.setTimestamp(5, Timestamp.valueOf(admin.getDateLastModified()));
        statement.setString(6, admin.getUserName());

        statement.executeUpdate();
    }

    /**
     * Executes an update for each of items in the collection.
     * Precondition: The object must be of a type User, Clinician or Administrator
     * Post-conditions: An entry that represents the object is created and stored in the database.
     *
     * @param object collection of objects to update the database with
     * @param connection Connection to the target database
     * @param <T>        User, Clinician or Administrator
     * @throws InvalidClassException if the object is not User, Clinicians or Administrators
     */
    private <T> void executeCreation(T object, Connection connection) throws InvalidClassException {
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                if (object instanceof Administrator) {
                    Administrator admin = (Administrator) object;
                    createAdmin(admin, connection);
                    createPassword(admin, connection);
                } else if (object instanceof Clinician) {
                    Clinician clinician = (Clinician) object;
                    createClinician(clinician, connection);
                    // TODO: Create the clinician contact stuff once the abstractions are completed 25/6 - Eiran
                } else if (object instanceof User) {
                    User user = (User) object;
                    createUser(user, connection);
                    createEmergencyContact(user.getNhi(), user, connection);
                    createContact(user.getNhi(), user, connection);
                    createHealthDetails(user.getNhi(), user, connection);
                } else {
                    throw new InvalidClassException("Provided role is not of type Users, Clinicians or Administrators");
                }
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

    //<editor-fold desc="Create helper functions">
    /**
     * Saves the hashed password to the PasswordDetails table in the database
     * Precondition: The role provided is an Administrator or Clinician
     * Postcondition: The hashed password and salt is stored in the database.
     *
     * @param role Object to store the password of
     * @param connection Connection to the target database
     * @param <T>  Administrator or Clinician
     * @throws InvalidClassException if the role is not an instance of Administrator or Clinician
     * @throws SQLException          If there is an error in storing it into the database or the connection is invalid
     */
    private <T> void createPassword(T role, Connection connection) throws InvalidClassException, SQLException {
        if (role instanceof Administrator) {
            Administrator admin = (Administrator) role;
            PreparedStatement statement = connection.prepareStatement("INSERT INTO PasswordDetails (fkAdminUserName, hash, salt) VALUES (?, ?, ?)");
            statement.setString(1, admin.getUserName());
            //TODO: Figure out how to send the password hash and salt 25/6 - Eiran
        } else if (role instanceof Clinician) {

        } else {
            throw new InvalidClassException("Not a Administrator or Clinician");
        }
    }

    /**
     * Creates an admin entry in the database tables using CREATE_ADMIN_STMT
     * Preconditions: Must have an active connection to the database
     * Post-conditions: The given admin is created in the database
     *
     * @param admin administrator object to create.
     * @param connection Conncetion to the target database
     * @throws SQLException If there isn't an active connection to the database or there is an error creating the administrator
     */
    private void createAdmin(Administrator admin, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_ADMIN_STMT);

        statement.setString(1, admin.getUserName());
        statement.setString(2, admin.getFirstName());
        statement.setString(3, admin.getMiddleName());
        statement.setString(4, admin.getLastName());

        statement.executeUpdate();
    }

    /**
     * Creates a clinician entry in the tables
     * Preconditions: Must have an active connection to the database
     * Postconditions: The given clinician is created in the database
     *
     * @param clinician The clinician object to create
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
     * Creates an user entry in the tables.
     * Must have an active connection to the database (created through connect())
     *
     * @param user user object to place into the entry
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
     * @param userNhi nhi of the user to associate the contact object with.
     * @param user    user to create the associated contact for
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
     * @param userNhi NHI of the user to get the latest contact info for
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
     * @param userNhi NHI of the user to associate the health details with.
     * @param user    user to create the associated contact for
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
     * @param userNhi The NHI of the user to link the contact details for
     * @param user    The user for which the emergency contact is to be created.
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
    //</editor-fold>

    /**
     * Deletes a role entry from the database that is associated with the given object
     * Preconditions: The object provided is of type User, Clinician, Admin
     * Postcondition: The entry associated with the object will be deleted.
     *
     * @param object The object associated with the entry to be deleted
     * @param connection Connection to the target database
     * @param <T>    User, Clinician or Administrator
     * @throws InvalidClassException if the provided object is not of type User, Clinician or Admin
     */
    private <T> void deleteRole(T object, Connection connection) throws InvalidClassException {
        String identifier;
        String sql;
        try {
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                if (object instanceof Administrator) {
                    Administrator admin = (Administrator) object;
                    identifier = admin.getUserName();
                    sql = DELETE_ADMIN_STMT;
                } else if (object instanceof Clinician) {
                    Clinician clinician = (Clinician) object;
                    identifier = clinician.getStaffId();
                    sql = DELETE_CLINICIAN_STMT;
                } else if (object instanceof User) {
                    User user = (User) object;
                    identifier = user.getNhi();
                    sql = DELETE_USER_STMT;
                } else {
                    throw new InvalidClassException("Provided role is not of type Users, Clinicians or Administrators");
                }
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
     * Deletes finer user details such as medications, procedures and diseases
     * Preconditions: There is an active connection to the database
     * Post-conditions: The changes made locally are reflected on the database
     *
     * @param user user to delete the details of
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
     * @param userNhi Nhi of the user to delete the diseases from
     * @param diseases The user for which the previous disease belonged to.
     * @param statement The sql delete statement to use. Can either be DELETE_PAST_DISEASE_STMT if the collection is of the user's past diseases
     *                  Or DELETE_USER_DISEASE_STMT if the collection is of the user's current diseases
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
     * @param user User to delete all marked medications for. Cannot be null
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
     * @param user User to check and delete procedures for
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

    //TODO: Please dont remove this main until db handler is fully developed
//    public static void main(String [ ] args) throws SQLException{
//        DBHandler dbHandler = new DBHandler();
//        JDBCDriver jdbcDriver = new JDBCDriver();
//        dbHandler.getAllUsers(jdbcDriver.getTestConnection());
//        dbHandler.loadClinicians(jdbcDriver.getTestConnection());
//        dbHandler.loadAdmins(jdbcDriver.getTestConnection());
//        System.out.println("done");
//    }
}
