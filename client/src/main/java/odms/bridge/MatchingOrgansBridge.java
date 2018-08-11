package odms.bridge;

import com.mysql.jdbc.StringUtils;
import javafx.collections.ObservableList;
import odms.commons.exception.ApiException;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MatchingOrgansBridge extends Bifrost {
    public MatchingOrgansBridge(OkHttpClient client) {
        super(client);
    }

    public void getMatchingOrgansList(String organ, String bloodType, String city, String region, String country, ObservableList<Map<AvailableOrganDetail, List<TransplantDetails>>> observableList) {
        StringBuilder url = new StringBuilder(ip);
        url.append("/matchingOrgans?");

        if (!StringUtils.isNullOrEmpty(organ)) {
            url.append("&organ=").append(organ);
        }

        if (!StringUtils.isNullOrEmpty(bloodType)) {
            url.append("&bloodType=").append(bloodType);
        }

        if (!StringUtils.isNullOrEmpty(city)) {
            url.append("&city=").append(city);
        }

        if (!StringUtils.isNullOrEmpty(region)) {
            url.append("&region=").append(region);
        }

        if (!StringUtils.isNullOrEmpty(country)){
            url.append("&country").append(country);
        }

        url = url.deleteCharAt(url.indexOf("&")); //removes first occurrence of "&"

        Request request = new Request.Builder().get()
                .header(TOKEN_HEADER, AppController.getInstance().getToken())
                .url(url.toString()).build();
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

                Map<AvailableOrganDetail, List<TransplantDetails>> matchingOrgansList = handler.decodeMatchingOrgansList(response);
                observableList.addAll(matchingOrgansList);
            }
        });

    }
}
