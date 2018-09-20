package odms.database;

import odms.commons.model.datamodel.BloodTest;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class BloodTestHandler {

    private static final String CREATE_BLOOD_TEST_STMT = "INSERT INTO BloodTests (fkUserNhi, redBloodCellCount, " +
            "whiteBloodCellCount, haemoglobinLevel, platelets, glucoseLevels, meanCellVolume, haematocrit, " +
            "meanCellHaematocrit, requestedDate, resultsReceived, requestedByClinician) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_BLOOD_TEST_STMT = "UPDATE BloodTests SET redBloodCellCount = ?, " +
            "whiteBloodCellCount = ?, haemoglobinLevel = ?, platelets = ?, glucoseLevels = ?, meanCellVolume = ?, " +
            "haematocrit = ?, meanCellHaematocrit = ?, resultsReceived = ?, requestedByClinician = ? WHERE bloodTestId = ?";

    private static final String SELECT_ONE_BLOOD_TEST = "SELECT * FROM BloodTests WHERE bloodTestId = ?";
    private static final String SELECT_ALL_BLOOD_TESTS_FOR_USER = "SELECT * FROM BloodTests WHERE fkUserNhi = ? LIMIT ? OFFSET ?";
    private static final String SELECT_ALL_BLOOD_TESTS_FOR_USER_GRAPH = "SELECT * FROM BloodTests WHERE fkUserNhi = ? AND (resultsReceived BETWEEN ? AND ?)";

    /**
     * Saves and stores the given blood test within the database
     *
     * @param connection Connection to the target database
     * @param bloodTest  The blood test to be saved
     * @param nhi        The unique identifier of the user
     * @return ignore this
     * @throws SQLException If there is an error storing the blood test into the database or the connection is invalid
     */
    public BloodTest postBloodTest(Connection connection, BloodTest bloodTest, String nhi) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_BLOOD_TEST_STMT)) {
            preparedStatement.setString(1, nhi);
            preparedStatement.setDouble(2, bloodTest.getRedBloodCellCount());
            preparedStatement.setDouble(3, bloodTest.getWhiteBloodCellCount());
            preparedStatement.setDouble(4, bloodTest.getHaemoglobinLevel());
            preparedStatement.setDouble(5, bloodTest.getPlatelets());
            preparedStatement.setDouble(6, bloodTest.getGlucoseLevels());
            preparedStatement.setDouble(7, bloodTest.getMeanCellVolume());
            preparedStatement.setDouble(8, bloodTest.getHaematocrit());
            preparedStatement.setDouble(9, bloodTest.getMeanCellHaematocrit());
            preparedStatement.setDate(10, Date.valueOf(bloodTest.getRequestedDate()));
            preparedStatement.setDate(11, Date.valueOf(bloodTest.getResultsRecived()));
            preparedStatement.setString(12, bloodTest.getRequestedByClinician());
            preparedStatement.executeUpdate();
        }


        return null; // TODO - remove this when the end point has been changed
    }

    /**
     * Updates a blood test that is already within the database
     *
     * @param connection Connection to the target database
     * @param bloodTest  The given blood test to be updated
     * @throws SQLException If there is an error updating the blood test or the connection is invalid
     */
    public void putBloodTest(Connection connection, String nhi, BloodTest bloodTest) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BLOOD_TEST_STMT)) {
            preparedStatement.setDouble(1, bloodTest.getRedBloodCellCount());
            preparedStatement.setDouble(2, bloodTest.getWhiteBloodCellCount());
            preparedStatement.setDouble(3, bloodTest.getHaemoglobinLevel());
            preparedStatement.setDouble(4, bloodTest.getPlatelets());
            preparedStatement.setDouble(5, bloodTest.getGlucoseLevels());
            preparedStatement.setDouble(6, bloodTest.getMeanCellVolume());
            preparedStatement.setDouble(7, bloodTest.getHaematocrit());
            preparedStatement.setDouble(8, bloodTest.getMeanCellHaematocrit());
            preparedStatement.setDate(9, Date.valueOf(bloodTest.getResultsRecived()));
            preparedStatement.setString(10, bloodTest.getRequestedByClinician());
            preparedStatement.setInt(11, bloodTest.getBloodTestId());
            preparedStatement.executeUpdate();
        }
    }

    public BloodTest patchBloodTest(Connection connection, String nhi, String id, BloodTest bloodTest) {

        return null;
    }

    public BloodTest deleteBloodTest(Connection connection, String nhi, String id) {
        //TODO implement me :)
        return null;
    }

    /**
     * Retrieves a single blood test for the given user from the database
     *
     * @param connection Connection to the target database
     * @param nhi        Unique identifier of the current user
     * @param id         Unique identifier of the blood test
     * @return           A blood test with values retrieved from the database
     * @throws SQLException if the connection is invalid or there is an error with the database
     */
    public BloodTest getBloodTest(Connection connection, String nhi, int id) throws SQLException {
        BloodTest bloodTest = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ONE_BLOOD_TEST)) {
            preparedStatement.setInt(1, id);
            try (ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    bloodTest = decodeBloodTestFromResultSet(results);
                }
            }
        }
        return bloodTest;
    }

    /**
     * Gets all the blood tests between the start and count values for the given user nhi
     *
     * @param connection Connection to the target database
     * @param nhi        Unique identifier of the user
     * @param count      Row number that the query finishes taking entries from
     * @param start      Row number that query starts taking entries from
     * @return           A collection of all the users blood tests between the start and count indices
     * @throws SQLException if the connection is invalid or there is an error retrieving entries from the database
     */
    public Collection<BloodTest> getAllBloodTests(Connection connection, String nhi, int count, int start) throws SQLException {
        Collection<BloodTest> bloodTests = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BLOOD_TESTS_FOR_USER)) {
            preparedStatement.setString(1, nhi);
            preparedStatement.setInt(2, count);
            preparedStatement.setInt(3, start);
            try (ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    bloodTests.add(decodeBloodTestFromResultSet(results));
                }
            }
        }

        return bloodTests;
    }

    /**
     * Retrieves all of the given users blood tests if the date the results were released is between the two given dates
     *
     * @param connection Connection to the target database
     * @param nhi        Unique identifier of the user
     * @param startDate  The date that the query starts taking entries from
     * @param endDate    The date that the query finishes taking entries from
     * @return           A collection of all the users blood tests between the start and end dates
     * @throws SQLException if the connection is invalid or there is an error retrieving entries from the database
     */
    public Collection<BloodTest> getBloodTests(Connection connection, String nhi, LocalDate startDate, LocalDate endDate) throws SQLException {
        Collection<BloodTest> bloodTests = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BLOOD_TESTS_FOR_USER_GRAPH)) {
            preparedStatement.setString(1, nhi);
            preparedStatement.setDate(2, Date.valueOf(startDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            try (ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    bloodTests.add(decodeBloodTestFromResultSet(results));
                }
            }
        }

        return bloodTests;
    }

    /**
     * Adds all the values of a result set to a blood test object and returns it to the user.
     *
     * @param resultSet result set containing values to generate the blood test
     * @return  The blood test to be given back to the user
     * @throws SQLException if there was an error with the result set
     */
    private BloodTest decodeBloodTestFromResultSet(ResultSet resultSet) throws SQLException {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setBloodTestId(resultSet.getInt("bloodTestId"));
        bloodTest.setRedBloodCellCount(resultSet.getDouble("redBloodCellCount"));
        bloodTest.setWhiteBloodCellCount(resultSet.getDouble("whiteBloodCellCount"));
        bloodTest.setHaemoglobinLevel(resultSet.getDouble("haemoglobinLevel"));
        bloodTest.setPlatelets(resultSet.getDouble("platelets"));
        bloodTest.setGlucoseLevels(resultSet.getDouble("glucoseLevels"));
        bloodTest.setMeanCellVolume(resultSet.getDouble("meanCellVolume"));
        bloodTest.setHaematocrit(resultSet.getDouble("haematocrit"));
        bloodTest.setMeanCellHaematocrit(resultSet.getDouble("meanCellHaematocrit"));
        bloodTest.setRequestedDate(resultSet.getDate("requestedDate").toLocalDate());
        bloodTest.setResultsRecived(resultSet.getDate("resultsReceived").toLocalDate());
        bloodTest.setRequestedByClinician(resultSet.getString("requestedByClinician"));

        return bloodTest;
    }
}
