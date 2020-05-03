apply(plugin = "com.google.android.gms.oss-licenses-plugin")
apply(plugin = "com.google.gms.google-services")
apply(plugin = "com.android.application")


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(Android.compileSdk)
    defaultConfig {
        resConfigs("en", "sv")
        applicationId = Android.applicationId
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        versionCode = Android.versionCode
        versionName = Android.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testOptions {
            animationsDisabled = true
//            unitTests.isIncludeAndroidResources  = true

        }
//        tasks.withType<Test> {
//            useJUnitPlatform()
//        }

    }


    signingConfigs {
        create("release") {
            // Note that none of this should be in any file that is on git or any other version control.
            // Preferably in the local.properties file.
            storeFile = file("../sample-keystore")
            storePassword = "sample"
            keyAlias = "sample"
            keyPassword = "sample"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))

            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            ext["alwaysUpdateBuildId"] = false
            isTestCoverageEnabled = true
            splits.abi.isEnable = false
            splits.density.isEnable = false
            aaptOptions.cruncherEnabled = false
        }
    }


    flavorDimensions("version")
    productFlavors {
        create("dev") {
            setDimension("version")
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

        create("prod") {
            setDimension("version")

            splits {
                density {
                    isEnable = true
                    exclude("ldpi")
                    compatibleScreens("small", "normal", "large", "xlarge")
                }

                abi {
                    isEnable = true
                    reset()

                    include("x86", "x86_64", "arm64-v8a", "armeabi-v7a")
                    isUniversalApk = false
                }
            }
        }
    }

    applicationVariants.all {
        val lintTask = tasks["lint${name.capitalize()}"]
        assembleProvider?.get()?.run {
            dependsOn += lintTask
        }
    }

    lintOptions {
        isWarningsAsErrors = true
        isCheckAllWarnings = true
        isAbortOnError = false
        setLintConfig(file("../lint.xml"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    useLibrary("android.test.runner")
}


dependencies {
    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))
    implementation(Deps.appCompat)
    implementation(Deps.constraintLayout)
    implementation(Deps.material)
    implementation(Deps.design)

    implementation(Deps.okHttp)
    implementation(Deps.retrofit)
    implementation(Deps.retrofitMoshi)
    implementation(Deps.retrofitRx)

    implementation(Deps.glide)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    kapt(Deps.glideAnnotation)

    implementation(Deps.httpLogger)

    implementation(Deps.androidxCompat)
    implementation(Deps.archLifecycle)
    implementation(Deps.navigationFragment)
    implementation(Deps.navigationUi)
    implementation(Deps.karumiDexter)

    implementation(Deps.rxJava)
    implementation(Deps.rxKotlin)
    implementation(Deps.rxAndroid)

    implementation(Deps.koin)
    implementation(Deps.koinViewModel)
    implementation(Deps.koinScope)
    implementation(Deps.koinFragment)


    implementation(Deps.timber)
    implementation(Deps.androidOss)

    implementation(Deps.paging)
    implementation(Deps.pagingrx)
    implementation(Deps.permissions)
    implementation("androidx.preference:preference-ktx:1.1.0")
    implementation("androidx.preference:preference-ktx:1.1.0")
    implementation("com.google.firebase:firebase-analytics:17.2.3")
    implementation("com.google.firebase:firebase-auth:19.2.0")
    implementation("com.google.firebase:firebase-core:17.2.3")
    implementation("com.google.firebase:firebase-messaging:20.1.2")

    implementation("com.firebaseui:firebase-ui-auth:4.3.1")
    implementation("com.facebook.android:facebook-android-sdk:5.15.0")
    implementation("com.xwray:groupie:2.1.0")
    implementation("com.xwray:groupie-kotlin-android-extensions:2.1.0")
    implementation("com.ealva:ealvatag:0.4.3")
    implementation("com.squareup.okio:okio:2.4.3")
    implementation("com.google.guava:guava:28.2-android")
    implementation("com.github.squti:Android-Wave-Recorder:1.4.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3")
    implementation ("androidx.appcompat:appcompat:1.1.0")
    implementation ("androidx.core:core-ktx:1.2.0")
    implementation ("com.chibde:audiovisualizer:2.2.0")
    implementation ("com.tyorikan:voice-recording-visualizer:1.0.3@aar")
    implementation ("com.facebook.android:facebook-login:5.15.0")


    implementation ("com.simplemobiletools:commons:5.18.13")
    implementation("me.tankery.lib:circularSeekBar:1.1.5")
    implementation("com.mikhaellopez:circularimageview:3.2.0")
    implementation("com.google.firebase:firebase-storage:19.1.1")

    //>>>>>>>>>>>>>>

    testImplementation(Deps.junit)
    testImplementation(Deps.mockk)
    testImplementation(Deps.okHttpMockServer)
    testImplementation(Deps.archTesting)
    testImplementation(Deps.livedataTesting)
    testImplementation(Deps.expekt)
    testImplementation("org.robolectric:robolectric:3.6.1")


    //Kotest
//    testImplementation("org.junit.platform:junit-platform-commons:1.6.0")
//    testImplementation("org.junit.platform:junit-platform-launcher:1.6.0")
//    testImplementation("org.junit.vintage:junit-vintage-engine:5.6.0")
//    testImplementation("org.junit.platform:junit-platform-engine:1.6.0")
//    testImplementation("io.kotlintest:kotlintest-runner-junit4:3.4.2")


    androidTestImplementation(Deps.espresso)
    androidTestImplementation(Deps.testCore)
    androidTestImplementation(Deps.testRunner)
    androidTestImplementation(Deps.androidTestRule)
    androidTestImplementation(Deps.androidMockk)
    androidTestImplementation("com.agoda.kakao:kakao:2.1.0")
    androidTestImplementation("io.kotlintest:kotlintest-runner-junit4:3.4.2") { exclude(module = "objenesis") }




}
