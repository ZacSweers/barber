package io.sweers.barber;

/**
 * These represent the different attribute kinds.
 * <p>
 * {@link #STANDARD} represents the default behavior.
 * (e.g. a float type will result in the generate code using getFloat())
 * <p>
 * The remaining kinds are used to indicate that the given type represents that type of attribute.
 * (e.g. a color kind indicates that even though it's an int, it should be retrieved via getColor())
 */
public enum Kind {

    // The default behavior. Will call get<target type>(...)
    STANDARD,

    // int getColor(...)
    COLOR,

    // float getDimension(...)
    DIMEN,

    // int getDimensionPixelOffset(...)
    DIMEN_PIXEL_OFFSET,

    // int getDimensionPixelSize(...)
    DIMEN_PIXEL_SIZE,

    // float getFraction(...)
    FRACTION,

    // int getInteger(...)
    INTEGER,

    // int getResourceId(...)
    RES_ID,

    // String getNonResourceString(...)
    NON_RES_STRING
}
