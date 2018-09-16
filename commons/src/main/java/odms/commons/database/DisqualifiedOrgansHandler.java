package odms.commons.database;

import odms.commons.exception.InvalidOrganTypeException;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class DisqualifiedOrgansHandler {

    private static final String SELECT_DISQUALIFIED_STATEMENT = "SELECT * FROM DisqualifiedOrgans WHERE fkUserNhi = ? AND isCurrentlyDisqualified = 1";
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
            if (category.getDbValue() == resultSet.getInt("fkCategoryId")) {
                organ = category;
            }
        }
        if (organ == null) {
            return null;
        }
        String reason = resultSet.getString("description");
        String staffId = resultSet.getString("fkStaffId");
        LocalDate dateDisqualified = resultSet.getDate("dateDisqualified").toLocalDate();
        LocalDate dateEligible = resultSet.getDate("dateEligible").toLocalDate();

        OrgansWithDisqualification disqualifiedOrgan = new OrgansWithDisqualification(organ, reason, dateDisqualified, staffId);
        disqualifiedOrgan.setDisqualifiedId(disqualifiedId);
        disqualifiedOrgan.setDateEligible(dateEligible);

        return disqualifiedOrgan;
    }

    public void postDisqualifiedOrgan(Connection connection, Collection<OrgansWithDisqualification> disqualifications) {
        //for organ in disqualifications
            //Code for constructing sql goes here
    }

    /**
     * Executes a series of sql queries to delete specified disqualified organ entries in the database
     * @param connection Connection to the target database
     * @param disqualifications list of disqualifications to delete. The id is extracted from each to find it's database entry
     * @throws SQLException if there is an issue with the request to or response from to the database
     */
    public void deleteDisqualifiedOrgan(Connection connection, Collection<OrgansWithDisqualification> disqualifications) throws SQLException {
        connection.setAutoCommit(false);
        try {
            for (OrgansWithDisqualification organ : disqualifications) {
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
        } catch (SQLException s) {
            connection.rollback();
            Log.severe("Executing sql to delete disqualified organs encountered an error. Commit was rolled back", s);
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
