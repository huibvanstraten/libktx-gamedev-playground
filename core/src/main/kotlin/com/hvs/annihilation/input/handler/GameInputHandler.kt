package com.hvs.annihilation.input.handler

import com.badlogic.gdx.scenes.scene2d.Stage
import com.hvs.annihilation.event.GamePauseEvent
import com.hvs.annihilation.event.GameResumeEvent
import com.hvs.annihilation.event.fire
import com.hvs.annihilation.input.XboxInputProcessor
import com.hvs.annihilation.state.PlayerEntity
import com.hvs.annihilation.input.buttoninput.Input

class GameInputHandler(
    private val playerEntity: PlayerEntity,
    private val gameStage: Stage
) {
    var paused: Boolean = false

    fun handleInput(input: Input) {
        if (input.buttonCode() == XboxInputProcessor.BUTTON_START) {
            paused = !paused

            if (paused) gameStage.fire(GamePauseEvent())
            else gameStage.fire(GameResumeEvent())
            return
        }

        val command = playerEntity.inputConfig.mappedCommands.getValue(input)
        command.execute()
    }
}
