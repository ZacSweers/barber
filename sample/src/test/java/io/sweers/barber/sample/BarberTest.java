package io.sweers.barber.sample;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import io.sweers.barber.sample.testing.ChildTestView;
import io.sweers.barber.sample.testing.DefaultsTestView;
import io.sweers.barber.sample.testing.GrandChildTestView;
import io.sweers.barber.sample.testing.TestTextView;
import io.sweers.barber.sample.testing.TestView;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BarberTest {

    private Context context;
    private TestView testView;
    private ChildTestView childTestView;
    private GrandChildTestView grandChildTestView;
    private TestTextView testTextView;
    private DefaultsTestView defaultsTestView;
    private Resources res;

    @Before
    public void setUp() throws Exception {
        context = Robolectric.buildActivity(Activity.class).create().get();
        testView = (TestView) View.inflate(context, R.layout.test_view, null);
        childTestView = (ChildTestView) View.inflate(context, R.layout.child_test_view, null);
        grandChildTestView = (GrandChildTestView) View.inflate(context, R.layout.grand_child_test_view, null);
        testTextView = (TestTextView) View.inflate(context, R.layout.test_textview, null);
        defaultsTestView = (DefaultsTestView) View.inflate(context, R.layout.defaults_test_view, null);
        res = context.getResources();
    }

    @Test
    public void testBoolean() {
        Assert.assertTrue(testView.testBoolean);
        Assert.assertTrue(defaultsTestView.testBoolean);
    }

    @Test
    public void testInt() {
        Assert.assertEquals(3, testView.testInt);
        Assert.assertEquals(3, defaultsTestView.testInt);
    }

    @Test
    public void testInteger() {
        Assert.assertEquals(12, testView.testInteger);
        Assert.assertEquals(3, defaultsTestView.testInteger);
    }

    @Test
    public void testColor() {
        Assert.assertEquals(res.getColor(android.R.color.holo_red_dark), testView.testColor);
        Assert.assertEquals(res.getColor(android.R.color.holo_red_dark), defaultsTestView.testColor);
    }

    @Test
    public void testFloat() {
        Assert.assertEquals(0.75f, testView.testFloat, 0.01f);
        Assert.assertEquals(0.75f, defaultsTestView.testFloat, 0.01f);
    }

    @Test
    public void testCharSequence() {
        Assert.assertNotNull(testView.testCharSequence);
        Assert.assertEquals("charsequence", testView.testCharSequence);
        Assert.assertNotNull(defaultsTestView.testCharSequence);
        Assert.assertEquals("charsequence", defaultsTestView.testCharSequence);
    }

    @Test
    public void testString() {
        Assert.assertNotNull(testView.testString);
        Assert.assertEquals("this is a string", testView.testString);
        Assert.assertNotNull(defaultsTestView.testString);
        Assert.assertEquals("this is a string", defaultsTestView.testString);
    }

    @Test
    public void testTextArray() {
        Assert.assertNotNull(testView.testTextArray);
        Assert.assertTrue(Arrays.deepEquals(testView.testTextArray, res.getTextArray(R.array.buzzwords)));
        Assert.assertNotNull(defaultsTestView.testTextArray);
        Assert.assertTrue(Arrays.deepEquals(defaultsTestView.testTextArray, res.getTextArray(R.array.buzzwords)));
    }

    @Test
    public void testColorStateList() {
        int defaultColor = res.getColor(android.R.color.holo_blue_bright);
        int pressedColor = res.getColor(android.R.color.holo_blue_dark);
        int[] pressedState = {android.R.attr.state_pressed};
        int[] defaultState = {};

        Assert.assertNotNull(testView.testColorStateList);
        Assert.assertTrue(testView.testColorStateList.isStateful());
        Assert.assertEquals(pressedColor, testView.testColorStateList.getColorForState(pressedState, defaultColor));
        Assert.assertEquals(defaultColor, testView.testColorStateList.getColorForState(defaultState, defaultColor));

        Assert.assertNotNull(defaultsTestView.testColorStateList);
        Assert.assertTrue(defaultsTestView.testColorStateList.isStateful());
        Assert.assertEquals(pressedColor, defaultsTestView.testColorStateList.getColorForState(pressedState, defaultColor));
        Assert.assertEquals(defaultColor, defaultsTestView.testColorStateList.getColorForState(defaultState, defaultColor));
    }

    @Test
    public void testDrawable() {
        Assert.assertNotNull(testView.testDrawable);
        Assert.assertNotNull(defaultsTestView.testDrawable);
    }

    @Test
    public void testFraction() {
        Assert.assertEquals(res.getFraction(R.fraction.fraction, 2, 2), testView.testFractionBase, 0.1f);
        Assert.assertEquals(res.getFraction(R.fraction.parent_fraction, 2, 2), testView.testFracionPBase, 0.1f);
        Assert.assertEquals(res.getFraction(R.fraction.fraction, 2, 2), defaultsTestView.testFractionBase, 0.1f);
        Assert.assertEquals(res.getFraction(R.fraction.parent_fraction, 2, 2), defaultsTestView.testFracionPBase, 0.1f);
    }

    @Test
    public void testDimension() {
        Assert.assertEquals(res.getDimension(R.dimen.test_dimen), testView.testDimension, 0.01f);
        Assert.assertEquals(res.getDimension(R.dimen.test_dimen), defaultsTestView.testDimension, 0.01f);
    }

    @Test
    public void testDimensionPixelSize() {
        Assert.assertEquals(res.getDimensionPixelSize(R.dimen.test_dimen), testView.testDimensionPixelSize);
        Assert.assertEquals(res.getDimensionPixelSize(R.dimen.test_dimen), defaultsTestView.testDimensionPixelSize);
    }

    @Test
    public void testDimensionPixelOffset() {
        Assert.assertEquals(res.getDimensionPixelOffset(R.dimen.test_dimen), testView.testDimensionPixelOffset);
        Assert.assertEquals(res.getDimensionPixelOffset(R.dimen.test_dimen), defaultsTestView.testDimensionPixelOffset);
    }

    @Test
    public void testResId() {
        Assert.assertEquals(R.array.buzzwords, testView.testResId);
        Assert.assertEquals(R.array.buzzwords, defaultsTestView.testResId);
    }

    // These are borked by Robolectric right now.
    // https://github.com/robolectric/robolectric/issues/1670
    @Test
    public void testNonResString() {
//        Assert.assertEquals("Hello", testView.testNonResString1);
//        Assert.assertNull(testView.testNonResString2);
//        Assert.assertEquals("Hello", defaultsTestView.testNonResString1);
//        Assert.assertEquals("Hello", defaultsTestView.testNonResString2);
    }

    @Test
    public void testInheritance() {
        Assert.assertTrue(childTestView.testBoolean);
        Assert.assertNull(childTestView.testString);
        Assert.assertEquals(3, childTestView.testInt);
        Assert.assertEquals(3, childTestView.childInt);
        Assert.assertTrue(childTestView.childBoolean);
        Assert.assertEquals("child", childTestView.childString);
        Assert.assertEquals("grandChild", grandChildTestView.grandChildString);
    }

    @Test
    public void testRequired() {
        try {
            View.inflate(context, R.layout.required_styled_attr_test_view, null);
            Assert.fail("Expected missing required attribute exception");
        } catch (InflateException e) {
            // Success
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testAndroidAttr() {
        Assert.assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, testTextView.layoutHeight);
        Assert.assertTrue(testTextView.textAllCaps);
        Assert.assertEquals(R.color.material_blue_grey_800, testTextView.textColor);
        Assert.assertEquals(3, testTextView.maxLines);
        Assert.assertEquals(1.2f, testTextView.lineSpacingMultiplier, 0.01f);

        // These are currently borked because their implementations in Robolectric are wrong.

        // https://github.com/robolectric/robolectric/issues/1669
//        Assert.assertEquals("0x1", testTextView.textStyle);
        Assert.assertEquals("bold", testTextView.textStyle);

        // https://github.com/robolectric/robolectric/issues/1668
//        Assert.assertEquals(Color.parseColor("#40ffffff"), testTextView.hintColor);
    }
}
