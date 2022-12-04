package com.hvs.annihilation.actioncommand.action

import com.github.quillraven.fleks.World

class MoveStopAction (
    world: World
) : MoveAction(world) {

    fun stop() {
        playerSin = 0f
        updatePlayerMovement()
    }
}
