package io.sweers.barber;

import android.util.AttributeSet;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entry point for applications.
 * <p>
 * Use one of the {@link #style(java.lang.Object, android.util.AttributeSet, int[])} variants to
 * style your custom views.
 */
public class Barber {

    public static final String SUFFIX = "$$Barbershop";
    public static final String ANDROID_PREFIX = "android.";
    public static final String JAVA_PREFIX = "java.";
    private static final String TAG = "Barber";
    private static final IBarbershop<Object> NO_OP = null;
    private static boolean debug = false;
    private static final Map<Class<?>, IBarbershop<Object>> BARBERSHOPS = new LinkedHashMap<>();

    public static void style(Object target, AttributeSet set, int[] attrs) {
        style(target, set, attrs, 0);
    }

    public static void style(Object target, AttributeSet set, int[] attrs, int defStyleAttr) {
        style(target, set, attrs, defStyleAttr, 0);
    }

    public static void style(Object target, AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        Class<?> targetClass = target.getClass();
        if (debug) {
            Log.d(TAG, "Looking up barbershop for " + targetClass.getName());
        }
        IBarbershop<Object> barbershop = findBarbershopForClass(targetClass);
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
    private static IBarbershop<Object> findBarbershopForClass(Class<?> cls) {
        IBarbershop<Object> barbershop = BARBERSHOPS.get(cls);
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
            barbershop = (IBarbershop<Object>) barbershopClass.newInstance();
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
        String ANDROID_ATTR_NAMESPACE = "http://schemas.android.com/apk/res/android";
        void style(final T target, final AttributeSet set, final int[] attrs, final int defStyleAttr, final int defStyleRes);
    }
}
