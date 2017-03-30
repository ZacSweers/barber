package io.sweers.barber;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotate a field with @AndroidAttr to specify an android namespace attribute to retrieve
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface AndroidAttr {
    // An android attribute name (e.g. "textAllCaps")
    String value();

    // Kind of the injection. Use this to specify if the return type is a special case,
    // such as unsigned ints or resource ints
    AttrSetKind kind() default AttrSetKind.STANDARD;
}
