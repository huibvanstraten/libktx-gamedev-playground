package com.hvs.annihilation.input.configuration

import com.hvs.annihilation.actioncommand.ActionCommand
import com.hvs.annihilation.input.buttoninput.Input

data class InputConfig(
    val mappedCommands: MutableMap<Input, ActionCommand>
)

