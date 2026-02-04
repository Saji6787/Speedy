# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/lib/android/sdk/tools/proguard/proguard-android.txt
# and each project's build.gradle file.

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.CoroutineExceptionHandler {
    <init>(...);
}

# AndroidX WorkManager
-keep class androidx.work.Worker { *; }
-keep class androidx.work.CoroutineWorker { *; }
-keep class com.example.datausage.worker.** { *; }

# DataStore
-keep class androidx.datastore.preferences.protobuf.** { *; }

# Networking
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.** 
-dontwarn okio.**

# Compose
# Generally R8 handles Compose well, adding standard keeps just in case of reflection usage
-keep class androidx.compose.ui.ui.** { *; }
