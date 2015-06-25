package io.sweers.barber.sample;

import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.sweers.barber.sample.testing.TestPreferencesActivity;

/**
 * Preferences can be styled too!
 *
 * This is currently borked by Robolectric because its ShadowPreference doesn't implement getContext()
 * https://github.com/robolectric/robolectric/issues/1671
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PreferenceTest {

    private TestPreferencesActivity testPreferencesActivity;
    private Resources resources;

    @Before
    public void setUp() throws Exception {
        testPreferencesActivity = Robolectric.buildActivity(TestPreferencesActivity.class).create().get();
        resources = testPreferencesActivity.getResources();
    }

    @Test
    public void testAttributes() {
//        Assert.assertEquals(resources.getColor(android.R.color.holo_red_dark), testPreferencesActivity.customPreference.titleColor);
//        Assert.assertEquals(resources.getColor(android.R.color.holo_blue_dark), testPreferencesActivity.customPreference.summaryColor);
    }
}
