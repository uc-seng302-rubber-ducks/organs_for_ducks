package odms.bridge;

import com.google.gson.Gson;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collection;

public class BloodTestBridge extends Bifrost {

    private static final String USER = "user/";

    BloodTestBridge(OkHttpClient client) {
        super(client);
    }

    /**
     * Posts a blood test for a user
     *
     * @param bloodTest blood test to post
     * @param nhi nhi of the user to post the blood test for
     */
    public void postBloodtest(BloodTest bloodTest, String nhi){
        String url = ip + USER + nhi + " /bloodTest";
        RequestBody requestBody = RequestBody.create(json, new Gson().toJson(bloodTest));
        Request request = new Request.Builder().post(requestBody).url(url).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("POST", url));
    }

    /**
     * patches a blood test for a user
     *
     * @param bloodTest blood test to patch
     * @param nhi nhi of the user to patch the blood test for
     */
    public void patchBloodtest(BloodTest bloodTest, String nhi){
        String url = ip + USER + nhi +"/bloodTest/"+bloodTest.getBloodTestId();
        RequestBody requestBody = RequestBody.create(json, new Gson().toJson(bloodTest));
        Request request = new Request.Builder().patch(requestBody).url(url).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("PATCH", url));
    }

    /**
     * deletes a blood test for a user
     *
     * @param bloodTestId blood test id to delete
     * @param nhi nhi of the user to delete the blood test for
     */
    public void deleteBloodtest(String bloodTestId , String nhi){
        String url = ip + USER + nhi +" /bloodTest/"+ bloodTestId;
        Request request = new Request.Builder().delete().url(url).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("DELETE", url));
    }

    /**
     * Returns a single blood test
     * @param bloodTestId id od the blood test
     * @param nhi nhi of the user associated with the bloodtest
     * @return The blood test if it can be parsed; null otherwise
     */
    public BloodTest getBloodTest(String bloodTestId , String nhi) {
        String url = ip + USER + nhi + " /bloodTest/" + bloodTestId;
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
     * Returns all  blood tests for a user
     * @param nhi nhi of the user associated with the bloodtests
     * @return The blood tests if it can be parsed; null otherwise
     */
    public Collection<BloodTest> getBloodTests(String nhi) {
        String url = ip + USER + nhi + " /bloodTests/";
        Request request = new Request.Builder().get().url(url).build();
        Collection<BloodTest> toReturn = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                toReturn = handler.decodeBloodTests(response.body().string());
            }
        } catch (IOException e) {
            Log.warning("Could not GET from " + url,e);
        }
        return toReturn;
    }
}
