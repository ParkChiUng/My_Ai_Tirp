import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
android {
    namespace = "com.sessac.myaitrip"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sessac.myaitrip"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

//        local.properties 내부에서 key값을 가져오는 함수 구현방식
        buildConfigField("String","KAKAO_NATIVE_KEY",
            "\"${getApiKey("KAKAO_NATIVE_KEY")}\"")

        manifestPlaceholders["KAKAO_NATIVE_KEY"] = getApiKey("KAKAO_NATIVE_KEY")

        buildConfigField("String","TOUR_API_SERVICE_KEY",
            "\"${getApiKey("TOUR_API_SERVICE_KEY")}\"")

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
        buildConfig = true
    }
}

// 2. local.properties 내부에서 key값을 가져오는 함수 구현방식
fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey).toString()
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Flow Binding, RxBinding
    val flowbinding_version = "1.2.0"
    implementation("io.github.reactivecircus.flowbinding:flowbinding-android:${flowbinding_version}")
//    val rxbinding_version = "4.0.0"
//    implementation ("com.jakewharton.rxbinding4:rxbinding:${rxbinding_version}")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore:24.10.2")    // FireStore Database
    implementation("com.google.firebase:firebase-storage:20.3.0")       // Storage
    implementation("com.google.firebase:firebase-auth-ktx")             // Firebase Auth

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Kakao Login
    implementation ("com.kakao.sdk:v2-user:2.19.0")

    // Data Store
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}