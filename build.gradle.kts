// Contenido para el build.gradle.kts en la RAÍZ del proyecto

plugins {
    // Aquí se define la versión de los plugins para todo el proyecto
    id("com.android.application") version "8.9.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.dagger.hilt.android") version "2.52" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}
