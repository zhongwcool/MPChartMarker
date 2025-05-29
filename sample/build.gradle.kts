plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.alex.mpchart.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alex.mpchart.sample"
        minSdk = 31
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // K线标记库依赖
    implementation(project(":kline-marker-lib"))
    implementation(libs.mpandroidchart)

    // 网络请求库
    implementation(libs.okhttp)

    // Markdown渲染库 - 排除冲突的annotations依赖
    implementation("io.noties.markwon:core:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("io.noties.markwon:html:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation("io.noties.markwon:image:4.6.2") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}