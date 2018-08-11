package odms.bridge;

import com.mysql.jdbc.StringUtils;
import javafx.collections.ObservableList;
import odms.commons.exception.ApiException;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class AvailableOrgansBridge extends Bifrost {
    public AvailableOrgansBridge(OkHttpClient client) {
        super(client);
    }

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
            url.append("&city=").append(city);
        }

        if (!StringUtils.isNullOrEmpty(country)){
            url.append("&country").append(city);
        }

        Request request = new Request.Builder().get()
                .header(tokenHeader, AppController.getInstance().getToken())
                .url(url.toString()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe("Failed to GET the list of available organs", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (200 < response.code() || response.code() > 299) {
                    throw new ApiException(response.code(), "got response with code outside of 200 range");
                }

                List<AvailableOrganDetail> availableOrgansDetails = handler.decodeAvailableOrgansList(response);
                for (AvailableOrganDetail detail : availableOrgansDetails) {
                    detail.generateProgressTask();
                }
                observableList.addAll(availableOrgansDetails);
            }
        });

    }
}
