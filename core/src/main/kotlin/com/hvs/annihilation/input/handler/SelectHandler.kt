package com.hvs.annihilation.input.handler

import com.badlogic.gdx.controllers.Controller
import com.hvs.annihilation.input.XboxInputProcessor
import com.hvs.annihilation.screen.GameScreen
import com.hvs.annihilation.screen.TitleScreen
import com.hvs.annihilation.state.PlayerEntity
import ktx.log.logger

class SelectHandler(
    private val titleScreen: TitleScreen
): XboxInputProcessor {

    init {
        addXboxControllerListener()
    }

    override fun buttonDown(
        controller: Controller,
        buttonCode: Int
    ): Boolean {
        when (buttonCode) {
            XboxInputProcessor.BUTTON_DOWN -> titleScreen.moveOption(1)
            XboxInputProcessor.BUTTON_UP -> titleScreen.moveOption(-1)
            XboxInputProcessor.BUTTON_START -> titleScreen.gameSelectMenu()
        }
        return true
    }

    override fun buttonUp(controller: Controller, buttonCode: Int) = false
    override fun axisMoved(controller: Controller, axisCode: Int, value: Float) = false

    companion object {
        private val log = logger<GameScreen>()
    }
}
