Change Log
==========

Version 1.4.0 *In progress*
----------------------------

* New project structure. 
  * Doesn't require any changes existing gradle configurations.
  * API is now a normal AAR, which means support annotations and consumer proguard rules!
  * API and compiler now have no direct interdependence, just a shared internal "barber-annotations" module.
* More memory-efficient WeakArraySet implementation to handle view inheritance.
* Switched to full mavencentral releasing structure. This means artifacts will only be pushed to mavencentral.
  * It's just easier for me this way, the bintray-release plugin was causing trouble all the time
  * This also means snapshots!

Version 1.3.1 *2015-7-3*
----------------------------

* Hotfix for the barber-compiler pom not specifying the correct artifactId. Should be able to 
properly resolve the dependency now

Version 1.3.0 *2015-7-3*
----------------------------

This release adds support for specifying default values in `@StyledAttr` annotations. 
They must point to a resource ID, and abide by the same conventions for type resolution as 
before regarding `Kind`.

```java
@StyledAttr(value = R.styleable.BarberView_animated, defaultValue = R.bool.animated_default)
public boolean isAnimated;
```

Version 1.2.1 *2015-5-2*
----------------------------

* Add mavencentral support and update some dependencies

Version 1.2.0 *2015-4-11*
----------------------------

This adds support for retrieving Android attribute values.
  
```java
@AndroidAttr("textAllCaps")
public boolean textAllCaps;
```

Like `StyledAttr`, the default behavior is to return the type of the field/param. These are also subject to the same approach as `@StyledAttr` regarding special return types. See the [AttrSetKind enum](https://github.com/hzsweers/barber/blob/master/api/src/main/java/io/sweers/barber/AttrSetKind.java) for a full list of supported types.

```java
@AndroidAttr(value = "textColor", kind = AttrSetKind.RESOURCE)
public int textColor;
```

Version 1.1.1 *2015-3-8*
----------------------------

* Adds support for Preferences styling by removing the hard View requirement in Barber.style(…) 
methods. Now it simply takes in an Object, and will now work for preferences with custom attributes.

Version 1.1.0 *2015-3-6*
----------------------------

* Add support for @Required annotation
* Add support for inheritance (#5)

Version 1.0.1 *2015-3-3*
----------------------------

* Lower minSdkVersion to 14

Version 1.0.0 *2015-3-3*
----------------------------

Initial release
