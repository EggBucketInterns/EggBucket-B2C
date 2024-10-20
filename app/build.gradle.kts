plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}


android {
    namespace = "com.eggbucket.eggbucket_b2c"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.eggbucket.eggbucket_b2c"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation("androidx.databinding:databinding-common:8.7.1")
    implementation("androidx.databinding:databinding-runtime:8.7.1")
    implementation("androidx.security:security-crypto-ktx:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.pinview)

    implementation ("androidx.fragment:fragment-ktx:1.8.3")
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.firebase:firebase-auth-ktx:22.0.0")
    implementation ("com.google.firebase:firebase-bom:32.0.0")
    implementation("io.insert-koin:koin-android:4.0.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

}