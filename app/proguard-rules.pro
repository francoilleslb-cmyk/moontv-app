# Moon TV ProGuard Rules

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson / modelos
-keepattributes *Annotation*
-keep class com.moontv.app.models.** { *; }
-keep class com.google.gson.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ExoPlayer / Media3
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
