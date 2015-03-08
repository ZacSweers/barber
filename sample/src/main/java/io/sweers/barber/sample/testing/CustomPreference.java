package io.sweers.barber.sample.testing;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import io.sweers.barber.Barber;
import io.sweers.barber.Kind;
import io.sweers.barber.StyledAttr;
import io.sweers.barber.sample.R;

/**
* Styled preference
*/
public class CustomPreference extends Preference {

    @StyledAttr(value = R.styleable.TestPreference_prefTitleColor, kind = Kind.COLOR)
    public int titleColor = -1;

    @StyledAttr(value = R.styleable.TestPreference_prefSummaryColor, kind = Kind.COLOR)
    public int summaryColor = -1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.TestPreference, defStyleAttr, defStyleRes);
    }

    public CustomPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.TestPreference, defStyleAttr);
    }

    public CustomPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        if (titleColor != -1) {
            titleView.setTextColor(titleColor);
        }

        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        if (summaryColor != -1) {
            summaryView.setTextColor(summaryColor);
        }
    }
}
