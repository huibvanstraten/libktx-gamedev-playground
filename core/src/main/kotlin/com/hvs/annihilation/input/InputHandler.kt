package com.hvs.annihilation.input

import com.hvs.annihilation.state.PlayerEntity
import com.hvs.annihilation.input.buttoninput.Input

class InputHandler(
    private val playerEntity: PlayerEntity
) {

    fun handleInput(input: Input) {
        val command = playerEntity.inputConfig.mappedCommands.getValue(input)
        command.execute()
    }
}
