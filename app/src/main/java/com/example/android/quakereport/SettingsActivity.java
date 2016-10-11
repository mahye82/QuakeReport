package com.example.android.quakereport;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Inflate the settings_main.xml which contains a PreferenceScreen root View.
            // This creates a list of Preferences which can be clicked and edited, with the
            // value inputted by the user then stored with an associated key.
            // Currently, there is only one Preference for 'Minimum Magnitude.
            addPreferencesFromResource(R.xml.settings_main);
        }
    }
}
