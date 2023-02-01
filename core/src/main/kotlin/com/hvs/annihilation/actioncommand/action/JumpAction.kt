package com.hvs.annihilation.actioncommand.action

import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import com.hvs.annihilation.ecs.collision.CollisionComponent
import com.hvs.annihilation.ecs.jump.JumpComponent
import com.hvs.annihilation.ecs.jump.JumpOrder
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.screen.GameScreen
import ktx.log.logger

class JumpAction(
    world: World,
    private val collisionComponents: ComponentMapper<CollisionComponent> = world.mapper(),
    private val jumpComponents: ComponentMapper<JumpComponent> = world.mapper()
) {

    private val playerEntities = world.family(allOf = arrayOf(CollisionComponent::class, PlayerComponent::class))

    fun jump() {
        playerEntities.forEach {
            with(collisionComponents[it]) {
                if (numGroundContacts != 0 && jumpComponents[it].order != JumpOrder.JUMP) {
                    jumpComponents[it].order = JumpOrder.JUMP
                }
            }
        }
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
