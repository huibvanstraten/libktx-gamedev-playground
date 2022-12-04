package com.hvs.annihilation.actioncommand.action

import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import com.hvs.annihilation.ecs.attack.AttackComponent

class AttackAction(
    world: World,
    private val attackComponents: ComponentMapper<AttackComponent> = world.mapper()
) {

    private val playerEntities = world.family(allOf = arrayOf(AttackComponent::class))

    fun attack() {
        playerEntities.forEach {
            with(attackComponents[it]) {
                doAttack = true
            }
        }
    }
}
