package odms.bridge;

import com.mysql.jdbc.StringUtils;
import odms.commons.exception.ApiException;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class TransplantBridge extends Bifrost {
    public TransplantBridge(OkHttpClient client) {
        super(client);
    }


    /**
     * sends a request to the /transplantList endpoint with filters as set up below. Results are passed out via the callback
     *
     * @param startIndex number of entries to skip (for pagination)
     * @param count      number of results to return
     * @param name       name (or partial name) to search by
     * @param region     region to search by
     * @param organs     only return results for the selected organs
     */
    public void getWaitingList(int startIndex, int count, String name, String region, Collection<Organs> organs) {
        StringBuilder url = new StringBuilder(ip);
        url.append("/transplantList?count=").append(count);
        url.append("&startIndex=").append(startIndex);
        if (!StringUtils.isNullOrEmpty(name)) {
            url.append("&name=").append(name);
        }
        if (!StringUtils.isNullOrEmpty(region)) {
            url.append("&region=").append(region);
        }

        if (!organs.isEmpty()) {
            for (Organs organ : organs) {
                //repeated values are interpreted by the server as an array
                url.append("&organs=").append(organ.name().replaceAll(" ", "_"));
            }
        }
        Log.debug(url.toString());
        Request request = new Request.Builder().get()
                .header(tokenHeader, AppController.getInstance().getToken())
                .url(url.toString()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not get transplant list", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (200 < response.code() || response.code() > 299) {
                    throw new ApiException(response.code(), "Response code: " + response.code());
                }

                List<TransplantDetails> transplantDetails = handler.decodeTransplantList(response);

                for (TransplantDetails transplantDetail : transplantDetails) {
                    AppController.getInstance().addTransplant(transplantDetail);
                                    }
            }
        });
    }

}
