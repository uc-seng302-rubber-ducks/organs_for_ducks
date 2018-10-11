package odms.bridge;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import odms.commons.model._enum.BloodTestProperties;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.gui.widget.CountableLoadingWidget;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BloodTestBridge extends Bifrost {

    private static final String USER = "user/";

    public BloodTestBridge(OkHttpClient client) {
        super(client);
    }

    /**
     * Posts a blood test for a user
     *
     * @param bloodTest blood test to post
     * @param nhi nhi of the user to post the blood test for
     * @param token authentication token
     */
    public void postBloodTest(BloodTest bloodTest, String nhi, String token) {
        String url = ip + "/" + USER + nhi + "/bloodTest";
        RequestBody requestBody = RequestBody.create(json, new Gson().toJson(bloodTest));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).post(requestBody).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("POST", url));
    }

    /**
     * patches a blood test for a user
     *
     * @param bloodTest blood test to patch
     * @param nhi nhi of the user to patch the blood test for
     * @param token authentication token
     */
    public void patchBloodTest(BloodTest bloodTest, String nhi, String token) {
        String url = ip + "/" + USER + nhi +"/bloodTest/"+bloodTest.getBloodTestId();
        RequestBody requestBody = RequestBody.create(json, new Gson().toJson(bloodTest));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).patch(requestBody).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("PATCH", url));
    }

    /**
     * deletes a blood test for a user
     *
     * @param bloodTestId blood test id to delete
     * @param nhi nhi of the user to delete the blood test for
     * @param token authentication token
     */
    public void deleteBloodTest(String bloodTestId, String nhi, String token) {
        String url = ip +"/" + USER + nhi +" /bloodTest/"+ bloodTestId;
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).delete().build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("DELETE", url));
    }

    /**
     * Returns a single blood test
     * @param bloodTestId id od the blood test
     * @param nhi nhi of the user associated with the bloodtest
     * @return The blood test if it can be parsed; null otherwise
     */
    public BloodTest getBloodTest(String bloodTestId , String nhi) {
        String url = ip +"/" + USER + nhi + " /bloodTest/" + bloodTestId;
        Request request = new Request.Builder().get().url(url).build();
        BloodTest toReturn = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                toReturn = handler.decodeBloodTest(response.body().string());
            }
        } catch (IOException e) {
            Log.warning("Could not GET from " + url,e);
        }
        return toReturn;
    }


    /**
     * Gets all  blood tests for a specified user
     *
     * @param nhi nhi of the user associated with the blood tests
     * @param widget spinning wheel widget to give feedback that the graph is loading
     * @param startIndex number of entries to skip before returning
     * @param count number of entries to return
     * @param endDate gets blood tests up to this date
     * @param startDate gets blood tests after this date
     * @param observableBloodTests blood test list to populate
     */
    public void getBloodTests(String nhi, String startDate, String endDate, int count, int startIndex, ObservableList<BloodTest> observableBloodTests, CountableLoadingWidget widget) {
        String url = String.format("%s/%s%s/bloodTests?startDate=%s&endDate=%s&count=%d&startIndex=%d", ip, USER, nhi, startDate, endDate, count, startIndex);
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe("Could not get all blood tests for " + nhi, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String bodyString = response.body().string();
                    Collection<BloodTest> bloodTests = handler.decodeBloodTests(bodyString);
                    for (BloodTest bt : bloodTests) {
                        checkBounds(bt);
                    }

                    Platform.runLater(() -> {
                        observableBloodTests.clear();
                        observableBloodTests.addAll(bloodTests);
                        if (widget != null) {
                            widget.setWaiting(false);
                        }
                    });
                }
            }
        });
    }

    /**
     * Checks that the given value is above or equal to the lower bound of that property.
     * Ignores values at 0.0 as these are considered as if they were not tested.
     *
     * @param btProperty     Blood test property containing the lower bound value
     * @param bloodTestValue Current value of a blood test
     * @param lowValues      List containing blood test properties that are below their lower bound
     */
    private void checkLower(BloodTestProperties btProperty, double bloodTestValue, List<BloodTestProperties> lowValues) {
        if (!AttributeValidation.checkAboveLowerBound(btProperty.getLowerBound(), bloodTestValue) && bloodTestValue != 0.0) {
            lowValues.add(btProperty);
        }
    }

    /**
     * Checks that the given value is below or equal to the upper bound of that property
     *
     * @param btProperty     Blood test property containing the upper bound value
     * @param bloodTestValue Current value of a blood test
     * @param highValues     List containing blood test properties that are above their upper bound
     */
    private void checkHigher(BloodTestProperties btProperty, double bloodTestValue, List<BloodTestProperties> highValues) {
        if (!AttributeValidation.checkBelowUpperBound(btProperty.getUpperBound(), bloodTestValue)) {
            highValues.add(btProperty);
        }
    }

    /**
     * Checks to see if each blood test property is within the upper and lower bounds.
     * If a property is not within those bounds, it is added to the corresponding list.
     * @param bloodTest blood test to check the bounds of
     */
    private void checkBounds(BloodTest bloodTest) {
        List<BloodTestProperties> lowValues = new ArrayList<>();
        List<BloodTestProperties> highValues = new ArrayList<>();

        checkLower(BloodTestProperties.RED_BLOOD_CELL, bloodTest.getRedBloodCellCount(), lowValues);
        checkLower(BloodTestProperties.WHITE_BLOOD_CELL, bloodTest.getWhiteBloodCellCount(), lowValues);
        checkLower(BloodTestProperties.HAEMOGLOBIN, bloodTest.getHaemoglobinLevel(), lowValues);
        checkLower(BloodTestProperties.PLATELETS, bloodTest.getPlatelets(), lowValues);
        checkLower(BloodTestProperties.GLUCOSE, bloodTest.getGlucoseLevels(), lowValues);
        checkLower(BloodTestProperties.HAEMATOCRIT, bloodTest.getHaematocrit(), lowValues);
        checkLower(BloodTestProperties.MEAN_CELL_VOLUME, bloodTest.getMeanCellVolume(), lowValues);
        checkLower(BloodTestProperties.MEAN_CELL_HAEMATOCRIT, bloodTest.getMeanCellHaematocrit(), lowValues);

        checkHigher(BloodTestProperties.RED_BLOOD_CELL, bloodTest.getRedBloodCellCount(), highValues);
        checkHigher(BloodTestProperties.WHITE_BLOOD_CELL, bloodTest.getWhiteBloodCellCount(), highValues);
        checkHigher(BloodTestProperties.HAEMOGLOBIN, bloodTest.getHaemoglobinLevel(), highValues);
        checkHigher(BloodTestProperties.PLATELETS, bloodTest.getPlatelets(), highValues);
        checkHigher(BloodTestProperties.GLUCOSE, bloodTest.getGlucoseLevels(), highValues);
        checkHigher(BloodTestProperties.HAEMATOCRIT, bloodTest.getHaematocrit(), highValues);
        checkHigher(BloodTestProperties.MEAN_CELL_VOLUME, bloodTest.getMeanCellVolume(), highValues);
        checkHigher(BloodTestProperties.MEAN_CELL_HAEMATOCRIT, bloodTest.getMeanCellHaematocrit(), highValues);

        bloodTest.setLowValues(lowValues);
        bloodTest.setHighValues(highValues);
    }

}
