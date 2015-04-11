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
public class RequiredStyledAttrTestView extends View {

    @Required
    @StyledAttr(R.styleable.RequiredStyledAttrTestView_requiredString)
    public String requiredString;

    public RequiredStyledAttrTestView(Context context) {
        super(context);
    }

    public RequiredStyledAttrTestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RequiredStyledAttrTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.RequiredStyledAttrTestView, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RequiredStyledAttrTestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.RequiredStyledAttrTestView, defStyleAttr, defStyleRes);
    }
}
