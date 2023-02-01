package com.hvs.annihilation.actioncommand.action

import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import com.hvs.annihilation.ecs.move.MoveComponent
import com.hvs.annihilation.ecs.player.PlayerComponent

abstract class MoveAction(
    world: World,
    private val moveComponents: ComponentMapper<MoveComponent> = world.mapper()
) {

    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))
    protected var playerCos = 0f
    protected var playerSin = 0f

    protected fun updatePlayerMovement() {
        playerEntities.forEach { player ->
            with (moveComponents[player]) {
                y = playerCos
                x = playerSin
            }
        }
    }
}
