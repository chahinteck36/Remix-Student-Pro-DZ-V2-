# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep data models & Room entities
-keep class com.example.data.local.** { *; }
-keep class com.example.data.content.** { *; }
-keep class com.example.data.ai.** { *; }

# Moshi rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
    @com.squareup.moshi.* <methods>;
}
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.JsonQualifier <fields>;
}

# Retrofit rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

