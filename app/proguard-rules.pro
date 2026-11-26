# OurWorld - Proguard Rules
# Keep Firebase models
-keepclassmembers class com.ourworld.app.data.model.** { *; }
-keep class com.ourworld.app.data.model.** { *; }

# Keep Hilt generated classes  
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
