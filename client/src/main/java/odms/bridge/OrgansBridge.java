package odms.bridge;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.exception.ApiException;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.Log;
import odms.commons.utils.OrganSorter;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class OrgansBridge extends Bifrost {
    public OrgansBridge(OkHttpClient client) {
        super(client);
    }

    /**
     * Gets all the organs available for donation by making a request to the server and populating the observable list.
     *
     * @param startIndex     the position to start obtaining items from
     * @param count          how many entries to obtain
     * @param organ          if specified, return only organs of that type
     * @param region         if specified, return only organs located within that region
     * @param bloodType      if specified, return only organs of that blood type
     * @param city           if specified, return only organs in that city
     * @param country        if specified, return only organs in that country
     * @param observableList observable list to populate.
     */
    public void getAvailableOrgansList(int startIndex, int count, String organ, String region, String bloodType, String city, String country, ObservableList<AvailableOrganDetail> observableList) {
        StringBuilder url = new StringBuilder(ip);
        url.append("/availableOrgans?count=").append(count);
        url.append("&startIndex=").append(startIndex);

        if (!StringUtils.isNullOrEmpty(organ)) {
            url.append("&organ=").append(organ);
        }

        if (!StringUtils.isNullOrEmpty(region)) {
            url.append("&region=").append(region);
        }

        if (!StringUtils.isNullOrEmpty(bloodType)) {
            url.append("&bloodType=").append(bloodType);
        }

        if (!StringUtils.isNullOrEmpty(city)) {
            url.append("&city=").append(city);
        }

        if (!StringUtils.isNullOrEmpty(country)){
            url.append("&country=").append(country);
        }
        Request request = new Request.Builder().get()
                .header(tokenHeader, AppController.getInstance().getToken())
                .url(url.toString().replaceAll(" ", "_")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe("Failed to GET the list of available organs", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (200 < response.code() || response.code() > 299) {
                    throw new ApiException(response.code(), "got response with code outside of 200 range, Code: "+ response.code());
                }

                List<AvailableOrganDetail> availableOrgansDetails = handler.decodeAvailableOrgansList(response);
                if (ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equalsIgnoreCase("false")) {
                    for (AvailableOrganDetail detail : availableOrgansDetails) {
                        detail.generateProgressTask();
                    }
                }
                Platform.runLater(()-> observableList.addAll(availableOrgansDetails));
            }
        });

    }

    /**
     * Gets the potential matches list from the server by firing a HTTP request.
     * @param startIndex the position to start obtaining items from
     * @param count how many entries to obtain
     * @param donorNhi user who is donating the organ
     * @param organToDonate Available organ detail to identify the map entry of the response
     * @param observableList the observable list to populate the potential matches with
     */
    public void getMatchingOrgansList(int startIndex, int count, String donorNhi, AvailableOrganDetail organToDonate,
                                      ObservableList<TransplantDetails> observableList) {
        String url = ip + "/matchingOrgans?" +
                "count=" + count +
                "&organ=" + organToDonate.getOrgan().toString() +
                "&startIndex=" + startIndex +
                "&donorNhi=" + donorNhi;


        Request request = new Request.Builder().get()
                .header(tokenHeader, AppController.getInstance().getToken())
                .url(url.replaceAll(" ", "_")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe("Failed to GET the list of matching organs", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (200 < response.code() || response.code() > 299) {
                    throw new ApiException(response.code(), "got response with code outside of 200 range");
                }

                List<TransplantDetails> matchingTransplants = handler.decodeMatchingOrgansList(response);

                Platform.runLater(() ->{
                    observableList.clear();
                    observableList.addAll( OrganSorter.sortOrgansIntoRankedOrder(organToDonate, matchingTransplants));
                } );

            }
        });

    }

    /**
     * Gets the disqualified organs for a user
     * @param nhi user to get organs for
     * @param observableDisqualifications Observable list to populate with the disqualified organs.
     */
    public void getDisqualifiedOrgans(String nhi, ObservableList<OrgansWithDisqualification> observableDisqualifications) {
        String url = ip + "/users/" + nhi + "/disqualified";

        Request request = new Request.Builder().get().header(tokenHeader, AppController.getInstance().getToken()).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe("Failed to GET the list of disqualified organs for user " + nhi, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (200 < response.code() || response.code() > 299) {
                    throw new ApiException(response.code(), "got response with code outside of 200 range");
                }

                ResponseBody body = response.body();
                if (body == null) {
                    Log.warning("A null response body was returned to the user");
                    return;
                }
                String bodyString = response.body().string();

                List<OrgansWithDisqualification> disqualifications = handler.decodeDisqualified(bodyString);

                Platform.runLater(() -> {
                    observableDisqualifications.clear();
                    observableDisqualifications.addAll(disqualifications);
                });
            }
        });
    }

    /**
     * Updates or creates a user's disqualified organs
     * @param nhi User to update disqualified organs for
     * @param disqualifications List of disqualified organs to send to the database
     */
    public void postDisqualifiedOrgans(String nhi, List<OrgansWithDisqualification> disqualifications) {
        String url = ip + "/users/" + nhi + "/disqualified";
        RequestBody body = RequestBody.create(json, new Gson().toJson(disqualifications));
        Request request = new Request.Builder().header(tokenHeader, AppController.getInstance().getToken())
                .url(url).post(body).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("POST", url));
    }

    /**
     * Deletes a set of disqualified organs from the user
     * @param nhi User to delete disqualified organs from
     * @param disqualifications List of disqualified organs to delete from the database
     */
    public void deleteDisqualifiedOrgans(String nhi ,List<OrgansWithDisqualification> disqualifications) {
        String url = ip + "/users/" + nhi + "/disqualified";
        RequestBody body = RequestBody.create(json, new Gson().toJson(disqualifications));
        Request request = new Request.Builder().get().header(tokenHeader, AppController.getInstance().getToken())
                .url(url).delete(body).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("DELETE", url));
    }







}
