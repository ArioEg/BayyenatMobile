package ir.najmossagheb.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by r.kiani on 05/22/2015.
 */
public class ConfigurationManager {

    SharedPreferences sharedPreferences;
    Context context;
    private static ConfigurationManager mInstance = null;

    private boolean autoRefresh;
    private String refreshFreq;
    private boolean drawScreenRect;
    private boolean isServiceStarted;

    public boolean isDrawScreenRect() {
        return drawScreenRect;
    }

    public static ConfigurationManager getInstance(Context context) {
        if(mInstance == null) mInstance =  new ConfigurationManager(context);
        mInstance.updateInstance();
        return mInstance;
    }

    public ConfigurationManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void updateInstance()
    {
        this.autoRefresh = sharedPreferences.getBoolean("prefAutoRefresh",false);
        this.refreshFreq = sharedPreferences.getString("prefChangeFrequency","30");
        this.drawScreenRect = sharedPreferences.getBoolean("prefDrawScreenRect",true);

        this.isServiceStarted = sharedPreferences.getBoolean("prefServiceStarted",false);
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean isAutoRefresh)
    {
        sharedPreferences.edit().putBoolean("prefAutoRefresh",isAutoRefresh).commit();
        this.autoRefresh = isAutoRefresh;
    }

    public String getRefreshFreq() {
        return refreshFreq;
    }

    public boolean isServiceStarted() {
        return isServiceStarted;
    }

    public void setServiceStarted(boolean isServiceStarted) {
        sharedPreferences.edit().putBoolean("prefServiceStarted",isServiceStarted).commit();
        this.isServiceStarted = isServiceStarted;
    }
}
