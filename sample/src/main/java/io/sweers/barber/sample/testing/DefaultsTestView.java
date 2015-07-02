package io.sweers.barber.sample.testing;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import io.sweers.barber.Barber;
import io.sweers.barber.Kind;
import io.sweers.barber.StyledAttr;
import io.sweers.barber.sample.R;

/**
 * View for testing defaults
 */
public class DefaultsTestView extends View {

    @StyledAttr(value = R.styleable.TestView_testBoolean, defaultValue = R.bool.test_default_bool)
    public boolean testBoolean = false;

    @StyledAttr(value = R.styleable.TestView_testInt, defaultValue = R.integer.test_default_int)
    public int testInt = -1;

    @StyledAttr(value = R.styleable.TestView_testInteger, kind = Kind.INTEGER, defaultValue = R.integer.test_default_int)
    public int testInteger = -1;

    @StyledAttr(value = R.styleable.TestView_testColor, kind = Kind.COLOR, defaultValue = android.R.color.holo_red_dark)
    public int testColor;

    @StyledAttr(value = R.styleable.TestView_testCharSequence, defaultValue = R.string.test_default_charseq)
    public CharSequence testCharSequence;

    @StyledAttr(value = R.styleable.TestView_testString, defaultValue = R.string.test_default_string)
    public String testString;

    @StyledAttr(value = R.styleable.TestView_testTextArray, defaultValue = R.array.buzzwords)
    public CharSequence[] testTextArray;

    @StyledAttr(value = R.styleable.TestView_testColorStateList, defaultValue = R.color.button_selector)
    public ColorStateList testColorStateList;

    @StyledAttr(value = R.styleable.TestView_testDrawable, defaultValue = R.drawable.ic_action_refresh)
    public Drawable testDrawable;

    @StyledAttr(
            value = R.styleable.TestView_testFractionBase,
            kind = Kind.FRACTION,
            base = 2,
            pbase = 2,
            defaultValue = R.fraction.fraction
    )
    public float testFractionBase;

    @StyledAttr(
            value = R.styleable.TestView_testFractionPBase,
            kind = Kind.FRACTION,
            base = 2,
            pbase = 2,
            defaultValue = R.fraction.parent_fraction
    )
    public float testFracionPBase;

    @StyledAttr(
            value = R.styleable.TestView_testDimension,
            kind = Kind.DIMEN,
            defaultValue = R.dimen.test_dimen
    )
    public float testDimension;

    @StyledAttr(
            value = R.styleable.TestView_testDimensionPixelSize,
            kind = Kind.DIMEN_PIXEL_SIZE,
            defaultValue = R.dimen.test_dimen
    )
    public int testDimensionPixelSize;

    @StyledAttr(
            value = R.styleable.TestView_testDimensionPixelOffset,
            kind = Kind.DIMEN_PIXEL_OFFSET,
            defaultValue = R.dimen.test_dimen
    )
    public int testDimensionPixelOffset;

    @StyledAttr(
            value = R.styleable.TestView_testResourceId,
            kind = Kind.RES_ID,
            defaultValue = R.array.buzzwords
    )
    public int testResId;

    @StyledAttr(
            value = R.styleable.TestView_testNonResString1,
            kind = Kind.NON_RES_STRING,
            defaultValue = R.string.testStringRes
    )
    public String testNonResString1;

    @StyledAttr(
            value = R.styleable.TestView_testNonResString2,
            kind = Kind.NON_RES_STRING,
            defaultValue = R.string.testStringRes
    )
    public String testNonResString2;

    public float testFloat;

    public DefaultsTestView(Context context) {
        super(context);
    }

    public DefaultsTestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultsTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.TestView, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DefaultsTestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.TestView, defStyleAttr, defStyleRes);
    }

    @StyledAttr(value = R.styleable.TestView_testFloat, defaultValue = R.dimen.test_default_float)
    public void setTestFloat(float test) {
        this.testFloat = test;
    }
}
