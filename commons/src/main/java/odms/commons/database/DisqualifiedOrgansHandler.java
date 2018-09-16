package odms.commons.database;

import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class DisqualifiedOrgansHandler {

    private static final String CREATE_DISQUALIFIED_STATEMENT = "INSERT INTO DisqualifiedOrgans (fkUserNhi, description, fkOrgan, fkStaffId, dateDisqualified, dateEligible, isCurrentlyDisqualified) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_DISQUALIFIED_STATEMENT = "DELETE FROM DisqualifiedOrgans WHERE disqualifiedId = ?";

    public Collection<OrgansWithDisqualification> getDisqualifiedOrgans(Connection connection) {
        //Code for constructing sql goes here
        return new ArrayList<>();
    }

    public void postDisqualifiedOrgan(Connection connection, Collection<OrgansWithDisqualification> disqualifications, String nhi) throws SQLException {
        for (OrgansWithDisqualification disqualification : disqualifications) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_DISQUALIFIED_STATEMENT)) {
                preparedStatement.setString(1, nhi);
                preparedStatement.setString(2, disqualification.getReason());
                preparedStatement.setInt(3, disqualification.getOrganType().getDbValue());
                preparedStatement.setString(4, disqualification.getStaffId());
                preparedStatement.setDate(5, Date.valueOf(disqualification.getDate()));
                preparedStatement.setDate(6, Date.valueOf(disqualification.getEligibleDate()));
                preparedStatement.setBoolean(7, true);
                // todo: do we actually need a boolean flag saying the organ is disqualified?

                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Executes a series of sql queries to delete specified disqualified organ entries in the database
     * @param connection Connection to the target database
     * @param disqualifications list of disqualifications to delete. The id is extracted from each to find it's database entry
     * @throws SQLException if there is an issue with the connection to the database
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
