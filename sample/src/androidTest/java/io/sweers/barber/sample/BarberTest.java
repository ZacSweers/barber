package io.sweers.barber.sample;

import android.content.res.Resources;
import android.graphics.Color;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import io.sweers.barber.sample.testing.ChildTestView;
import io.sweers.barber.sample.testing.DefaultsTestView;
import io.sweers.barber.sample.testing.GrandChildTestView;
import io.sweers.barber.sample.testing.TestTextView;
import io.sweers.barber.sample.testing.TestView;

public class BarberTest extends AndroidTestCase {

    private TestView testView;
    private ChildTestView childTestView;
    private GrandChildTestView grandChildTestView;
    private TestTextView testTextView;
    private DefaultsTestView defaultsTestView;
    private Resources res;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testView = (TestView) View.inflate(getContext(), R.layout.test_view, null);
        childTestView = (ChildTestView) View.inflate(getContext(), R.layout.child_test_view, null);
        grandChildTestView = (GrandChildTestView) View.inflate(getContext(), R.layout.grand_child_test_view, null);
        testTextView = (TestTextView) View.inflate(getContext(), R.layout.test_textview, null);
        defaultsTestView = (DefaultsTestView) View.inflate(getContext(), R.layout.defaults_test_view, null);
        res = getContext().getResources();
    }

    @SmallTest
    public void testBoolean() {
        assertTrue(testView.testBoolean);
        assertTrue(defaultsTestView.testBoolean);
    }

    @SmallTest
    public void testInt() {
        assertEquals(3, testView.testInt);
        assertEquals(3, defaultsTestView.testInt);
    }

    @SmallTest
    public void testInteger() {
        assertEquals(12, testView.testInteger);
        assertEquals(3, defaultsTestView.testInteger);
    }

    @SmallTest
    public void testColor() {
        assertEquals(res.getColor(android.R.color.holo_red_dark), testView.testColor);
        assertEquals(res.getColor(android.R.color.holo_red_dark), defaultsTestView.testColor);
    }

    @SmallTest
    public void testFloat() {
        assertEquals(0.75f, testView.testFloat);
        assertEquals(0.75f, defaultsTestView.testFloat);
    }

    @SmallTest
    public void testCharSequence() {
        assertNotNull(testView.testCharSequence);
        assertEquals("charsequence", testView.testCharSequence);
        assertNotNull(defaultsTestView.testCharSequence);
        assertEquals("charsequence", defaultsTestView.testCharSequence);
    }

    @SmallTest
    public void testString() {
        assertNotNull(testView.testString);
        assertEquals("this is a string", testView.testString);
        assertNotNull(defaultsTestView.testString);
        assertEquals("this is a string", defaultsTestView.testString);
    }

    @SmallTest
    public void testTextArray() {
        assertNotNull(testView.testTextArray);
        assertTrue(Arrays.deepEquals(testView.testTextArray, res.getTextArray(R.array.buzzwords)));
        assertNotNull(defaultsTestView.testTextArray);
        assertTrue(Arrays.deepEquals(defaultsTestView.testTextArray, res.getTextArray(R.array.buzzwords)));
    }

    @SmallTest
    public void testColorStateList() {
        int defaultColor = res.getColor(android.R.color.holo_blue_bright);
        int pressedColor = res.getColor(android.R.color.holo_blue_dark);
        int[] pressedState = {android.R.attr.state_pressed};
        int[] defaultState = {};

        assertNotNull(testView.testColorStateList);
        assertTrue(testView.testColorStateList.isStateful());
        assertEquals(pressedColor, testView.testColorStateList.getColorForState(pressedState, defaultColor));
        assertEquals(defaultColor, testView.testColorStateList.getColorForState(defaultState, defaultColor));

        assertNotNull(defaultsTestView.testColorStateList);
        assertTrue(defaultsTestView.testColorStateList.isStateful());
        assertEquals(pressedColor, defaultsTestView.testColorStateList.getColorForState(pressedState, defaultColor));
        assertEquals(defaultColor, defaultsTestView.testColorStateList.getColorForState(defaultState, defaultColor));
    }

    @SmallTest
    public void testDrawable() {
        assertNotNull(testView.testDrawable);
        assertNotNull(defaultsTestView.testDrawable);
    }

    @SmallTest
    public void testFraction() {
        assertEquals(res.getFraction(R.fraction.fraction, 2, 2), testView.testFractionBase);
        assertEquals(res.getFraction(R.fraction.parent_fraction, 2, 2), testView.testFracionPBase);
        assertEquals(res.getFraction(R.fraction.fraction, 2, 2), defaultsTestView.testFractionBase);
        assertEquals(res.getFraction(R.fraction.parent_fraction, 2, 2), defaultsTestView.testFracionPBase);
    }

    @SmallTest
    public void testDimension() {
        assertEquals(res.getDimension(R.dimen.test_dimen), testView.testDimension, 0.01f);
        assertEquals(res.getDimension(R.dimen.test_dimen), defaultsTestView.testDimension, 0.01f);
    }

    @SmallTest
    public void testDimensionPixelSize() {
        assertEquals(res.getDimensionPixelSize(R.dimen.test_dimen), testView.testDimensionPixelSize);
        assertEquals(res.getDimensionPixelSize(R.dimen.test_dimen), defaultsTestView.testDimensionPixelSize);
    }

    @SmallTest
    public void testDimensionPixelOffset() {
        assertEquals(res.getDimensionPixelOffset(R.dimen.test_dimen), testView.testDimensionPixelOffset);
        assertEquals(res.getDimensionPixelOffset(R.dimen.test_dimen), defaultsTestView.testDimensionPixelOffset);
    }

    @SmallTest
    public void testResId() {
        assertEquals(R.array.buzzwords, testView.testResId);
        assertEquals(R.array.buzzwords, defaultsTestView.testResId);
    }

    @SmallTest
    public void testNonResString() {
        assertEquals("Hello", testView.testNonResString1);
        assertNull(testView.testNonResString2);
        assertEquals("Hello", defaultsTestView.testNonResString1);
        assertEquals("Hello", defaultsTestView.testNonResString2);
    }

    @SmallTest
    public void testInheritance() {
        assertTrue(childTestView.testBoolean);
        assertNull(childTestView.testString);
        assertEquals(3, childTestView.testInt);
        assertEquals(3, childTestView.childInt);
        assertTrue(childTestView.childBoolean);
        assertEquals("child", childTestView.childString);
        assertEquals("grandChild", grandChildTestView.grandChildString);
    }

    @SmallTest
    public void testRequired() {
        try {
            View.inflate(getContext(), R.layout.required_styled_attr_test_view, null);
            fail("Expected missing required attribute exception");
        } catch (InflateException e) {
            // Success
            assertTrue(true);
        }
    }

    @SmallTest
    public void testAndroidAttr() {
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, testTextView.layoutHeight);
        assertTrue(testTextView.textAllCaps);
        assertEquals("0x1", testTextView.textStyle);
        assertEquals(R.color.material_blue_grey_800, testTextView.textColor);
        assertEquals(Color.parseColor("#40ffffff"), testTextView.hintColor);
        assertEquals(3, testTextView.maxLines);
        assertEquals(1.2f, testTextView.lineSpacingMultiplier);
    }
}
