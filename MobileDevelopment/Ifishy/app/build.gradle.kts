plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.ifishy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ifishy"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
    buildTypes {
        debug{
            buildConfigField("String","USER_PREFERENCE","\"user_preferences\"")
            buildConfigField("String","BASE_URL","\"https://bursting-proper-cougar.ngrok-free.app/api/auth/\"")
        }
        release {
            buildConfigField("String","USER_PREFERENCE","\"user_preferences\"")
            buildConfigField("String","BASE_URL","\"https://bursting-proper-cougar.ngrok-free.app/api/auth/")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.kotlinx.coroutines.android)

    //ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //Lottie
    implementation(libs.lottie)

    //Shimmer
    implementation(libs.shimmer)

    //Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    //Retrofit
    implementation(libs.converter.gson)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)

    //DataStore
    implementation(libs.androidx.datastore.preferences)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //SplashScreen
    implementation(libs.androidx.core.splashscreen)

    //Glide
    implementation (libs.glide)


}