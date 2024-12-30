plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "com.elegidocodes.android.util"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            minCompileSdk = 21
        }

    }

    /* For different flavors:
    productFlavors {
        register("foo") {
            aarMetadata {
                minCompileSdk = 21
            }
        }
    }*/

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar() // Optional: Include sources jar
        }
    }

    /*testFixtures {
        enable = true
    }*/

}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.elegidocodes" // Represents your organization or domain
            artifactId = "android-util" // Represents the library itself
            version = "1.0.0-alpha" // Semantic versioning

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}