package odms.database;

import odms.commons.model.datamodel.BloodTest;
import odms.test_utils.DBHandlerMocker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BloodTestHandlerTest {

    private Connection connection;
    private PreparedStatement mockStmt;
    private ResultSet mockResultSet;
    private BloodTestHandler bloodTestHandler;
    private BloodTest testBloodTest;

    @Before
    public void beforeTest() throws SQLException {
        connection = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        bloodTestHandler = new BloodTestHandler();

        when(connection.prepareStatement(anyString())).thenReturn(mockStmt);
        doNothing().when(mockStmt).setString(anyInt(), anyString());
        when(mockStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        testBloodTest = new BloodTest();
        testBloodTest.setBloodTestId(1);
        testBloodTest.setRedBloodCellCount(0.0054);
        testBloodTest.setTestDate(LocalDate.of(2018, 9, 15));
    }

    @After
    public void afterTest() throws SQLException {
        connection.close();
    }

    @Test
    public void testCreateBloodTest() throws SQLException {
        BloodTest bloodTest = new BloodTest();
        bloodTest.setTestDate(LocalDate.now().minusDays(5));

        bloodTestHandler.postBloodTest(connection, bloodTest, "ABC1234");
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testPatchBloodTest() throws SQLException {
        DBHandlerMocker.setBloodTestResultSet(mockResultSet);
        testBloodTest.setRedBloodCellCount(0.0063);

        bloodTestHandler.patchBloodTest(connection, "ABC1234", 1, testBloodTest);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testGetOneBloodTest() throws SQLException {
        DBHandlerMocker.setBloodTestResultSet(mockResultSet);
        BloodTest bt = bloodTestHandler.getBloodTest(connection, "ABC1234", 1);
        verify(mockStmt, times(1)).executeQuery();
        Assert.assertTrue(bt.getTestDate().equals(testBloodTest.getTestDate()));
        Assert.assertTrue(bt.getBloodTestId() == testBloodTest.getBloodTestId());
        Assert.assertTrue(bt.getRedBloodCellCount() == testBloodTest.getRedBloodCellCount());
    }

    @Test
    public void testGetAllBloodTests() throws SQLException {
        DBHandlerMocker.setBloodTestResultSet(mockResultSet);
        Collection<BloodTest> bTCollection = bloodTestHandler.getBloodTests(connection, "ABC1234", LocalDate.now().minusDays(10), LocalDate.now().minusDays(2), 30, 0);

        Assert.assertTrue(bTCollection.size() == 1);
        verify(mockStmt, times(1)).executeQuery();
    }

    @Test
    public void testDeleteBloodTest() throws SQLException {
        bloodTestHandler.deleteBloodTest(connection, "ABC1234", 1);
        verify(mockStmt, times(1)).executeUpdate();
    }

}
