import groovy.json.JsonSlurper
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
}

// La configuración demo solo sirve para compilar en CI; no autentica cuentas reales.
val validateFirebaseConfiguration = tasks.register("validateFirebaseConfiguration") {
    val configFile = layout.projectDirectory.file("google-services.json")
    inputs.file(configFile).optional()
    inputs.property("allowDemoFirebase", providers.gradleProperty("allowDemoFirebase").orElse("false"))
    doLast {
        val file = inputs.files.singleFile
        check(file.isFile) {
            "Descarga google-services.json de la app Android en Firebase y guárdalo en androidApp/."
        }
        val config = JsonSlurper().parse(file) as Map<*, *>
        val project = config["project_info"] as? Map<*, *>
        val projectId = project?.get("project_id") as? String
        val allowDemo = inputs.properties["allowDemoFirebase"] == "true"
        if (allowDemo && projectId == "demo-cobrewash-ci") return@doLast
        check(projectId == "lavanderia-el-cobre-16fd5") {
            "Firebase no está configurado para CobreWash. Reemplaza androidApp/google-services.json " +
                "por el archivo real del proyecto lavanderia-el-cobre-16fd5. " +
                "La configuración demo no permite iniciar sesión; -PallowDemoFirebase=true es solo para CI."
        }
    }
}

tasks.named("preBuild") {
    dependsOn(validateFirebaseConfiguration)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.elcobre.lavanderiaelcobre"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.elcobre.lavanderiaelcobre"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
