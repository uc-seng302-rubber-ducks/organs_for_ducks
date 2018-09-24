package odms.database;

import odms.commons.model.datamodel.BloodTest;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BloodTestHandler {

    private static final String CREATE_BLOOD_TEST_STMT = "INSERT INTO BloodTestDetails (fkUserNhi, redBloodCellCount, " +
            "whiteBloodCellCount, haemoglobinLevel, platelets, glucoseLevels, meanCellVolume, haematocrit, " +
            "meanCellHaematocrit, testDate) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ONE_BLOOD_TEST = "SELECT * FROM BloodTestDetails WHERE bloodTestId = ?";
    private static final String DELETE_ONE_BLOOD_TEST = "DELETE FROM BloodTestDetails WHERE bloodTestId = ?";
    private static final String SELECT_ALL_BLOOD_TESTS_FOR_USER = "SELECT * FROM BloodTestDetails WHERE fkUserNhi = ? AND (testDate BETWEEN ? AND ?) LIMIT ? OFFSET ?";

    /**
     * Saves and stores the given blood test within the database
     *
     * @param connection Connection to the target database
     * @param bloodTest  The blood test to be saved
     * @param nhi        The unique identifier of the user
     * @throws SQLException If there is an error storing the blood test into the database or the connection is invalid
     */
    public void postBloodTest(Connection connection, BloodTest bloodTest, String nhi) throws SQLException {
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
            preparedStatement.setDate(10, Date.valueOf(bloodTest.getTestDate()));
            preparedStatement.executeUpdate();
        }

    }

    /**
     * Updates only the values of a blood test that have changed
     *
     * @param connection Connection to the target database
     * @param nhi        The unique identifier of the user
     * @param id         The unique identifier of the blood test
     * @param bloodTest  The given blood test to be updated
     * @throws SQLException If there is an error updating the blood test or the connection is invalid
     */
    public void patchBloodTest(Connection connection, String nhi, int id, BloodTest bloodTest) throws SQLException {
        BloodTest originalBloodTest = getBloodTest(connection, nhi, id);

        if (originalBloodTest != null) {
            List<String> changes = new ArrayList<>();
            List<Double> values = new ArrayList<>();
            LocalDate date = null;

            if (originalBloodTest.getRedBloodCellCount() != bloodTest.getRedBloodCellCount()) {
                changes.add("redBloodCellCount = ?");
                values.add(bloodTest.getRedBloodCellCount());
            }

            if (originalBloodTest.getWhiteBloodCellCount() != bloodTest.getWhiteBloodCellCount()) {
                changes.add("whiteBloodCellCount = ?");
                values.add(bloodTest.getWhiteBloodCellCount());
            }

            if (originalBloodTest.getHaemoglobinLevel() != bloodTest.getHaemoglobinLevel()) {
                changes.add("haemoglobinLevel = ?");
                values.add(bloodTest.getHaemoglobinLevel());
            }

            if (originalBloodTest.getPlatelets() != bloodTest.getPlatelets()) {
                changes.add("platelets = ?");
                values.add(bloodTest.getPlatelets());
            }

            if (originalBloodTest.getGlucoseLevels() != bloodTest.getGlucoseLevels()) {
                changes.add("glucoseLevels = ?");
                values.add(bloodTest.getGlucoseLevels());
            }

            if (originalBloodTest.getMeanCellVolume() != bloodTest.getMeanCellVolume()) {
                changes.add("meanCellVolume = ?");
                values.add(bloodTest.getMeanCellVolume());
            }

            if (originalBloodTest.getHaematocrit() != bloodTest.getHaematocrit()) {
                changes.add("haematocrit = ?");
                values.add(bloodTest.getHaematocrit());
            }

            if (originalBloodTest.getMeanCellHaematocrit() != bloodTest.getMeanCellHaematocrit()) {
                changes.add("meanCellHaematocrit = ?");
                values.add(bloodTest.getMeanCellHaematocrit());
            }

            if (originalBloodTest.getTestDate() != bloodTest.getTestDate()) {
                changes.add("testDate = ?");
                date = bloodTest.getTestDate();
            }

            if (changes.size() != 0) {

                int size;
                if (date != null) {
                    size = changes.size() - 1;
                } else {
                    size = changes.size();
                }

                String updateStatement = "UPDATE BloodTestDetails SET ";
                updateStatement += String.join(", ", changes);
                updateStatement += "WHERE bloodTestId = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(updateStatement)) {

                    int i = 0;
                    if (values.size() != 0) { // only adds the double values if they exist
                        for (i = 0; i < size; i++) {
                            preparedStatement.setDouble(i + 1, values.get(i));
                        }
                    }

                    if (date != null) { // only adds the date if it exists
                        preparedStatement.setDate(i + 1, Date.valueOf(bloodTest.getTestDate()));
                        preparedStatement.setInt(i + 2, id);
                    } else {
                        preparedStatement.setInt(i + 1, id);
                    }


                    preparedStatement.executeUpdate();

                }
            }
        }
    }

    /**
     * Deletes the given blood test
     *
     * @param connection Connection to the database
     * @param nhi        Unique identifier of the user
     * @param id         Blood tests unique identifier
     * @throws SQLException thrown on DB error
     */
    public void deleteBloodTest(Connection connection, String nhi, int id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ONE_BLOOD_TEST)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
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
     * Retrieves all of the given users blood tests if the date the results were released is between the two given dates
     *
     * @param connection Connection to the target database
     * @param nhi        Unique identifier of the user
     * @param startDate  The date that the query starts taking entries from
     * @param endDate    The date that the query finishes taking entries from
     * @param count      Row number that the query finishes taking entries from
     * @param startIndex Row number that query starts taking entries from
     * @return           A collection of all the users blood tests between the start and end dates
     * @throws SQLException if the connection is invalid or there is an error retrieving entries from the database
     */
    public Collection<BloodTest> getBloodTests(Connection connection, String nhi, LocalDate startDate, LocalDate endDate, int count, int startIndex) throws SQLException {
        Collection<BloodTest> bloodTests = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BLOOD_TESTS_FOR_USER)) {
            preparedStatement.setString(1, nhi);
            preparedStatement.setDate(2, Date.valueOf(startDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            preparedStatement.setInt(4, count);
            preparedStatement.setInt(5, startIndex);
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
        bloodTest.setTestDate(resultSet.getDate("testDate").toLocalDate());

        return bloodTest;
    }
}
