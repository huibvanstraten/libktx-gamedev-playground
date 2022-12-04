package com.hvs.annihilation.input

import com.badlogic.gdx.controllers.Controller
import com.hvs.annihilation.input.buttoninput.ButtonAInput
import com.hvs.annihilation.input.buttoninput.ButtonBInput
import com.hvs.annihilation.input.buttoninput.ButtonLeftInput
import com.hvs.annihilation.input.buttoninput.ButtonRightInput
import com.hvs.annihilation.input.buttoninput.ButtonStartInput
import ktx.log.logger

class InputConverter(
    private val inputHandler: InputHandler
) : XboxInputProcessor {

    private var pressedButtons = mutableSetOf<Int>()

    init {
        addXboxControllerListener()
    }

    override fun buttonDown(controller: Controller, buttonCode: Int): Boolean {

        if (buttonCode.isImplementedButton()) {
            pressedButtons.add(buttonCode)
            when (buttonCode) {
                A -> inputHandler.handleInput(ButtonAInput(buttonCode, buttonDown = true, buttonUp = false))
                B -> inputHandler.handleInput(ButtonBInput(buttonCode, buttonDown = true, buttonUp = false))
                LEFT -> inputHandler.handleInput(ButtonLeftInput(buttonCode,buttonDown = true, buttonUp = false))
                RIGHT -> inputHandler.handleInput(ButtonRightInput(buttonCode,buttonDown = true, buttonUp = false))
//                START -> inputHandler.handleInput(ButtonStartInput(buttonCode, buttonDown = true, buttonUp = false))
            }
            return true
        }
        if (buttonCode == START) {

        }

        return false
    }

    override fun buttonUp(controller: Controller, buttonCode: Int): Boolean {
        if (buttonCode.isMovementKey()) {
            when (buttonCode) {
                A -> {
                    log.debug { "Pressing A" }
                    inputHandler.handleInput(ButtonAInput(buttonCode, buttonDown = false, buttonUp = true))
                    pressedButtons.remove(A)
                }
                LEFT -> {
                    inputHandler.handleInput(ButtonLeftInput(buttonCode, buttonDown = false, buttonUp = true))
                    pressedButtons.remove(LEFT)
                }
                RIGHT ->  {
                    inputHandler.handleInput(ButtonRightInput(buttonCode, buttonDown = false, buttonUp = true))
                    pressedButtons.remove(RIGHT)
                }
            }
            return true
        }
        return false
    }

    private fun Int.isMovementKey(): Boolean = this == LEFT || this == RIGHT

    private fun Int.isImplementedButton(): Boolean = this == LEFT || this == RIGHT || this == B || this == A

    private fun isPressed(button: Int): Boolean = button in pressedButtons

    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float) = false

    companion object {
        private val log = logger<InputConverter>()

        private const val DOWN = XboxInputProcessor.BUTTON_DOWN
        private const val UP = XboxInputProcessor.BUTTON_UP
        private const val LEFT = XboxInputProcessor.BUTTON_LEFT
        private const val RIGHT = XboxInputProcessor.BUTTON_RIGHT
        private const val B = XboxInputProcessor.BUTTON_B
        private const val A = XboxInputProcessor.BUTTON_A
        private const val START = XboxInputProcessor.BUTTON_START
    }
}
