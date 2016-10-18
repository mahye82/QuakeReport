package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Inflate the settings_main.xml which contains a PreferenceScreen root View.
            // This creates a list of Preferences which can be clicked and edited, with the
            // value inputted by the user then stored with an associated key.
            // Currently, there is only one Preference for 'Minimum Magnitude.
            addPreferencesFromResource(R.xml.settings_main);

            // Find the minimum magnitude Preference
            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            // Update the summary of this Preference to the value
            bindPreferenceSummaryToValue(minMagnitude);
        }

        /**
         * Helper method which sets the current {@link EarthquakePreferenceFragment} instance as the
         * listener on each preference. It also takes the value of the {@param preference} stored in
         * {@link android.content.SharedPreferences} on the device, and displays that in the
         * preference summary, so that the user can see the current value of the preference.
         * @param preference that needs to be registered to a listener and displayed in the
         *                   preference summary
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the current EarthquakePreferenceFragment as the listener for the preference
            preference.setOnPreferenceChangeListener(this);

            // Get the default SharedPreferences on the device
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            // Get the value for this preference as a String
            String preferenceString = preferences.getString(preference.getKey(), "");

            // Invoke the callback method to say which preference has changed, and pass in the value
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            // Get the value for the Preference and set the summary for it
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    }
}
