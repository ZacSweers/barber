package io.sweers.barber.internal;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Barber internals shared by both the compiler and api modules.
 */
public final class BarberInternal {
    public static final String BARBER_CLASS_SUFFIX = "$$Barbershop";
    public static final String ANDROID_PACKAGE_PREFIX = "android.";
    public static final String JAVA_PACKAGE_PREFIX = "java.";
    public static final TypedValue REUSABLE_TYPEVALUE = new TypedValue();

    @SuppressWarnings("unused")
    public static float resolveFloatResource(Resources res, int resId) {
        res.getValue(resId, REUSABLE_TYPEVALUE, true);
        return REUSABLE_TYPEVALUE.getFloat();
    }

    /** DO NOT USE. Exposed for generated classes' use. */
    public interface IBarbershop<T> {
        @SuppressWarnings("unused")
        String ANDROID_ATTR_NAMESPACE = "http://schemas.android.com/apk/res/android";
        void style(final T target, final AttributeSet set, final int[] attrs, final int defStyleAttr, final int defStyleRes);
    }


}
