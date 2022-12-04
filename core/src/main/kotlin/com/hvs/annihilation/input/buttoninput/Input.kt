package com.hvs.annihilation.input.buttoninput

interface Input {

    fun buttonCode(): Int

    fun buttonIsDown(): Boolean

    fun buttonIsUp(): Boolean
}
