package io.sweers.barber.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import io.sweers.barber.Barber;
import io.sweers.barber.Kind;
import io.sweers.barber.StyledAttr;

/**
 * Sample view showcasing Barber's ease-of-use
 */
public class BarberView extends FrameLayout {

    @StyledAttr(value = R.styleable.BarberView_stripeColor, kind = Kind.COLOR)
    int stripeColor = Color.BLUE;

    @StyledAttr(R.styleable.BarberView_stripeCount)
    int stripeCount = 3;

    @StyledAttr(value = R.styleable.BarberView_poleWidth, kind = Kind.DIMEN_PIXEL_SIZE)
    int poleWidth;

    private final Paint paint = new Paint();

    public BarberView(Context context) {
        super(context);
    }

    public BarberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, R.styleable.BarberView, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarberView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.BarberView, defStyleAttr, defStyleRes);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        int stripeWidth = getHeight() / (2 * stripeCount);
        paint.setStrokeWidth(stripeWidth);

        int poleXStart = (getWidth() / 2) - (poleWidth / 2);

        for (int i = 0; i < (stripeCount * 2) + 4; ++i) {
            paint.setColor(i % 2 == 0 ? stripeColor : Color.WHITE);
            canvas.drawLine(poleXStart, i*stripeWidth, poleXStart + poleWidth, (i*stripeWidth) + stripeWidth, paint);
        }

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20f);
        canvas.drawLine(poleXStart, 0, poleXStart, getHeight(), paint);
        canvas.drawLine(poleXStart + poleWidth, 0, poleXStart + poleWidth, getHeight(), paint);
        paint.setStrokeWidth(80f);
        canvas.drawLine(poleXStart, getHeight(), poleXStart + poleWidth, getHeight(), paint);
        canvas.drawLine(poleXStart, 0, poleXStart + poleWidth, 0, paint);
    }
}
