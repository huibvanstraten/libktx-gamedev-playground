package com.hvs.annihilation.actioncommand.action

import com.github.quillraven.fleks.World

class MoveLeftAction(
    world: World,
): MoveAction(world) {

    fun moveLeft() {
        playerSin = -1f
        updatePlayerMovement()
    }
}
