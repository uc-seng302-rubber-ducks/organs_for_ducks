package odms;

import odms.commons.config.ConfigPropertiesSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AppCommandLineArgsTest {
    private App app;
    private ConfigPropertiesSession mockSession;

    @Before
    public void setUp() {
        app = new App();
        mockSession = mock(ConfigPropertiesSession.class);
        ConfigPropertiesSession.getInstance(); //init if not there
        ConfigPropertiesSession.setInstance(mockSession);
    }

    @After
    public void tearDown() {
        ConfigPropertiesSession.setInstance(null);
    }

    @Test
    public void shouldCreatePropertiesFromValidArgs() throws Exception {
        String[] args = new String[]{"test.arg=testValue", "secondArg=second.value"};
        App.getProperties(args);
        verify(mockSession, times(1)).setProperty(eq("test.arg"), eq("testValue"));
        verify(mockSession, times(1)).setProperty(eq("secondArg"), eq("second.value"));
    }

    @Test
    public void shouldRunWithNoArgs() {
        String[] args = new String[0];
        App.getProperties(args);
        verify(mockSession, times(0)).setProperty(anyString(), anyString());
    }

    @Test
    public void shouldIgnoreInvalidArgs() {
        String[] args = new String[]{"test.arg=", "asdf", "one=two=three"};
        App.getProperties(args);
        verify(mockSession, times(0)).setProperty(anyString(), anyString());
    }

    @Test
    public void shouldRunWithSomeValidArgs() {
        String[] args = new String[]{"inv=a=lid", "this.is=okay", "this.is.not"};
        App.getProperties(args);
        verify(mockSession, times(1)).setProperty(eq("this.is"), eq("okay"));
    }
}
