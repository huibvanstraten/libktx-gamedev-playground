package com.hvs.annihilation.actioncommand.action

import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import com.hvs.annihilation.ecs.jump.JumpComponent
import com.hvs.annihilation.ecs.jump.JumpOrder
import com.hvs.annihilation.ecs.player.PlayerComponent

class StopJumpAction(
    world: World,
    private val jumpComponents: ComponentMapper<JumpComponent> = world.mapper()
) {
    private val playerEntities = world.family(allOf = arrayOf(JumpComponent::class, PlayerComponent::class))

    fun fall() {
        playerEntities.forEach {
            with(jumpComponents[it]) {
                order = JumpOrder.NONE
            }
        }
    }
}
