package seng302.model;

import odms.commons.utils.HttpRequester;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HttpRequesterTest {

    private OkHttpClient mockClient;
    private HttpRequester requester;

    @Before
    public void setUp() {
        mockClient = mock(OkHttpClient.class);
        requester = new HttpRequester(mockClient);
    }

    @Test
    public void DrugInteractionsShouldSendDrugInteractionsRequest() throws IOException {
        //getDrugInteractions with two valid drug names
        Response mockResponse = mock(Response.class);
        //mock of the underlying classes not visible from source code
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        //set behaviours
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("test body");
        String result = requester.getDrugInteractions("Panadol", "Tramadol");

        verify(mockCall, times(1)).execute();
        assert (result.equals("test body"));
    }

    @Test
    public void DrugInteractionsShouldReturnBlankIfNoResponseBody() throws IOException {
        //getDrugInteractions with two valid drug names
        Response mockResponse = mock(Response.class);
        //mock of the underlying classes not visible from source code
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        //set behaviours
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponseBody.string()).thenReturn(null);
        when(mockResponse.body()).thenReturn(mockResponseBody);

        String result = requester.getDrugInteractions("water", "milk");

        verify(mockCall, times(1)).execute();
        assert (result.equals(""));
    }

    @Test
    public void DrugInteractionsShouldRestrictResultsWhenAgeAndGenderPresent() throws Exception {
        Response mockResponse = mock(Response.class);
        //mock of the underlying classes not visible from source code
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        //get json body from file
        JSONParser parser = new JSONParser();
        JSONObject responseBody = (JSONObject) parser.parse(new FileReader(
                "src/test/resources/httpResponses/coumadin-acetaminophen-interactions.json"));

        //set behaviours
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponseBody.string()).thenReturn(responseBody.toString());
        when(mockResponse.body()).thenReturn(mockResponseBody);
        Set<String> expected = new HashSet<>();
        expected.add("convulsion (not specified)");
        expected.add("dizziness (not specified)");
        expected.add("fatigue (not specified)");
        expected.add("muscle spasms (not specified)");
        expected.add("injury (not specified)");
        expected.add("nausea (not specified)");
        expected.add("pain in extremity (not specified)");
        expected.add("dyspnoea (not specified)");
        expected.add("hypotension (6 - 12 months)");
        expected.add("catheter site inflammation (not specified)");
        expected.add("oedema peripheral (< 1 month)");
        expected.add("anxiety (< 1 month)");
        expected.add("anaemia (1 - 6 months)");
        expected.add("pneumonia (5 - 10 years)");
        expected.add("international normalised ratio increased (10+ years)");


        Set<String> results = requester.getDrugInteractions("coumadin", "acetaminophen", "m", 36);

        verify(mockCall, times(1)).execute();
        Assert.assertEquals(expected, results);
    }

    @Test
    public void DrugInteractionsShouldReturnEmptySetWhenNullResponse() throws IOException {
        Response mockResponse = mock(Response.class);
        //mock of the underlying classes not visible from source code
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        //set behaviours
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponseBody.string()).thenReturn(null);
        when(mockResponse.body()).thenReturn(mockResponseBody);

        Set<String> result = requester.getDrugInteractions("water", "milk", "male", 42);

        verify(mockCall, times(1)).execute();
        assert (result.isEmpty());
    }

    @Test
    public void ActiveIngredientsReturnsIngredients() throws IOException {
        Response mockResponse = mock(Response.class);
        //mock of the underlying classes not visible from source code
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        //set behaviours
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponseBody.string()).thenReturn("[\"test result 1\",\"test result 2\"]");
        when(mockResponse.body()).thenReturn(mockResponseBody);

        String[] expected = new String[]{"test result 1", "test result 2"};
        String[] results = requester.getActiveIngredients("reserpine");
        Assert.assertArrayEquals(expected, results);
    }

    @Test
    public void ActiveIngredientsReturnsEmptyStringArrayWhenNoResponseGiven() throws IOException {
        Response mockResponse = mock(Response.class);
        //mock of the underlying classes not visible from source code
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        //set behaviours
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponseBody.string()).thenReturn(null);
        when(mockResponse.body()).thenReturn(mockResponseBody);

        String[] expected = {};
        String[] results = requester.getActiveIngredients("reserpine");
        Assert.assertArrayEquals(results, expected);
    }

    @Test
    public void ActiveIngredientsReturnsEmptyStringInArrayWhenEmptyResultsGiven() throws IOException {
        Response mockResponse = mock(Response.class);
        //mock of the underlying classes not visible from source code
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        //set behaviours
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponseBody.string()).thenReturn("[]");
        when(mockResponse.body()).thenReturn(mockResponseBody);

        String[] expected = {""};
        String[] results = requester.getActiveIngredients("reserpine");
        Assert.assertArrayEquals(results, expected);
    }

    @Test
    public void autoCompleteReturnsString() throws IOException {
        Response mockResponse = mock(Response.class);

        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        JSONParser parser = new JSONParser();
        JSONObject responseBody = null;
        try {
            responseBody = (JSONObject) parser.parse(new FileReader(
                    "src/test/resources/httpResponses/res-autocomplete-query.json"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponseBody.string()).thenReturn(responseBody.toString());
        when(mockResponse.body()).thenReturn(mockResponseBody);
        String expected = "Reserpine," +
                "Resectisol,Resectisol in plastic container,Restoril,Rescriptor,Restasis,Rescula," +
                "Reserpine and hydrochlorothiazide,Reserpine, hydralazine hydrochloride and hydrochlorothiazide" +
                ",Reserpine, hydrochlorothiazide, and hydralazine hydrochloride,Reserpine and hydrochlorothiazide-50," +
                "Reserpine and hydroflumethiazide,Resporal,";

        String result = requester.getSuggestedDrugs("res");
        System.out.println();
        verify(mockCall, times(1)).execute();
        Assert.assertTrue(result.equals(expected));
    }


    @Test
    public void returnEmptyArrayStringOnError() throws IOException {
        Response mockResponse = mock(Response.class);
        //mock of the underlying classes not visible from source code
        Call mockCall = mock(Call.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        JSONParser parser = new JSONParser();
        JSONObject responseBody = null;
        try {
            responseBody = (JSONObject) parser.parse(new FileReader(
                    "src/test/resources/httpResponses/empty-Autocomplete.json"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //set behaviours
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponseBody.string()).thenReturn(responseBody.toString());
        when(mockResponse.body()).thenReturn(mockResponseBody);


        String result = requester.getSuggestedDrugs("aaa");
        verify(mockCall, times(1)).execute();
        System.out.println(result);
        Assert.assertTrue(result.equals(""));
    }
}
