package io.sweers.barber;

/**
 * These represent the different attribute kinds.
 * <p>
 * {@link #STANDARD} represents the default behavior.
 * (e.g. a float type will result in the generate code using getAttributeFloatValue())
 * <p>
 * The remaining kinds are used to indicate that the given type represents that type of attribute.
 * (e.g. a color kind indicates that even though it's an int, it should be retrieved via getColor())
 */
public enum AttrSetKind {

    // The default behavior. Will call get<target type>(...)
    STANDARD,

    // int getAttributeUnsignedIntValue(...)
    U_INT,

    // int getAttributeValue(...)
    RESOURCE
}
