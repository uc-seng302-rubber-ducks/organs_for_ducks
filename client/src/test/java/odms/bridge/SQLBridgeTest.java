package odms.bridge;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SQLBridgeTest extends BridgeTestBase {

    private SQLBridge bridge;
    private Call mockCall;

    @Before
    public void setUp() {
        mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        bridge = new SQLBridge(mockClient);
    }

    @Test
    public void executeShouldReturnEmptyListOnBadResponse() throws IOException {
        Response res = jsonResponseMock(null, 400);
        when(mockCall.execute()).thenReturn(res);
        List<String> result = bridge.executeSqlStatement("select * from Users", "tokenGoesHere");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void executeShouldReturnCorrectResultsOnSuccess() throws IOException {
        List<String> dummyList = Arrays.asList("Hello", "There");
        Response res = jsonResponseMock(dummyList, 200);
        when(res.isSuccessful()).thenReturn(true);
        when(mockCall.execute()).thenReturn(res);
        List<String> result = bridge.executeSqlStatement("select * from Users", "tokenGoesHere");
        Assert.assertTrue(result.size() == 2);
    }
}
