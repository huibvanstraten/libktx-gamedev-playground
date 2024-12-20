@file:JvmName("Lwjgl3Launcher")

package com.hvs.annihilation.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.hvs.annihilation.Annihilation

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(Annihilation(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Annihilation")
        setMaximized(true)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
