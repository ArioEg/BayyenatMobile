package ir.najmossagheb.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.najmossagheb.db.BayyenatDbHelper;
import ir.najmossagheb.model.Hadith;
import ir.najmossagheb.model.NodeCategory;
import ir.najmossagheb.model.NodeWallpaper;

public class ManifestParser {

	private static final String TAG = "ManifestParser";

    public ArrayList<NodeWallpaper> getWPResults(JSONArray array)
    {
        try {
            ArrayList<NodeWallpaper> nodeList = new ArrayList<NodeWallpaper>();

            NodeWallpaper wp;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                wp = new NodeWallpaper();
                wp.id = getInt(obj,"fid");
                wp.name = getString(obj,"name");
                wp.thumbUrl = getString(obj,"thumbUrl");
                wp.author = getString(obj,"author");
                wp.url = getString(obj,"url");
                wp.isnew = true;

                nodeList.add(wp);
            }
            return nodeList;
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    public ArrayList<Hadith> getHDResults(JSONArray array)
    {
        try {
            ArrayList<Hadith> nodeList = new ArrayList<Hadith>();

            Hadith hd;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                hd = new Hadith();
                hd.setId(getInt(obj,"quote_id"));
                hd.setText(getString(obj,"quote"));
                hd.setAuthor(getString(obj,"author"));
                hd.setSource(getString(obj,"source"));
                hd.setNew(true);
                hd.setTags(getString(obj,"tags"));

                nodeList.add(hd);
            }
            return nodeList;
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return null;
        }
    }


    public String getString (JSONObject obj, String tag) throws JSONException {
		return (obj != null && !obj.isNull(tag)) ? obj.getString(tag) : null;
	}

	public int getInt (JSONObject obj, String tag) throws JSONException {
		return (obj != null && !obj.isNull(tag)) ? obj.getInt(tag) : -1;
	}

	public boolean getBoolean (JSONObject obj, String tag) throws JSONException {
		return (obj != null && !obj.isNull(tag)) ? obj.getBoolean(tag) : false;
	}

	public JSONObject getJSONObject (JSONObject obj, String tag) throws JSONException {
		return (obj != null && !obj.isNull(tag)) ? obj.getJSONObject(tag) : null;
	}

	public JSONObject getJSONObject (JSONArray obj, int x) throws JSONException {
		return (obj != null && !obj.isNull(x)) ? obj.getJSONObject(x) : null;
	}

	public JSONArray getJSONArray (JSONObject obj, String tag) throws JSONException {
		return (obj != null && !obj.isNull(tag)) ? obj.getJSONArray(tag) : null;
	}
}
