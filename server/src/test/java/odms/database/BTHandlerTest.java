package odms.database;

import odms.commons.model.datamodel.BloodTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BTHandlerTest {

    private Connection connection;
    private PreparedStatement mockStmt;
    private BloodTestHandler bloodTestHandler;


    @Before
    public void beforeTest() throws SQLException {
        connection = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        bloodTestHandler = new BloodTestHandler();

        when(connection.prepareStatement(anyString())).thenReturn(mockStmt);
        doNothing().when(mockStmt).setString(anyInt(), anyString());
    }

    @After
    public void afterTest() throws SQLException {
        connection.close();
    }

    @Test
    public void testCreateBloodTest() throws SQLException {
        BloodTest testBloodTest = new BloodTest();
        testBloodTest.setRequestedDate(LocalDate.now().minusDays(5));

        bloodTestHandler.postBloodTest(connection, testBloodTest, "ABC1234");
        verify(mockStmt, times(1)).executeUpdate();
    }


}
