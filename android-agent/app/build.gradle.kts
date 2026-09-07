plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose"); id("org.jetbrains.kotlin.plugin.serialization"); id("com.google.devtools.ksp") }

android { namespace = "com.example.aiandroidagent"; compileSdk = 35
    defaultConfig { applicationId = "com.example.aiandroidagent"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0"; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    buildFeatures { compose = true; buildConfig = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.01.00")); implementation("androidx.activity:activity-compose:1.10.0"); implementation("androidx.compose.material3:material3"); implementation("androidx.compose.ui:ui"); implementation("androidx.compose.ui:ui-tooling-preview"); debugImplementation("androidx.compose.ui:ui-tooling"); implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"); implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7"); implementation("androidx.navigation:navigation-compose:2.8.5"); implementation("androidx.core:core-ktx:1.15.0"); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0"); implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3"); implementation("androidx.room:room-runtime:2.6.1"); implementation("androidx.room:room-ktx:2.6.1"); ksp("androidx.room:room-compiler:2.6.1"); testImplementation("junit:junit:4.13.2"); testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
