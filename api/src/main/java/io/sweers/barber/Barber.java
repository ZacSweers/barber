package io.sweers.barber;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private static final Method NO_OP = null;
    private static boolean debug = false;
    private static final Map<Class<?>, Method> BARBERSHOPS = new LinkedHashMap<>();

    public static void style(View target, AttributeSet set, int[] attrs) {
        style(target, set, attrs, 0);
    }

    public static void style(View target, AttributeSet set, int[] attrs, int defStyleAttr) {
        style(target, set, attrs, defStyleAttr, 0);
    }

    public static void style(View target, AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        Class<?> targetClass = target.getClass();
        try {
            if (debug) {
                Log.d(TAG, "Looking up barbershop for " + targetClass.getName());
            }
            Method style = findStyleMethodForClass(targetClass);
            if (style != NO_OP) {
                style.invoke(null, target, set, attrs, defStyleAttr, defStyleRes);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            Throwable t = e;
            if (t instanceof InvocationTargetException) {
                t = t.getCause();
            }
            throw new RuntimeException("Unable to inject styleable value for " + target, t);
        }
    }

    /**
     * Searches for the style() method given an instance of a generated class. Caches for efficiency.
     *
     * @param cls Instance of a *$$Barbershop instance
     * @return style Method for the instance
     * @throws NoSuchMethodException
     */
    private static Method findStyleMethodForClass(Class<?> cls) throws NoSuchMethodException {
        Method style = BARBERSHOPS.get(cls);
        if (style != null) {
            if (debug) Log.d(TAG, "HIT: Cached in shop map.");
            return style;
        }
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
            if (debug) {
                Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            }
            return NO_OP;
        }
        try {
            Class<?> barbershop = Class.forName(clsName + SUFFIX);
            style = barbershop.getMethod("style", cls, AttributeSet.class, int[].class, int.class, int.class);
            if (debug) {
                Log.d(TAG, "HIT: Class loaded barbershop class.");
            }
        } catch (ClassNotFoundException e) {
            if (debug) {
                Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            }
            style = findStyleMethodForClass(cls.getSuperclass());
        }
        BARBERSHOPS.put(cls, style);
        return style;
    }

}
