package ir.najmossagheb.net;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.najmossagheb.model.Hadith;
import ir.najmossagheb.model.NodeCategory;
import ir.najmossagheb.model.NodeWallpaper;

/**
 * Created by r.kiani on 06/02/2015.
 */
public class RestClientHandler extends AsyncHttpResponseHandler {
    private final OnRestResponseHandler mOnRestResponseHandler;
    private final int mode;

    public static final int REST_WP = 1;
    public static final int REST_HD = 2;

    public interface OnRestResponseHandler {
        public void onResponseWP (ArrayList<NodeWallpaper> response);
        public void onResponseHD (ArrayList<Hadith> response);
    }

    public RestClientHandler(OnRestResponseHandler listener, int mode) {
        mOnRestResponseHandler = listener;
        this.mode = mode;
    }

    @Override
    public void onSuccess(String s) {
        try {
            JSONArray array = new JSONArray(s);

            if(mode == REST_WP) {
                final ArrayList<NodeWallpaper> data = new ManifestParser().getWPResults(array);
                if (data == null) {
                    this.onFailure(new Throwable("Manifest Could Not Be Parsed!"), s);
                    return;
                }
                mOnRestResponseHandler.onResponseWP(data);
            }else if(mode == REST_HD) {
                final ArrayList<Hadith> data = new ManifestParser().getHDResults(array);
                if (data == null) {
                    this.onFailure(new Throwable("Manifest Could Not Be Parsed!"), s);
                    return;
                }
                mOnRestResponseHandler.onResponseHD(data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(Throwable throwable, String s) {
        this.mOnRestResponseHandler.onResponseWP(null);
    }
}
