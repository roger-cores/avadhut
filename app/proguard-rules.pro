# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/roger/Apps/android-sdk-linux/tools/proguard/proguard-android.txt
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


-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
 -dontwarn com.fasterxml.jackson.databind.**
 -keep class org.codehaus.** { *; }
 -keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class com.frostox.calculo.entities.** {
  public void set*(***);
  public *** get*();
  public boolean is*();
}
-keep public class com.frostox.calculo.entities.wrappers.** {
  public void set*(***);
  public *** get*();
  public boolean is*();
}
-keep class com.frostox.calculo.view_holders.** { *; }

-keep class com.frostox.calculo.nodes.** { *; }

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}


-dontwarn com.squareup.okhttp.**
-dontwarn javax.annotation.**
-dontwarn java.lang.**
-dontwarn libcore.**
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-dontwarn com.google.**
-dontwarn com.facebook.**
-dontwarn com.android.**
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-dontwarn twitter4j.**

-keep class com.shaded.fasterxml.jackson.** { *; }
