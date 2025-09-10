plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.pizzamania"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pizzamania"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    // Material Design Components (if not already added)
    implementation("com.google.android.material:material:1.10.0")
// RecyclerView (if not already added)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
// Coordinator Layout (if not already added)
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
// For image loading (optional but recommended)
    implementation("com.github.bumptech.glide:glide:4.15.1")


}