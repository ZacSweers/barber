package io.sweers.barber.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import io.sweers.barber.Barber;
import io.sweers.barber.StyledAttr;

/**
 * Testing inheritance
 */
public class ChildTestView extends TestView {

    @StyledAttr(R.styleable.ChildTestView_coolNumber)
    protected int coolNumber = 0;

    public ChildTestView(Context context) {
        super(context);
    }

    public ChildTestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChildTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.ChildTestView, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChildTestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.ChildTestView, defStyleAttr, defStyleRes);
    }
}
