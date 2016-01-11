-keep class **$$Barbershop { *; }
-keep class io.sweers.barber.** { *; }
-keepclasseswithmembers class * {
    @io.sweers.barber.* <fields>;
}
-keepclasseswithmembers class * {
    @io.sweers.barber.* <methods>;
}
-dontwarn io.sweers.barber.**
