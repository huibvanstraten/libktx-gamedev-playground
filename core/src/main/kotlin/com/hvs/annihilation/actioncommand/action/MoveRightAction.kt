package com.hvs.annihilation.actioncommand.action

import com.github.quillraven.fleks.World

class MoveRightAction(
    world: World,
): MoveAction(world) {

    fun moveRight() {
        playerSin = 1f
        updatePlayerMovement()
    }
}
