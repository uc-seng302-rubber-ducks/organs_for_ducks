package odms.commons.database;

import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;

public class DisqualifiedOrgansHandlerTest {

    private Connection connection;
    private PreparedStatement mockStmt;
    private DisqualifiedOrgansHandler handler;
    private ResultSet resultSet;

    @Before
    public void beforeTest() throws SQLException {
        connection = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        handler = new DisqualifiedOrgansHandler();
        resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(mockStmt);
        doNothing().when(mockStmt).setString(anyInt(), anyString());
    }

    @After
    public void afterTest() throws SQLException {
        connection.close();
    }

    /**
     * Helper function for tests to use instead of repeating code
     * @return a collection of OrgansWithDisqualification
     */
    private OrgansWithDisqualification createTestDisqualifiedOrgan(Integer id) {
        OrgansWithDisqualification testOrgan = new OrgansWithDisqualification(Organs.LIVER, "Testing", LocalDate.now(),"0");
        testOrgan.setDisqualifiedId(id);
        return testOrgan;
    }

    private void setUpResultSetWhenMocks(OrgansWithDisqualification testOrgan) throws SQLException {
        when(mockStmt.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("fkCategoryId")).thenReturn(testOrgan.getOrganType().getDbValue());
        when(resultSet.getInt("disqualifiedId")).thenReturn(testOrgan.getDisqualifiedId());
        when(resultSet.getString("description")).thenReturn(testOrgan.getReason());
        when(resultSet.getString("fkStaffId")).thenReturn(testOrgan.getStaffId());
        Long dateLong = Instant.now().truncatedTo(ChronoUnit.MILLIS).toEpochMilli();
        java.sql.Date date = new java.sql.Date(dateLong);
        when(resultSet.getDate("dateDisqualified")).thenReturn(date);
        when(resultSet.getDate("dateEligible")).thenReturn(date);
    }

    @Test
    public void testGetDisqualifiedOrgans_ReturnsList_NoIssues() throws SQLException {
        OrgansWithDisqualification testOrgan = createTestDisqualifiedOrgan(null);
        testOrgan.setDisqualifiedId(0);
        List<OrgansWithDisqualification> disqualifications = new ArrayList<>();
        disqualifications.add(testOrgan);
        setUpResultSetWhenMocks(testOrgan);

        List<OrgansWithDisqualification> resultCollection = new ArrayList<>(handler.getDisqualifiedOrgans(connection, "ABC2134"));
        Assert.assertEquals(disqualifications.get(0), resultCollection.get(0));
    }

    @Test
    public void testGetDisqualifiedOrgans_ReturnsList_WithResultSetError() throws SQLException {
        OrgansWithDisqualification testOrgan = createTestDisqualifiedOrgan(null);
        testOrgan.setDisqualifiedId(0);
        setUpResultSetWhenMocks(testOrgan);
        int incorrectOrganType = -1;
        when(resultSet.getInt("fkCategoryId")).thenReturn(incorrectOrganType);

        List<OrgansWithDisqualification> resultCollection = new ArrayList<>(handler.getDisqualifiedOrgans(connection, "ABC2134"));
        Assert.assertTrue(resultCollection.isEmpty());
    }

    @Test
    public void testDeleteDisqualifiedOrgans_DoesNotCommit_NullId() throws SQLException {
        OrgansWithDisqualification testOrgan = createTestDisqualifiedOrgan(null);
        Collection<OrgansWithDisqualification> disqualifications = new ArrayList<>();
        disqualifications.add(testOrgan);

        handler.deleteDisqualifiedOrgan(connection, disqualifications);
        verify(connection, times(0)).commit();
    }

    @Test
    public void testDeleteDisqualifiedOrgans_CommitsOnce() throws SQLException {
        OrgansWithDisqualification testOrgan = createTestDisqualifiedOrgan(0);
        Collection<OrgansWithDisqualification> disqualifications = new ArrayList<>();
        disqualifications.add(testOrgan);

        handler.deleteDisqualifiedOrgan(connection, disqualifications);
        verify(connection, times(1)).commit();
    }

    @Test
    public void testDeleteDisqualifiedOrgans_CommitsMoreThanOnce() throws SQLException {
        OrgansWithDisqualification testOrgan1 = createTestDisqualifiedOrgan(1);
        OrgansWithDisqualification testOrgan2 = createTestDisqualifiedOrgan(2);
        OrgansWithDisqualification testOrgan3 = createTestDisqualifiedOrgan(3);

        Collection<OrgansWithDisqualification> disqualifications = new ArrayList<>();
        disqualifications.add(testOrgan1);
        disqualifications.add(testOrgan2);
        disqualifications.add(testOrgan3);

        handler.deleteDisqualifiedOrgan(connection, disqualifications);
        verify(connection, times(3)).commit();
    }

    @Test
    public void testDeleteDisqualifiedOrgans_CommitsMoreThanOnce_WhenOneIdIsNull() throws SQLException {
        OrgansWithDisqualification testOrgan4 = createTestDisqualifiedOrgan(4);
        OrgansWithDisqualification testOrgan5 = createTestDisqualifiedOrgan(null);
        OrgansWithDisqualification testOrgan6 = createTestDisqualifiedOrgan(6);

        Collection<OrgansWithDisqualification> disqualifications = new ArrayList<>();
        disqualifications.add(testOrgan4);
        disqualifications.add(testOrgan5);
        disqualifications.add(testOrgan6);

        handler.deleteDisqualifiedOrgan(connection, disqualifications);
        verify(connection, times(2)).commit();
    }

    @Test
    public void testDeleteDisqualifiedOrgans_RollsBack_WhenSqlException() throws SQLException {
        OrgansWithDisqualification testOrgan = createTestDisqualifiedOrgan(0);

        Collection<OrgansWithDisqualification> disqualifications = new ArrayList<>();
        disqualifications.add(testOrgan);

        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());

        handler.deleteDisqualifiedOrgan(connection, disqualifications);
        verify(connection, times(0)).commit();
        verify(connection, times(1)).rollback();
    }

}
