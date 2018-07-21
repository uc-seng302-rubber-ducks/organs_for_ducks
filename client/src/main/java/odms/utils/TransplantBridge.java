package odms.utils;

import com.mysql.jdbc.StringUtils;
import odms.commons.exception.ApiException;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransplantBridge extends Bifrost {

    public TransplantBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

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
    public List<TransplantDetails> getWaitingList(int startIndex, int count, String name, String region, Collection<Organs> organs) throws ApiException{
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
                url.append("&organs=").append(organ.name());
            }
        }
        Log.debug(url.toString());
        Request request = new Request.Builder().get()
                .header(TOKEN_HEADER, AppController.getInstance().getToken())
                .url(url.toString()).build();
        try (Response response =  client.newCall(request).execute()){
            if (200 < response.code() || response.code() > 299) {
                throw new ApiException(response.code(), "got response with code outside of 200 range");
            }
            return handler.decodeTransplantList(response);

        } catch (ApiException ex) {
            throw ex;
        } catch (IOException ex) {
            Log.warning("could not decode transplant list response from server", ex);
            return new ArrayList<>();
        }
    }

}
