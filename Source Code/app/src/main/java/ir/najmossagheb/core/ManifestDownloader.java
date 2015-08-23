package ir.najmossagheb.core;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by r.kiani on 06/02/2015.
 */
public class ManifestDownloader {
    void getHadithManifest()
    {
        RequestParams params = new RequestParams();
        params.put("action","quotescollectionlist");
        params.put("orderby","quote_id");
        params.put("order","ASC");
        params.put("start","0");
        params.put("num_quotos","100");

    }
}
