/**
 * View attribute injection library for Android which generates the obtainStyledAttributes() and
 * TypedArray boilerplate code for you at compile time.
 * <p>
 * No more handing to deal with context.obtainStyledAttributes(...) or manually retrieving values
 * from the resulting {@link android.content.res.TypedArray TypedArray} instance. Just annotate your
 * field or method with {@link io.sweers.barber.StyledAttr @StyledAttr}.
 */
package io.sweers.barber;