Barber
======

A view attribute "injection" library for Android that generates the obtainStyledAttributes() and TypedArray boilerplate code for you.

Barber is your personal custom view stylist.

* Simply annotate your fields using the `@StyledAttr` annotation
* Call the appropriate `Barber.style(this...)` variant
* Let `Barber` take care of all the boilerplate for you to retrieve their values.
* Profit

Usage
-----
Barber has a single annotation that you use, `@StyledAttr`. This can be used on either fields (if you want to keep the value) or methods (specifically, setters).

```java
public class MyCustomView extends FrameLayout {

    @StyledAttr(value = R.styleable.BarberView_stripeColor, kind = Kind.COLOR)
    public int stripeColor;

    @StyledAttr(R.styleable.BarberView_stripeCount)
    public int stripeCount;

    @StyledAttr(R.styleable.BarberView_animated)
    public boolean isAnimated;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

There's a few things to note here:

* By default, Barber will resolve which `TypedArray` method to use based on the type of the target. That is, if you declare it on an `int`, then Barber will generate code that calls `typedArray.getInt(...)`.
* If you have a special case, such as colors, then you can specify the `kind` member of the annotation with the appropriate `Kind` enum to let Barber know. For example, the color example above tells Barber it should use `TypedArray`'s `getColor(...)` method. See the [Kind enum](link) for a full list of supported types.
* Due to limitations of how annotations work, you cannot specify a default value in the annotation. However, Barber *will not* override any existing values on a field if there is no value at that index. So if you want a default value, initialize the field to it. Unfortunately, annotated setters are out of luck here.
* The Barber class has 3 overloaded `style()` methods, so you can call the appropriate one from whichever constructor you prefer.

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
    apt 'io.sweers.barber:compiler:1.0.0'
    compile 'io.sweers.barber:api:1.0.0'
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