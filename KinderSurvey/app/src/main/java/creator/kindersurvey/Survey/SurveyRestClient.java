package creator.kindersurvey.Survey;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import creator.kindersurvey.util.AppConstants;

/**
 * Created by CreatorJeslin on 11/11/17.
 */

public class SurveyRestClient {
    //    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static final String BASE_URL = AppConstants.URL;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
        client.addHeader("Content-Type", "application/json");
        client.addHeader("authorization", "Basic YWRtaW46YWRtaW4=");
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
        client.addHeader("Content-Type", "application/json");
        client.addHeader("authorization", "Basic YWRtaW46YWRtaW4=");
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
