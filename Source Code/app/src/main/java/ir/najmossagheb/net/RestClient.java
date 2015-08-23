package ir.najmossagheb.net;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import ir.najmossagheb.util.NetworkUtil;

/**
 * Created by r.kiani on 06/02/2015.
 */
public class RestClient {
    public static AsyncHttpClient client = new AsyncHttpClient();

    public static void post (Context c, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (!NetworkUtil.getNetworkState(c)) {
            return;
        }
        client.post(c, url, params, responseHandler);
    }
}
