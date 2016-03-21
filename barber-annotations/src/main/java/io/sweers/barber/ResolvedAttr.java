package io.sweers.barber;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotate a field with @ResolvedAttr to specify an theme attribute to retrieve
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ResolvedAttr {
    // An attribute name (e.g. "android.R.attr.textColorPrimary")
    int value();
}
