package io.sweers.barber.sample;

import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;

import java.util.Arrays;

import io.sweers.barber.sample.testing.ChildTestView;
import io.sweers.barber.sample.testing.GrandChildTestView;
import io.sweers.barber.sample.testing.TestView;

public class BarberTest extends AndroidTestCase {

    private TestView testView;
    private ChildTestView childTestView;
    private GrandChildTestView grandChildTestView;
    private Resources res;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testView = (TestView) View.inflate(getContext(), R.layout.test_view, null);
        childTestView = (ChildTestView) View.inflate(getContext(), R.layout.child_test_view, null);
        grandChildTestView = (GrandChildTestView) View.inflate(getContext(), R.layout.grand_child_test_view, null);
        res = getContext().getResources();
    }

    @SmallTest
    public void testBoolean() {
        assertTrue(testView.testBoolean);
    }

    @SmallTest
    public void testInt() {
        assertEquals(3, testView.testInt);
    }

    @SmallTest
    public void testInteger() {
        assertEquals(12, testView.testInteger);
    }

    @SmallTest
    public void testColor() {
        assertEquals(res.getColor(android.R.color.holo_red_dark), testView.testColor);
    }

    @SmallTest
    public void testFloat() {
        assertEquals(0.75f, testView.testFloat);
    }

    @SmallTest
    public void testCharSequence() {
        assertNotNull(testView.testCharSequence);
        assertEquals("charsequence", testView.testCharSequence);
    }

    @SmallTest
    public void testString() {
        assertNotNull(testView.testString);
        assertEquals("this is a string", testView.testString);
    }

    @SmallTest
    public void testTextArray() {
        assertNotNull(testView.testTextArray);
        assertTrue(Arrays.deepEquals(testView.testTextArray, res.getTextArray(R.array.buzzwords)));
    }

    @SmallTest
    public void testColorStateList() {
        assertNotNull(testView.testColorStateList);
        assertTrue(testView.testColorStateList.isStateful());

        int defaultColor = res.getColor(android.R.color.holo_blue_bright);
        int pressedColor = res.getColor(android.R.color.holo_blue_dark);
        int[] pressedState = {android.R.attr.state_pressed};
        int[] defaultState = {};

        assertEquals(pressedColor, testView.testColorStateList.getColorForState(pressedState, defaultColor));
        assertEquals(defaultColor, testView.testColorStateList.getColorForState(defaultState, defaultColor));
    }

    @SmallTest
    public void testDrawable() {
        assertNotNull(testView.testDrawable);
    }

    @SmallTest
    public void testFraction() {
        assertEquals(res.getFraction(R.fraction.fraction, 2, 2), testView.testFractionBase);
        assertEquals(res.getFraction(R.fraction.parent_fraction, 2, 2), testView.testFracionPBase);
    }

    @SmallTest
    public void testDimension() {
        assertEquals(res.getDimension(R.dimen.test_dimen), testView.testDimension);
    }

    @SmallTest
    public void testDimensionPixelSize() {
        assertEquals(res.getDimensionPixelSize(R.dimen.test_dimen), testView.testDimensionPixelSize);
    }

    @SmallTest
    public void testDimensionPixelOffset() {
        assertEquals(res.getDimensionPixelOffset(R.dimen.test_dimen), testView.testDimensionPixelOffset);
    }

    @SmallTest
    public void testResId() {
        assertEquals(R.array.buzzwords, testView.testResId);
    }

    @SmallTest
    public void testNonResString() {
        assertEquals("Hello", testView.testNonResString1);
        assertNull(testView.testNonResString2);
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
            View.inflate(getContext(), R.layout.test_view, null);
        } catch (IllegalStateException e) {
            assertEquals("\"Missing required attribute \'requiredString\' while styling \'io.sweers.barber.sample.testing.RequiredTestView\'\"", e.getMessage());
        }
    }
}
