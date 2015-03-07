package io.sweers.barber.sample.testing;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import io.sweers.barber.Barber;
import io.sweers.barber.Required;
import io.sweers.barber.StyledAttr;
import io.sweers.barber.sample.R;

/**
 * Created by hsweers on 3/6/15.
 */
public class RequiredTestView extends View {

    @Required
    @StyledAttr(R.styleable.RequiredTestView_requiredString)
    public String requiredString;

    public RequiredTestView(Context context) {
        super(context);
    }

    public RequiredTestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RequiredTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.RequiredTestView, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RequiredTestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.RequiredTestView, defStyleAttr, defStyleRes);
    }
}
