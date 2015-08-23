package ir.najmossagheb.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import ir.najmossagheb.R;

/**
 * Created by r.kiani on 05/20/2015.
 */
public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        //TODO: Change colors...
    }
}
