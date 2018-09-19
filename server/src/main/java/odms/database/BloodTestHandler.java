package odms.database;

import odms.commons.model.datamodel.BloodTest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BloodTestHandler {

    private static final String CREATE_BLOOD_TEST_STMT = "INSERT INTO BloodTests (fkUserNhi, redBloodCellCount, " +
            "whiteBloodCellCount, haemoglobinLevel, platelets, glucoseLevels, meanCellVolume, haematocrit, " +
            "meanCellHaematocrit, requestedDate, resultsReceived, requestedByClinician) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            preparedStatement.setDate(10, Date.valueOf(bloodTest.getRequestedDate()));
            preparedStatement.setDate(11, Date.valueOf(bloodTest.getResultsRecived()));
            preparedStatement.setString(12, bloodTest.getRequestedByClinician());
            preparedStatement.executeUpdate();
        }
    }

}
