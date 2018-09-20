package odms.database;

import odms.commons.model.datamodel.BloodTest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

public class BloodTestHandler {

    private static final String CREATE_BLOOD_TEST_STMT = "INSERT INTO BloodTests (fkUserNhi, redBloodCellCount, " +
            "whiteBloodCellCount, haemoglobinLevel, platelets, glucoseLevels, meanCellVolume, haematocrit, " +
            "meanCellHaematocrit, requestedDate, resultsReceived, requestedByClinician) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_BLOOD_TEST_STMT = "UPDATE BloodTests SET redBloodCellCount = ?, " +
            "whiteBloodCellCount = ?, haemoglobinLevel = ?, platelets = ?, glucoseLevels = ?, meanCellVolume = ?, " +
            "haematocrit = ?, meanCellHaematocrit = ?, resultsReceived = ?, requestedByClinician = ? WHERE bloodTestId = ?";

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
    public void putBloodTest(Connection connection, BloodTest bloodTest) throws SQLException {
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

    public BloodTest getBloodTest(Connection connection, String nhi, int id) {
        //Todo impement me :)
        return null;
    }

    public Collection<BloodTest> getBloodTests(Connection connection, String nhi, LocalDate startDate, LocalDate endDate) {
        //TODO implement me :)
        return null;
    }

}
