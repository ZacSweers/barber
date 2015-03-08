package io.sweers.barber.sample;

import android.content.Intent;
import android.content.res.Resources;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import io.sweers.barber.sample.testing.TestPreferencesActivity;

/**
 * Preferences can be styled too!
 */
public class PreferenceTest extends ActivityUnitTestCase<TestPreferencesActivity> {

    private TestPreferencesActivity testPreferencesActivity;
    private Resources resources;

    public PreferenceTest() {
        super(TestPreferencesActivity.class);
    }

    public PreferenceTest(Class<TestPreferencesActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startActivity(new Intent(getInstrumentation().getTargetContext(), TestPreferencesActivity.class), null, null);
        testPreferencesActivity = getActivity();
        resources = testPreferencesActivity.getResources();
    }

    @SmallTest
    public void testAttributes() {
        assertEquals(resources.getColor(android.R.color.holo_red_dark), testPreferencesActivity.customPreference.titleColor);
        assertEquals(resources.getColor(android.R.color.holo_blue_dark), testPreferencesActivity.customPreference.summaryColor);
    }
}
