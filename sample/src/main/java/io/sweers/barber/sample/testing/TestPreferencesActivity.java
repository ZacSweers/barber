package io.sweers.barber.sample.testing;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import io.sweers.barber.sample.R;

/**
 * Preferences can be styled too!
 */
@SuppressWarnings("deprecation")
public class TestPreferencesActivity extends PreferenceActivity {

    public CustomPreference customPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.test_prefs);
        customPreference = (CustomPreference) findPreference("custom_pref");
    }
}
