package io.sweers.barber;

import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Map;

import io.sweers.barber.internal.BarberInternal.IBarbershop;

import static io.sweers.barber.internal.BarberInternal.ANDROID_PACKAGE_PREFIX;
import static io.sweers.barber.internal.BarberInternal.JAVA_PACKAGE_PREFIX;
import static io.sweers.barber.internal.BarberInternal.BARBER_CLASS_SUFFIX;

/**
 * Entry point for applications.
 * <p>
 * Use one of the {@link #style(java.lang.Object, android.util.AttributeSet, int[])} variants to
 * style your custom views.
 */
public class Barber {

    private static final IBarbershop<Object> IBARBERSHOP_NO_OP = null;
    private static final String TAG = "Barber";
    private static boolean DEBUG = false;
    private static final Map<Class<?>, IBarbershop<Object>> BARBERSHOPS = new ArrayMap<>();

    /**
     * Style an object with a provided {@link AttributeSet} and attrs
     *
     * @param target Target object to style
     * @param set AttributeSet to retrieve attrs from
     * @param attrs Array of attrs to retrieve
     */
    @UiThread
    public static void style(@NonNull Object target, AttributeSet set, int[] attrs) {
        style(target, set, attrs, 0);
    }

    /**
     * Style an object with a provided {@link AttributeSet}, attrs, and a style attr
     *
     * @param target Target object to style
     * @param set AttributeSet to retrieve attrs from
     * @param attrs Array of attrs to retrieve
     * @param defStyleAttr Style attr to use in styling ("R.attr.myStyle")
     */
    @UiThread
    public static void style(@NonNull Object target, AttributeSet set, int[] attrs, @AttrRes int defStyleAttr) {
        style(target, set, attrs, defStyleAttr, 0);
    }

    /**
     * Style an object with a provided {@link AttributeSet}, attrs, style attr, and style resource
     *
     * @param target Target object to style
     * @param set AttributeSet to retrieve attrs from
     * @param attrs Array of attrs to retrieve
     * @param defStyleAttr Style attr to use in styling ("R.attr.myStyle")
     * @param defStyleRes Style resource to use ("R.style.myStyle")
     */
    @UiThread
    public static void style(@NonNull Object target, AttributeSet set, int[] attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        Class<?> targetClass = target.getClass();
        if (DEBUG) {
            Log.d(TAG, "Looking up barbershop for " + targetClass.getName());
        }
        IBarbershop<Object> barbershop = findBarbershopForClass(targetClass);
        if (barbershop != IBARBERSHOP_NO_OP) {
            barbershop.style(target, set, attrs, defStyleAttr, defStyleRes);
        }
    }

    /**
     * Searches for $$Barbershop class for the given instance, cached for efficiency.
     *
     * @param cls Source class to find a matching $$Barbershop class for
     * @return $$Barbershop class instance
     */
    private static IBarbershop<Object> findBarbershopForClass(Class<?> cls) {
        IBarbershop<Object> barbershop = BARBERSHOPS.get(cls);
        if (barbershop != null) {
            if (DEBUG) {
                Log.d(TAG, "HIT: Cached in barbershop map.");
            }
            return barbershop;
        }
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PACKAGE_PREFIX) || clsName.startsWith(JAVA_PACKAGE_PREFIX)) {
            if (DEBUG) {
                Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            }
            return IBARBERSHOP_NO_OP;
        }
        //noinspection TryWithIdenticalCatches
        try {
            Class<?> barbershopClass = Class.forName(clsName + BARBER_CLASS_SUFFIX);
            //noinspection unchecked
            barbershop = (IBarbershop<Object>) barbershopClass.newInstance();
            if (DEBUG) {
                Log.d(TAG, "HIT: Class loaded barbershop class.");
            }
        } catch (ClassNotFoundException e) {
            if (DEBUG) {
                Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            }
            barbershop = findBarbershopForClass(cls.getSuperclass());
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }
        BARBERSHOPS.put(cls, barbershop);
        return barbershop;
    }
}
