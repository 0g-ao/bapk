# GeckoView ProGuard Rules
-keep class org.mozilla.geckoview.** { *; }
-keep class org.mozilla.gecko.SysInfo { *; }
-keep class org.mozilla.gecko.mozglue.JNIObject { *; }
-dontwarn org.mozilla.geckoview.**
-dontwarn mozilla.components.**
-dontwarn java.beans.**

# Standard Android rules are covered by proguard-android-optimize.txt
