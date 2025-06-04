plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "com.alex.klinemarker"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // 添加版本信息
        version = "1.0.0"
    }

    testOptions {
        targetSdk = 35
    }
    lint {
        targetSdk = 35
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

    // 启用发布构建变体
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // MPAndroidChart 依赖
    api(libs.mpandroidchart)

    // Android 基础依赖
    implementation(libs.appcompat)
    implementation(libs.core)

    // 测试依赖
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Maven 发布配置
publishing {
    publications {
        register<MavenPublication>("release") {
            // 发布信息
            groupId = "com.alex.klinemarker"
            artifactId = "kline-marker-lib"
            version = "1.0.0"

            // 绑定 Android Library 的发布组件
            afterEvaluate {
                from(components["release"])
            }

            // POM 文件配置
            pom {
                name.set("KLine Marker Library")
                description.set("A powerful Android library for adding markers and trend regions to MPAndroidChart K-line charts")
                url.set("https://github.com/zhongwcool/MPChartMarker")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("zhongwcool")
                        name.set("zhong")
                        email.set("zhongwcool@163.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/zhongwcool/MPChartMarker.git")
                    developerConnection.set("scm:git:ssh://github.com/zhongwcool/MPChartMarker.git")
                    url.set("https://github.com/zhongwcool/MPChartMarker")
                }
            }
        }
    }
}

// 发布配置可以后续添加 