package io.sweers.barber;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a field with @StyledAttribute to specify which attribute this reflects.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface StyledAttr {
    // A styleable resource id
    int value();

    // Kind of the injection. Use this to specify if the return type is a special case,
    // such as color ints or fraction floats
    Kind kind() default Kind.STANDARD;

    // Fraction base and parent base specifiers
    int base() default 1;
    int pbase() default 1;
}
