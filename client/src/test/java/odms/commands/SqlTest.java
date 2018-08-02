package odms.commands;

import odms.controller.AppController;
import odms.utils.SQLBridge;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SqlTest {

    AppController controller;
    SQLBridge sqlBridge;
    List<String> results;
    Sql command;

    @Before
    public void setUp() throws IOException {
        controller = mock(AppController.class);
        sqlBridge = mock(SQLBridge.class);

        results = new ArrayList<>();

        when(controller.getSqlBridge()).thenReturn(sqlBridge);
        when(controller.getToken()).thenReturn("token");
        when(sqlBridge.excuteSqlStatement(anyString(), anyString())).thenReturn(results);

        command = new Sql();
        command.setAppController(controller);
    }

    @Test
    public void testValidQuery() throws IOException {
        String[] args = {"select * from User"};

        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(sqlBridge, times(1)).excuteSqlStatement(anyString(), anyString());
    }

    @Test
    public void testInvalidQuery() throws IOException {
        String[] args = {"Drop table User"};

        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(sqlBridge, times(0)).excuteSqlStatement(anyString(), anyString());
    }

    @Test
    public void testInvalidQueryContainsSleep() throws IOException {
        String[] args = {"select * from User sleep(100)"};

        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(sqlBridge, times(0)).excuteSqlStatement(anyString(), anyString());
    }
}
