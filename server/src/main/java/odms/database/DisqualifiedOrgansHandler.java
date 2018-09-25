package odms.database;

import odms.commons.exception.InvalidOrganTypeException;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.Log;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class DisqualifiedOrgansHandler {

    private static final String SELECT_DISQUALIFIED_STATEMENT = "SELECT * FROM DisqualifiedOrgans WHERE fkUserNhi = ? AND isCurrentlyDisqualified = 1";
    private static final String CREATE_DISQUALIFIED_STATEMENT = "INSERT INTO DisqualifiedOrgans (fkUserNhi, description, fkOrgan, fkStaffId, dateDisqualified, dateEligible, isCurrentlyDisqualified) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_DISQUALIFIED_STATEMENT = "UPDATE DisqualifiedOrgans SET description = ?, dateEligible = ?, isCurrentlyDisqualified = ? WHERE disqualifiedId = ?";
    private static final String DELETE_DISQUALIFIED_STATEMENT = "DELETE FROM DisqualifiedOrgans WHERE disqualifiedId = ?";

    /**
     * Executes an sql statement to retrieve a list from the database of disqualified organs for the specified user
     * @param connection Connection to the target database
     * @param id User to retrieve organs for
     * @return A collection of disqualified organs
     * @throws SQLException If there is an issue with the request to or response from the database
     */
    public Collection<OrgansWithDisqualification> getDisqualifiedOrgans(Connection connection, String id) throws SQLException {
        Collection<OrgansWithDisqualification> disqualifications = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DISQUALIFIED_STATEMENT)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet != null && resultSet.next()) {
                    OrgansWithDisqualification disqualifiedOrgan = decodeDisqualifiedOrganFromResultSet(resultSet);
                    if (disqualifiedOrgan == null) {
                        Log.severe("A disqualified organ was returned that had no organ type, or the type " +
                                "id was outside the expected range. The organ was not added to the returned collection", new InvalidOrganTypeException());
                        break; //For some reason the while loop does not terminate if this case occurs.
                    } else {
                        disqualifications.add(disqualifiedOrgan);
                    }
                }
            }

        }
        return disqualifications;
    }

    /**
     * Takes a result set and returns an organsWithDisqualifications object based on the current row of the result set
     * @param resultSet Result set with the cursor set to the row to generate the object from
     * @return the organsWithDisqualification. Returns null if the organ type is invalid.
     * @throws SQLException If there was an error with the result set.
     */
    private OrgansWithDisqualification decodeDisqualifiedOrganFromResultSet(ResultSet resultSet) throws SQLException {
        Integer disqualifiedId = resultSet.getInt("disqualifiedId");
        Organs organ = null;
        for (Organs category : Organs.values()) {
            if (category.getDbValue() == resultSet.getInt("fkOrgan")) {
                organ = category;
            }
        }
        if (organ == null) {
            return null;
        }
        String reason = resultSet.getString("description");
        String staffId = resultSet.getString("fkStaffId");
        LocalDate dateDisqualified = resultSet.getDate("dateDisqualified").toLocalDate();
        Date sqlDate = resultSet.getDate("dateEligible");
        LocalDate dateEligible = null;
        if (sqlDate != null) {
            dateEligible = sqlDate.toLocalDate();
        }
        boolean isCurrentlyDisqualified = resultSet.getBoolean("isCurrentlyDisqualified");


        OrgansWithDisqualification disqualifiedOrgan = new OrgansWithDisqualification(organ, reason, dateDisqualified, staffId);
        disqualifiedOrgan.setDisqualifiedId(disqualifiedId);
        disqualifiedOrgan.setEligibleDate(dateEligible);
        disqualifiedOrgan.setCurrentlyDisqualified(isCurrentlyDisqualified);

        return disqualifiedOrgan;
    }

    public void postDisqualifiedOrgan(Connection connection, Collection<OrgansWithDisqualification> disqualifications, String nhi) throws SQLException {
        for (OrgansWithDisqualification disqualification : disqualifications) {
            if (disqualification.getDisqualifiedId() == null) { //If an organ does not have an Id then it does not exist in the database, so create it.
                try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_DISQUALIFIED_STATEMENT)) {
                    preparedStatement.setString(1, nhi);
                    preparedStatement.setString(2, disqualification.getReason());
                    preparedStatement.setInt(3, disqualification.getOrganType().getDbValue());
                    preparedStatement.setString(4, disqualification.getStaffId());
                    preparedStatement.setDate(5, Date.valueOf(disqualification.getDate()));
                    LocalDate dateEligible = disqualification.getEligibleDate();
                    if (dateEligible == null) {
                        preparedStatement.setDate(6, null);
                    } else {
                        preparedStatement.setDate(6, Date.valueOf(dateEligible));
                    }
                    preparedStatement.setBoolean(7, disqualification.isCurrentlyDisqualified());

                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    public void updateDisqualifiedOrgan(Connection connection, Collection<OrgansWithDisqualification> disqualifications) throws SQLException {
        for (OrgansWithDisqualification disqualification : disqualifications) {
            if (disqualification.getDisqualifiedId() != null) { //If an organ has an Id then it exists in the database, so update it.
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DISQUALIFIED_STATEMENT)) {
                    preparedStatement.setString(1, disqualification.getReason());
                    LocalDate dateEligible = disqualification.getEligibleDate();
                    if (dateEligible == null) {
                        preparedStatement.setDate(2, null);
                    } else {
                        preparedStatement.setDate(2, Date.valueOf(dateEligible));
                    }
                    preparedStatement.setBoolean(3, disqualification.isCurrentlyDisqualified());
                    preparedStatement.setInt(4, disqualification.getDisqualifiedId());

                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    /**
     * Executes a series of sql queries to delete specified disqualified organ entries in the database
     * @param connection Connection to the target database
     * @param disqualifications list of disqualifications to delete. The id is extracted from each to find it's database entry
     * @throws SQLException if there is an issue with the request to or response from to the database
     */
    public void deleteDisqualifiedOrgan(Connection connection, Collection<OrgansWithDisqualification> disqualifications) throws SQLException {
        for (OrgansWithDisqualification organ : disqualifications) {
            //Delete is organ is no longer disqualified AND past their eligible date. Basically any organ that has naturally become available again (eg. user became healthy).
            if (organ.getEligibleDate() != null && !organ.isCurrentlyDisqualified() && organ.getEligibleDate().isBefore(LocalDate.now())) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_DISQUALIFIED_STATEMENT)) {
                    Integer id = organ.getDisqualifiedId();
                    if (id == null) {
                        Log.warning("A database deletion was attempted on a disqualified organ without an id");
                    } else {
                        preparedStatement.setInt(1, id);
                        connection.commit();
                    }
                }
            }
        }
    }
}
