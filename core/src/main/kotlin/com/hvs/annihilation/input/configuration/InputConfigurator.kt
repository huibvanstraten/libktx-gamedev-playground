package com.hvs.annihilation.input.configuration

import com.hvs.annihilation.actioncommand.ActionCommand
import com.hvs.annihilation.actioncommand.command.AttackCommand
import com.hvs.annihilation.actioncommand.command.JumpCommand
import com.hvs.annihilation.actioncommand.command.JumpStopCommand
import com.hvs.annihilation.actioncommand.command.MoveLeftCommand
import com.hvs.annihilation.actioncommand.command.MoveRightCommand
import com.hvs.annihilation.actioncommand.command.MoveStopCommand
import com.hvs.annihilation.state.PlayerEntity
import com.hvs.annihilation.input.XboxInputProcessor
import com.hvs.annihilation.input.buttoninput.ButtonAInput
import com.hvs.annihilation.input.buttoninput.ButtonBInput
import com.hvs.annihilation.input.buttoninput.ButtonLeftInput
import com.hvs.annihilation.input.buttoninput.ButtonRightInput
import com.hvs.annihilation.input.buttoninput.ButtonStartInput
import com.hvs.annihilation.input.buttoninput.Input

class InputConfigurator(  //TODO: configurations need to be available and put in the mappedCommands map
) {

    //TODO: make config so it can read button down and button up
    //now is fixed on player... decoupling?
    fun mapCommandsToEntity(entity: PlayerEntity): InputConfig {
        return InputConfig(
            mapOf(
                ButtonBInput(XboxInputProcessor.BUTTON_B, true, false) to AttackCommand(entity),
                ButtonLeftInput(XboxInputProcessor.BUTTON_LEFT, true, false) to MoveLeftCommand(entity),
                ButtonLeftInput(XboxInputProcessor.BUTTON_LEFT, false, true) to MoveStopCommand(entity),
                ButtonRightInput(XboxInputProcessor.BUTTON_RIGHT, true, false) to MoveRightCommand(entity),
                ButtonRightInput(XboxInputProcessor.BUTTON_RIGHT, false, true) to MoveStopCommand(entity),
                ButtonAInput(XboxInputProcessor.BUTTON_A, true, false) to JumpCommand(entity),
                ButtonAInput(XboxInputProcessor.BUTTON_A, false, true) to JumpStopCommand(entity)
            )
        )
    }

    //with already preset commands
    fun mapCommands(commands: Map<Input, ActionCommand>): InputConfig {
        return InputConfig(commands)
    }
}
