package seng302.commands;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import picocli.CommandLine;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DbHandlerTest {

    Connection mockConnection = mock(Connection.class);

    DataSource mockDatasource;

    ResultSet mockResultSet = mock(ResultSet.class);

    @Before
    public void setUp() throws SQLException {
        when(mockDatasource.getConnection()).thenReturn(mockConnection);
        doNothing().when(mockConnection).commit();
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
    }

}
