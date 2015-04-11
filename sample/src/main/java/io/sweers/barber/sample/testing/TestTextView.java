package io.sweers.barber.sample.testing;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import io.sweers.barber.AndroidAttr;
import io.sweers.barber.AttrSetKind;
import io.sweers.barber.Barber;

/**
 * Test view mainly for testing Android attr values
 */
public class TestTextView extends TextView {

    @AndroidAttr("layout_height")
    public int layoutHeight = 0;

    @AndroidAttr("textAllCaps")
    public boolean textAllCaps = false;

    @AndroidAttr("textStyle")
    public String textStyle = "banana";

    @AndroidAttr(value = "textColor", kind = AttrSetKind.RESOURCE)
    public int textColor = 0;

    @AndroidAttr(value = "textColorHint", kind = AttrSetKind.U_INT)
    public int hintColor = 0;

    @AndroidAttr("lineSpacingMultiplier")
    public float lineSpacingMultiplier = 0;

    public int maxLines = 0;

    public TestTextView(Context context) {
        super(context);
    }

    public TestTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Barber.style(this, attrs, null);
    }

    public TestTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Barber.style(this, attrs, null, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TestTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, null, defStyleAttr, defStyleRes);
    }

    @AndroidAttr("maxLines")
    @Override
    public void setMaxLines(int maxlines) {
        super.setMaxLines(maxlines);
        this.maxLines = maxlines;
    }
}
