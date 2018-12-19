# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/lixiaoning/android-sdk-linux/tools/proguard/proguard-android.txt
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
-keep class android.*.** { *;}
-keep class android.*.** { *;}
-dontwarn no.nordicsemi.android.support.v18.scanner.**
-keep class no.nordicsemi.android.support.v18.scanner.** { *;}
-dontwarn java.*.**
-keep class java.*.** { *;}
-libraryjars 'C:\Program Files\Java\jdk1.8.0_102\jre\lib\rt.jar'

-libraryjars 'D:\Software\Android\SDK\platforms\android-19\android.jar'

-optimizationpasses 5

-dontusemixedcaseclassnames

# -keep public class * extends android.app.Activity

-keep class com.yybo.mylibrary.* {

public <fields>;

public <methods>;

}
