[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Barber-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1612)
Barber
======

Barber is your personal custom view stylist.

* Simply annotate your fields using the `@StyledAttr` annotation
* Call the appropriate `Barber.style(this...)` variant
* Let `Barber` take care of all the obtainStyledAttributes() and TypedArray boilerplate for you.
* Profit

This library is heavily influenced by Jake Wharton's [Butterknife](https://github.com/JakeWharton/butterknife) library, and was actually [suggested to me](http://www.reddit.com/r/androiddev/comments/2ue4rm/i_want_to_learn_annotation_processing_but_cant/co7n093?context=3) by the man himself.

Usage
-----
Barber has a single annotation that you use: `@StyledAttr`. This can be used on either fields (if you want to keep the value) or methods (specifically, setters).

```java
public class BarberView extends FrameLayout {

    @StyledAttr(value = R.styleable.BarberView_stripeColor, kind = Kind.COLOR)
    public int stripeColor;

    @StyledAttr(R.styleable.BarberView_stripeCount)
    public int stripeCount;

    @StyledAttr(R.styleable.BarberView_animated)
    public boolean isAnimated;

    public BarberView(Context context) {
        super(context);
    }

    public BarberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarberView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BarberView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Barber.style(this, attrs, R.styleable.BarberView, defStyleAttr, defStyleRes);
    }

    @StyledAttr(R.styleable.BarberView_toggleAnimation)
    public void setToggleAnimationDrawable(Drawable toggleAnimation) {
        // Do something with it
    }
}
```

The Barber class has 3 overloaded `style()` methods, so you can call the appropriate one from whichever constructor you prefer.

By default, Barber will resolve which `TypedArray` method to use based on the type of the target. That is, if you declare it on an `int`, then Barber will generate code that calls `typedArray.getInt(...)`.

```java
@StyledAttr(R.styleable.BarberView_stripeCount)
public int stripeCount;
```

*"But wait, sometimes my int is a color!".*

If you have a special case, such as colors, then you can specify the `kind` member of the annotation with the appropriate `Kind` enum to let Barber know.

 ```java
 @StyledAttr(value = R.styleable.BarberView_stripeColor, kind = Kind.COLOR)
 public int stripeColor;
 ```

The color example above tells Barber it should use `TypedArray`'s `getColor(...)` method. This works for other types as well!

```java
@StyledAttr(value = R.styleable.TestView_testDimension, kind = Kind.DIMEN)
public float testDimension;

@StyledAttr(value = R.styleable.TestView_testDimensionPixelSize, kind = Kind.DIMEN_PIXEL_SIZE)
public int testDimensionPixelSize;
```

And, if you're one of the 10 people that use fraction attributes, you'll be happy to know that those are supported as well.

```java
@StyledAttr(
        value = R.styleable.TestView_testFractionBase,
        kind = Kind.FRACTION,
        base = 2,
        pbase = 2
)
public float testFractionBase;
```

See the [Kind enum](https://github.com/hzsweers/barber/blob/master/api/src/main/java/io/sweers/barber/Kind.java) for a full list of supported types.

Note that these fields or methods cannot be private, and must at least be package accessible. This is because Barber will generate a `**$$Barbershop` class in the same package as the target class.

Required attributes
-------------------
If you want to require an attribute to be specified (beyond just checking if the value is still the default), you can use the `@Required` annotation as well.

```java
@Required
@StyledAttr(R.styleable.RequiredTestView_requiredString)
public String requiredString;
```

Now, if a view is inflated without specifying this attribute, its generated `$$Barbershop` class will throw an IllegalStateException looking like this:

`Missing required attribute 'requiredString' while styling 'io.sweers.barber.sample.testing.RequiredTestView'`

A word about default values
---------------------------
Due to limitations of how annotations work, you cannot specify a default value in the annotation. However, Barber *will not* override any existing values on a field if there is no value at that index. So if you want a default value, initialize the field to it. Unfortunately, annotated setters are out of luck here.

Installation
------------
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    apt 'io.sweers.barber:barber-compiler:1.1.0'
    compile 'io.sweers.barber:barber-api:1.1.0'
}
```

The `api` module comes in the form of an AAR. If you want the `api` sources available in your IDE, consider using [this](https://github.com/xujiaao/AARLinkSources) handy gradle plugin.

Proguard
--------

If you use Proguard, add the following lines to your rules
```
-keep class **$$Barbershop { *; }
-keep class io.sweers.barber.** { *; }

-keepclasseswithmembernames class * {
    @io.sweers.barber.* <fields>;
}

-keepclasseswithmembernames class * {
    @io.sweers.barber.* <methods>;
}
```

License
-------

    Copyright 2015 Henri Z. Sweers

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.