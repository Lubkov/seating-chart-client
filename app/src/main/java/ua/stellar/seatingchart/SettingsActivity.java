package ua.stellar.seatingchart;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import ua.stellar.ua.test.seatingchart.R;

public class SettingsActivity extends PreferenceActivity {

    private final String LOG_TAG = "RESERVE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "Settings activity created");
        addPreferencesFromResource(R.xml.settings_pref);
    }
}
