package io.sweers.barber.sample.testing;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import io.sweers.barber.Barber;
import io.sweers.barber.StyledAttr;
import io.sweers.barber.sample.R;

/**
 * Testing inheritance
 */
public class ChildTestView extends TestView {

    @StyledAttr(R.styleable.ChildTestView_childInt)
    public int childInt = 0;

    @StyledAttr(R.styleable.ChildTestView_childBoolean)
    public boolean childBoolean = false;

    @StyledAttr(R.styleable.ChildTestView_childString)
    public String childString;

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
