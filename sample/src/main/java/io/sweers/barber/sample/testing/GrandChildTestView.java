package io.sweers.barber.sample.testing;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import io.sweers.barber.Barber;
import io.sweers.barber.StyledAttr;
import io.sweers.barber.sample.R;

/**
 * Created by hsweers on 3/6/15.
 */
public class GrandChildTestView extends ChildTestView {

    @StyledAttr(R.styleable.GrandChildTestView_grandChildString)
    public String grandChildString;

    public GrandChildTestView(Context context) {
        super(context);
    }

    public GrandChildTestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrandChildTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.GrandChildTestView, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GrandChildTestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.GrandChildTestView, defStyleAttr, 0);
    }
}
