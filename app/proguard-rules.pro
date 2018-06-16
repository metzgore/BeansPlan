# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#######################################################################
# General settings
#######################################################################
-dontobfuscate
-printconfiguration config.txt

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

#######################################################################
# My stuff
#######################################################################
-keep class de.metzgore.beansplan.data.DailySchedule { *; }
-keep class de.metzgore.beansplan.data.WeeklySchedule { *; }
-keep class de.metzgore.beansplan.data.Show { *; }

#######################################################################
# Android
#######################################################################
-keep class kotlin.Metadata { *; }
-dontnote kotlin.internal.PlatformImplementationsKt
-dontnote kotlin.reflect.jvm.internal.**
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

#######################################################################
# Kotlin
#######################################################################
-keep class kotlin.Metadata { *; }
-dontnote kotlin.internal.PlatformImplementationsKt
-dontnote kotlin.reflect.jvm.internal.**

#######################################################################
# Retrofit
#######################################################################
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#######################################################################
# OkHttp
#######################################################################
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontnote okhttp3.internal.platform.*
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

#######################################################################
# Gson
#######################################################################
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-dontnote sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#######################################################################
# Android-Iconics
#######################################################################
-keep class com.mikepenz.iconics.core.R
-keep class **.R$* {
    <fields>;
}
-keep class com.mikepenz.iconics.typeface.ITypeface