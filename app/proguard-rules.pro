# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android SDK/tools/proguard/proguard-android.txt
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

#-keep includedescriptorclasses

-keepattributes Signature

-keep class uk.co.senab.photoview.**

-keep class net.hockeyapp.**

-keep class android.support.**

-keep class bolts.*

-keep class com.ooredoo.bizstore.camera.**
-keep class com.ooredoo.bizstore.views.RangeSeekBar$OnRangeSeekBarChangeListener
-keep class com.ooredoo.bizstore.utils.*
-keep class com.ooredoo.bizstore.adapters.*
-keep class com.ooredoo.bizstore.model.* {*;}
-keep class com.ooredoo.bizstore.asynctasks.*
-keep class com.ooredoo.bizstore.ui.**
-keep class com.ooredoo.bizstore.listeners.*
-keep class com.ooredoo.bizstore.views.*

-keep class com.facebook.**

-keep class com.google.android.**

-keep class com.google.zxing.**

-keep class com.activeandroid.** {*;}

-keep class android.support.design.widget.AppBarLayout$ScrollingViewBehavior {*;}

-keepclassmembers class android.support.*{
    int icon;
    int title;
    int mId;
    android.app.PendingIntent actionIntent;
}

-dontnote android.support.v4.**
-dontnote android.support.v7.**
-dontnote com.google.android.gms.maps.**
#-dontnote com.google.android.**
#-dontnote com.google.gson.**
-dontnote com.google.gson.internal.$Gson$Types$ParameterizedTypeImpl
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.zxing.client.android.book.SearchBookContentsListItem

-dontwarn net.hockeyapp.android.*



##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature


# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

-keepattributes *Annotation*

-keep class com.google.gson.annotations.SerializedName

##---------------End: proguard configuration for Gson  ----------