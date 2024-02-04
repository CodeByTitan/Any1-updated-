plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-android")
    id("com.google.devtools.ksp")
    id ("kotlin-kapt")
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.example.any1"

    compileSdk = 34

    buildFeatures{
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // Use the version that supports Kotlin 1.9.22
    }


    defaultConfig {
        applicationId = "com.example.any1"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    testImplementation("junit:junit:4.13.2")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation ("androidx.lifecycle:lifecycle-process:2.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Testing
    testImplementation("junit:junit:4.13.2")

// Firebase
//    implementation(platform("com.google.firebase:firebase-bom:24.0.0"))
//    implementation("com.google.gms:google-services:4.4.0")
//    implementation("com.google.firebase:firebase-firestore"){
//        exclude(group= "com.google.protobuf", module = "protobuf-javalite")
//        exclude(group= "io.grpc:grpc-protobuf-lite:1.57.2", module = "protobuf-javalite")
//    }
//
//    implementation("com.google.firebase:firebase-storage")
//    implementation("com.google.firebase:firebase-auth")

// UI and Image Loading
    implementation("com.ms-square:expandableTextView:0.1.4")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.emoji:emoji-appcompat:1.1.0")
    implementation("androidx.emoji:emoji-bundled:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.13.2")
    implementation("androidx.emoji:emoji:1.1.0")
    implementation("com.mikhaellopez:circularimageview:4.3.0")

// Permissions
    implementation("com.karumi:dexter:6.2.0")

// Authentication
    implementation("com.google.android.gms:play-services-auth:20.7.0")

// Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

// Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    implementation("androidx.compose.compiler:compiler:1.5.8")


    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.6")
//    protobuf ("com.google.api.grpc:proto-google-common-protos:2.23.0") {
//        exclude group: 'com.google.protobuf'
//    }
// Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

// Dagger - Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.42")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.50")
    testImplementation("com.google.dagger:hilt-android-testing:2.42")
    kaptTest("com.google.dagger:hilt-compiler:2.50")



}