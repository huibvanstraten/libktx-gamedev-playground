package com.hvs.annihilation.actioncommand.command

import com.hvs.annihilation.actioncommand.ActionCommand
import com.hvs.annihilation.state.PlayerEntity

class AttackCommand(
    private val entity: PlayerEntity
): ActionCommand {

    override fun execute() {
        entity.attack()
    }
}
