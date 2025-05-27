# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep all public classes and methods in the library
-keep public class com.alex.klinemarker.** { *; }

# Keep MPAndroidChart classes that are used by the library
-keep class com.github.mikephil.charting.** { *; } 