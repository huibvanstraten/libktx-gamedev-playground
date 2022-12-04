package com.hvs.annihilation.input.buttoninput

data class ButtonLeftInput(
    private val buttonCode: Int,
    private val buttonDown: Boolean,
    private val buttonUp: Boolean
): Input {

    override fun buttonCode() = buttonCode

    override fun buttonIsDown() = buttonDown

    override fun buttonIsUp() = buttonUp
}
