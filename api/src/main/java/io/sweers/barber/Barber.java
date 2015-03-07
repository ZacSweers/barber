package io.sweers.barber;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entry point for applications.
 * <p>
 * Use one of the {@link #style(android.view.View, android.util.AttributeSet, int[])} variants to
 * style your custom views.
 */
public class Barber {

    public static final String SUFFIX = "$$Barbershop";
    public static final String ANDROID_PREFIX = "android.";
    public static final String JAVA_PREFIX = "java.";
    private static final String TAG = "Barber";
    private static final IBarbershop<View> NO_OP = null;
    private static boolean debug = false;
    private static final Map<Class<?>, IBarbershop<View>> BARBERSHOPS = new LinkedHashMap<>();

    public static void style(View target, AttributeSet set, int[] attrs) {
        style(target, set, attrs, 0);
    }

    public static void style(View target, AttributeSet set, int[] attrs, int defStyleAttr) {
        style(target, set, attrs, defStyleAttr, 0);
    }

    public static void style(View target, AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        Class<?> targetClass = target.getClass();
        if (debug) {
            Log.d(TAG, "Looking up barbershop for " + targetClass.getName());
        }
        IBarbershop<View> barbershop = findBarbershopForClass(targetClass);
        if (barbershop != NO_OP) {
            barbershop.style(target, set, attrs, defStyleAttr, defStyleRes);
        }
    }

    /**
     * Searches for $$Barbershop class for the given instance, cached for efficiency.
     *
     * @param cls Source class to find a matching $$Barbershop class for
     * @return $$Barbershop class instance
     */
    private static IBarbershop<View> findBarbershopForClass(Class<?> cls) {
        IBarbershop<View> barbershop = BARBERSHOPS.get(cls);
        if (barbershop != null) {
            if (debug) Log.d(TAG, "HIT: Cached in barbershop map.");
            return barbershop;
        }
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
            if (debug) {
                Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            }
            return NO_OP;
        }
        //noinspection TryWithIdenticalCatches
        try {
            Class<?> barbershopClass = Class.forName(clsName + SUFFIX);
            //noinspection unchecked
            barbershop = (IBarbershop<View>) barbershopClass.newInstance();
            if (debug) {
                Log.d(TAG, "HIT: Class loaded barbershop class.");
            }
        } catch (ClassNotFoundException e) {
            if (debug) {
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

    /** DO NOT USE. Exposed for generated classes' use. */
    public interface IBarbershop<T> {
        public void style(final T target, final AttributeSet set, final int[] attrs, final int defStyleAttr, final int defStyleRes);
    }
}
