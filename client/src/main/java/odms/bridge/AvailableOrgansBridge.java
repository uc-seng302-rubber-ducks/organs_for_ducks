package odms.bridge;

import com.mysql.jdbc.StringUtils;
import odms.commons.exception.ApiException;
import odms.commons.model._enum.Organs;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.Collection;

public class AvailableOrgansBridge extends Bifrost {
    public AvailableOrgansBridge(OkHttpClient client) {
        super(client);
    }

    public void getAvailableOrgansList(int startIndex, int count, Collection<Organs> organs, String region, String bloodType, String city, String country) {
        StringBuilder url = new StringBuilder(ip);
        url.append("/availableOrgans?count=").append(count);
        url.append("&startIndex=").append(startIndex);

        if (!organs.isEmpty()) {
            for (Organs organ : organs){
                url.append("&organs=").append(organ.name());
            }
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
                .header(TOKEN_HEADER, AppController.getInstance().getToken())
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

                //todo: see how we're storing the info client side - jen 8/8
            }
        });

    }
}
