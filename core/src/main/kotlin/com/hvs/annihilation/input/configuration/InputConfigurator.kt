package com.hvs.annihilation.input.configuration

import com.hvs.annihilation.actioncommand.ActionCommand
import com.hvs.annihilation.actioncommand.command.AttackCommand
import com.hvs.annihilation.actioncommand.command.JumpCommand
import com.hvs.annihilation.actioncommand.command.JumpStopCommand
import com.hvs.annihilation.actioncommand.command.MoveLeftCommand
import com.hvs.annihilation.actioncommand.command.MoveRightCommand
import com.hvs.annihilation.actioncommand.command.MoveStopCommand
import com.hvs.annihilation.input.XboxInputProcessor
import com.hvs.annihilation.input.buttoninput.ButtonAInput
import com.hvs.annihilation.input.buttoninput.ButtonBInput
import com.hvs.annihilation.input.buttoninput.ButtonLeftInput
import com.hvs.annihilation.input.buttoninput.ButtonRightInput
import com.hvs.annihilation.input.buttoninput.ButtonSelectInput
import com.hvs.annihilation.input.buttoninput.Input
import com.hvs.annihilation.state.PlayerEntity

class InputConfigurator(
    private val entity: PlayerEntity
) {

    fun presetInputMap(): InputConfig = InputConfig(
        mutableMapOf(
            ButtonBInput(XboxInputProcessor.BUTTON_B, true, false) to AttackCommand(entity),
            ButtonLeftInput(XboxInputProcessor.BUTTON_LEFT, true, false) to MoveLeftCommand(entity),
            ButtonLeftInput(XboxInputProcessor.BUTTON_LEFT, false, true) to MoveStopCommand(entity),
            ButtonRightInput(XboxInputProcessor.BUTTON_RIGHT, true, false) to MoveRightCommand(entity),
            ButtonRightInput(XboxInputProcessor.BUTTON_RIGHT, false, true) to MoveStopCommand(entity),
            ButtonAInput(XboxInputProcessor.BUTTON_A, true, false) to JumpCommand(entity),
            ButtonAInput(XboxInputProcessor.BUTTON_A, false, true) to JumpStopCommand(entity),
        )
    )

    fun mapCommandsToEntity(
        commands: Map<Input, Int>
    ): InputConfig {
        val defaultConfig = presetInputMap()
        val configuredButtons = mutableMapOf<Input, ActionCommand>()
        commands.forEach {
            configuredButtons[it.key] = intToCommand(it.value)
        }

        defaultConfig.mappedCommands.putAll(configuredButtons)

        return defaultConfig
    }

    private fun intToCommand(command: Int): ActionCommand =
        when (command) {
            0 -> JumpCommand(entity)
            1 -> AttackCommand(entity)
            else -> JumpCommand(entity)
        }
}
