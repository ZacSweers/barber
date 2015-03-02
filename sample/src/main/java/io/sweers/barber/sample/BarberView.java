package io.sweers.barber.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import io.sweers.barber.Barber;
import io.sweers.barber.Kind;
import io.sweers.barber.StyledAttr;

/**
 * Created by hsweers on 2/24/15.
 */
public class BarberView extends FrameLayout {

    @StyledAttr(value = R.styleable.BarberView_stripeColor, kind = Kind.COLOR)
    public int stripeColor;

    @StyledAttr(R.styleable.BarberView_stripeCount)
    public int stripeCount;

    @StyledAttr(R.styleable.BarberView_animated)
    public boolean isAnimated;

    public BarberView(Context context) {
        super(context);
    }

    public BarberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarberView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarberView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.BarberView, defStyleAttr, defStyleRes);
    }

    @StyledAttr(R.styleable.BarberView_toggleAnimation)
    public void setToggleAnimationDrawable(Drawable toggleAnimation) {
        // Do something with it
    }
}
