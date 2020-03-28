# DEPRECATED

This project is no longer maintained. Consider using https://github.com/airbnb/paris

[![Build Status](https://travis-ci.org/hzsweers/barber.svg?branch=master)](https://travis-ci.org/hzsweers/barber) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Barber-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1612) [![Maven Central](https://img.shields.io/maven-central/v/io.sweers.barber/barber-api.svg)](http://search.maven.org/#browse%7C-504042670)
Barber
======

Barber is your personal custom view stylist.

* Simply annotate your fields using the `@StyledAttr` or `@AndroidAttr` annotations
* Call the appropriate `Barber.style(this...)` variant
* Let `Barber` take care of all the boilerplate for you.
* Profit

This library is heavily influenced by Jake Wharton's [Butter Knife](https://github.com/JakeWharton/butterknife) library, and was actually [suggested to me](http://www.reddit.com/r/androiddev/comments/2ue4rm/i_want_to_learn_annotation_processing_but_cant/co7n093?context=3) by the man himself.

Usage
-----

Barber has two main annotations that you use: `@StyledAttr` and `@AndroidAttr`. These can be used on fields or methods (e.g. setters). `StyledAttr` is used for retrieving custom attrs for custom views. `@AndroidAttr` is used for retrieving values for attributes in the android namespace.

The Barber class has 3 overloaded `style()` methods, so you can call the appropriate one from whichever constructor you prefer.

Annotated fields or methods *cannot* be private, and must at least be package accessible. This is because Barber will generate a `**$$Barbershop` class in the same package as the target class.

#### StyledAttr

Declare your styled attributes in your `attrs.xml`, like you normally would. For example:

```xml
<declare-styleable name="BarberView">
    <attr name="stripeColor" format="color" />
    <attr name="stripeCount" format="integer" />
    <attr name="animated" format="boolean" />
    <attr name="toggleAnimation" format="reference" />
</declare-styleable>
```

```java
public class BarberView extends FrameLayout {

    @StyledAttr(value = R.styleable.BarberView_stripeColor, kind = Kind.COLOR)
    public int stripeColor;

    @StyledAttr(R.styleable.BarberView_stripeCount)
    public int stripeCount;

    @StyledAttr(value = R.styleable.BarberView_animated, defaultValue = R.bool.animated_default)
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

By default, Barber will resolve which `TypedArray` method to use based on the type of the target. That is, if you declare it on an `int`, then Barber will generate code that calls `typedArray.getInt(...)`.

```java
@StyledAttr(R.styleable.BarberView_stripeCount)
public int stripeCount;
```

**"But wait, sometimes my int is a color!".**

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

**Default values**

You can specify resource IDs for default values.

```java
@StyledAttr(value = R.styleable.BarberView_animated, defaultValue = R.bool.animated_default)
public boolean isAnimated;
```

#### AndroidAttr

If you want to retrieve the value of an Android attribute, you can use `@AndroidAttr` to retrieve its value

```java
@AndroidAttr("textAllCaps")
public boolean textAllCaps;
```

Like `StyledAttr`, the normal behavior is to return the type of the field/param. These are also subject to the same approach as `@StyledAttr` regarding special return types. See the [AttrSetKind enum](https://github.com/hzsweers/barber/blob/master/api/src/main/java/io/sweers/barber/AttrSetKind.java) for a full list of supported types.

```java
@AndroidAttr(value = "textColor", kind = AttrSetKind.RESOURCE)
public int textColor;
```

Right now it's just limited to the API of `AttributeSet`, but I may look into adding a more flexible API layer on top of this for coercing the returned data if people express an interest.

Required attributes
-------------------
If you want to require an attribute to be specified, you can use the `@Required` annotation as well.

```java
@Required
@StyledAttr(R.styleable.RequiredTestView_requiredString)
public String requiredString;
```

Now, if a view is inflated without specifying this attribute, its generated `$$Barbershop` class will throw an IllegalStateException looking like this:

`Missing required attribute 'requiredString' while styling 'io.sweers.barber.sample.testing.RequiredTestView'`

**NOTE:** Due to how `AttributeSet`'s interface works, `@Required` is not compatible with `@AndroidAttr` annotations.

Installation
------------
```groovy
buildscript {
    repositories {
        jcenter() // Also available in maven central
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    apt 'io.sweers.barber:barber-compiler:1.3.1'
    compile 'io.sweers.barber:barber-api:1.3.1'
}
```

Proguard
--------

If you use Proguard, [consumer proguard rules](https://github.com/hzsweers/barber/blob/master/api/consumer-proguard-rules.pro) are packaged in the `api` module AAR.

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
