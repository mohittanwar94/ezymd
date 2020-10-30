# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Essential\Latest-Setup-Android\android-sdks\android-sdks/tools/proguard/proguard-android.txt
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

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment
-keep public class * extends android.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService
-keep @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class *
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
 native <methods>;
}

-keep public class * extends android.view.View {
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
 public void set*(...);
}

-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class **.R$* {
 public static <fields>;
}

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontwarn android.support.**

-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**


#mpChart progaurd
-keep class com.github.mikephil.charting.** { *; }
-dontwarn io.realm.**

#google progaurd
-keep class com.google.**
-dontwarn com.google.**


#duplicate class
-dontnote org.apache.**
-dontwarn org.apache.**
-dontnote android.net.http.SslError
-dontnote android.net.http.SslCertificate
-dontnote android.net.http.HttpResponseCache
-dontnote android.net.http.SslCertificate$DName


# flurry analytics
#-keep class com.flurry.** { *; }
#-dontwarn com.flurry.**

#google drive

-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}


-dontwarn com.google.api.client.extensions.android.**
-dontwarn com.google.api.client.googleapis.extensions.android.**
-dontwarn com.google.android.gms.**
-dontnote java.nio.file.Files, java.nio.file.Path
-dontnote **.ILicensingService
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.**
-dontwarn com.google.api.client.json.**

-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn com.squareup.okhttp.**
-dontwarn com.google.appengine.**
-dontwarn javax.servlet.**

-useuniqueclassmembernames


-keepnames class com.amazonaws.**
-keepnames class com.amazon.*
-keep class com.amazonaws.services.**.*Handler
-keep class com.company.app.OAuth2SaslClientFactory
-keep class org.apache.commons.logging.**               { *; }
-keep class com.amazonaws.services.sqs.QueueUrlHandler  { *; }
-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
-keep class com.amazonaws.javax.xml.stream.**           { *; }
-keep class com.amazonaws.services.**.model.*Exception* { *; }
-keep class org.codehaus.**                             { *; }
-keepattributes Signature,*Annotation*

-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.annotation.compiler.GlideAnnotationProcessor
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
-dontwarn uk.co.senab.photoview.**
-keep class uk.co.senab.photoview.** { *;}
#Retrofit, OkHttp, Gson
-keepattributes Signature
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# OkHttp3
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Rxjava-promises

-keep class com.darylteo.rx.** { *; }

-dontwarn com.darylteo.rx.**

# RxJava 0.21

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keep class com.ezymd.restaurantapp.** { *; }

## Retrolambda specific rules ##

-dontwarn java.lang.invoke.*

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!method/inlining/*
-useuniqueclassmembernames
-dontwarn us.zoom.videomeetings.BuildConfig
-dontwarn us.zoom.androidlib.BuildConfig
-dontwarn android.hardware.**
-dontwarn android.media.**
-dontwarn android.widget.**
-keep class com.box.** {
	*;
}

-keep class org.json.** {
    *;
}

-keepnames class com.fasterxml.jackson.** {
	*;
}
-dontwarn com.fasterxml.jackson.databind.**
-keep class com.google.android.gms.** {*;}
-dontwarn com.google.api.client.**
-keep class com.google.api.client.** {
	*;
}
-dontwarn com.microsoft.live.**
-keep class com.microsoft.live.** {
	*;
}
-keep class com.google.api.services.** {
	*;
}

-keep class com.google.protobuf.** {
    *;
}



-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# The Maps API uses serialization.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#RazorPay
-keepattributes JavascriptInterface


-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}

#connectycube
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-dontwarn org.jivesoftware.smackx.**
-dontwarn org.apache.http.annotation.**
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}


-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!method/inlining/*
-useuniqueclassmembernames
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-dontwarn us.zoom.thirdparty.**
-dontwarn com.google.firebase.**
-dontwarn com.nineoldandroids.**
-dontwarn us.google.protobuf.**
-dontwarn us.zoom.videomeetings.BuildConfig
-dontwarn us.zoom.androidlib.BuildConfig
-dontwarn com.bumptech.glide.**
-dontwarn com.google.gson.**
-dontwarn com.zipow.videobox.ptapp.PTAppProtos.**
-dontwarn com.zipow.videobox.ptapp.PTAppProtos**
-dontwarn org.greenrobot.eventbus.**
-dontwarn com.zipow.videobox.ptapp.MeetingInfoProto**
-dontwarn com.zipow.videobox.ZMFirebaseMessagingService
-dontwarn com.zipow.videobox.util.ConfLocalHelper**
-dontwarn com.zipow.videobox.util.RoundedCornersTransformation**
-dontwarn com.davemorrissey.labs.subscaleview.**
-dontwarn com.zipow.videobox.view.mm.UnshareAlertDialogFragment**
-dontwarn com.zipow.videobox.util.zmurl.avatar.ZMAvatarUrl**
-dontwarn com.android.internal.R$styleable**
-dontnote us.zoom.androidlib.app.ZMActivity
-dontnote us.zoom.androidlib.util.UIUtil
-dontnote us.zoom.androidlib.util.DnsServersDetector
-dontnote org.apache.**
-dontnote org.json.**
-dontnote com.zipow.videobox.view.mm.**
-dontnote com.zipow.videobox.view.sip.**
-dontnote com.davemorrissey.labs.**
-keep class com.zipow.videobox.view.video.ZPGLTextureView**{
    *;
}
-keep class ZMActivity.** { *;}
-keep class us.zoom.androidlib.util.UIUtil {  *;}
-dontwarn com.facebook.android.**
-dontwarn android.hardware.**
-dontwarn android.media.**
-dontwarn android.widget.**
-dontwarn com.zipow.videobox.**
-dontwarn us.zoom.androidlib.**
-dontwarn us.zoom.thirdparty.**
-dontwarn com.google.firebase.**
-dontwarn com.box.**
-keep class com.box.** {
	*;
}
-dontwarn com.dropbox.**
-keep class com.dropbox.** {
	*;
}
-dontwarn org.apache.**
-keep class org.apache.** {
    *;
}
-keepattributes *Annotation*
-dontwarn com.baidu.**
-keep class com.baidu.** {
	*;
}
-keep class com.google.android.** {
	*;
}
-keep class com.google.gson.** {
    *;
}
-keep class us.google.protobuf.** {
    *;
}
-keep class com.bumptech.glide.** {
    *;
}
-keep class us.zoom.androidlib.app.ZMFileListEntry{*;}
-keep class android.support.** {
    *;
}
#Zoom End

#Jitsi
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

-dontwarn com.facebook.react.**
-keep,includedescriptorclasses class com.facebook.react.bridge.** { *; }

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class okio.** { *; }
-dontwarn org.chromium.build.BuildHooksAndroid
-keep class com.facebook.react.bridge.CatalystInstanceImpl { *; }
-keep class com.facebook.react.bridge.ExecutorToken { *; }
-keep class com.facebook.react.bridge.JavaScriptExecutor { *; }
-keep class com.facebook.react.bridge.ModuleRegistryHolder { *; }
-keep class com.facebook.react.bridge.ReadableType { *; }
-keep class com.facebook.react.bridge.queue.NativeRunnable { *; }
-keep class com.facebook.react.devsupport.** { *; }

-dontwarn com.facebook.react.devsupport.**
-dontwarn com.google.appengine.**
-dontwarn com.squareup.okhttp.**
-dontwarn javax.servlet.**
-keep public class com.horcrux.svg.** {*;}
-keep class com.facebook.hermes.unicode.** { *; }
#End Jitsi

#aws
 # Class names are needed in reflection
 -keepnames class com.amazonaws.**
 # Request handlers defined in request.handlers
 -keep class com.amazonaws.services.**.*Handler
 # The following are referenced but aren't required to run
 -dontwarn org.apache.commons.logging.**
 # Android 6.0 release removes support for the Apache HTTP client
 -dontwarn org.apache.http.**
 # The SDK has several references of Apache HTTP client
 -dontwarn com.amazonaws.http.**
 -dontwarn com.amazonaws.metrics.**
 -keep class org.apache.commons.logging.**               { *; }
 -keepattributes Signature,*Annotation*

 -dontwarn javax.xml.stream.events.**
 -dontwarn org.codehaus.jackson.**
 -dontwarn org.apache.commons.logging.impl.**
 -dontwarn org.apache.http.conn.scheme.**
 -dontwarn com.fasterxml.jackson.**
#end aws



# Gson
-keep class sun.misc.Unsafe { *; }

# Okio
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.Platform**

# Dagger
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue

# OkHttp3: https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro
## JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

## A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

## Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

## OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
