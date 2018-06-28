/*package seng302.commands;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SqlTest {

    @Mock
    Connection mockConnection;

    @Mock
    DataSource mockDatasource;

    @Mock
    ResultSet mockResultSet;

    @Before
    public void setUp() throws SQLException {
        when(mockDatasource.getConnection()).thenReturn(mockConnection);
        doNothing().when(mockConnection).commit();
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
    }

    @Test(expected = SQLException.class)
    public void
    commented to commit

}*/
