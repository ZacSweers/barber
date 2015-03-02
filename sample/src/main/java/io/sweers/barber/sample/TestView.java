package io.sweers.barber.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import io.sweers.barber.Barber;
import io.sweers.barber.Kind;
import io.sweers.barber.StyledAttr;

/**
 * Test view used by BarberTest (see androidTest dir) to verify that the processor's working smoothly
 */
public class TestView extends View {

    @StyledAttr(R.styleable.TestView_testBoolean)
    public boolean testBoolean;

    @StyledAttr(R.styleable.TestView_testInt)
    public int testInt;

    @StyledAttr(value = R.styleable.TestView_testInteger, kind = Kind.INTEGER)
    public int testInteger;

    @StyledAttr(value = R.styleable.TestView_testColor, kind = Kind.COLOR)
    public int testColor;

    @StyledAttr(R.styleable.TestView_testCharSequence)
    public CharSequence testCharSequence;

    @StyledAttr(R.styleable.TestView_testString)
    public String testString;

    @StyledAttr(R.styleable.TestView_testTextArray)
    public CharSequence[] testTextArray;

    @StyledAttr(R.styleable.TestView_testColorStateList)
    public ColorStateList testColorStateList;

    @StyledAttr(R.styleable.TestView_testDrawable)
    public Drawable testDrawable;

    @StyledAttr(
            value = R.styleable.TestView_testFractionBase,
            kind = Kind.FRACTION,
            base = 2,
            pbase = 2
    )
    public float testFractionBase;

    @StyledAttr(
            value = R.styleable.TestView_testFractionPBase,
            kind = Kind.FRACTION,
            base = 2,
            pbase = 2
    )
    public float testFracionPBase;

    @StyledAttr(value = R.styleable.TestView_testDimension, kind = Kind.DIMEN)
    public float testDimension;

    @StyledAttr(value = R.styleable.TestView_testDimensionPixelSize, kind = Kind.DIMEN_PIXEL_SIZE)
    public int testDimensionPixelSize;

    @StyledAttr(value = R.styleable.TestView_testDimensionPixelOffset, kind = Kind.DIMEN_PIXEL_OFFSET)
    public int testDimensionPixelOffset;

    @StyledAttr(value = R.styleable.TestView_testResourceId, kind = Kind.RES_ID)
    public int testResId;

    @StyledAttr(value = R.styleable.TestView_testNonResString1, kind = Kind.NON_RES_STRING)
    public String testNonResString1;

    @StyledAttr(value = R.styleable.TestView_testNonResString2, kind = Kind.NON_RES_STRING)
    public String testNonResString2;

    public float testFloat;


    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.TestView, defStyleAttr, defStyleRes);
        Log.d("BLAH", toString());
    }

    @StyledAttr(R.styleable.TestView_testFloat)
    public void setTestFloat(float test) {
        this.testFloat = test;
    }
}
