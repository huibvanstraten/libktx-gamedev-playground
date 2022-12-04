package com.hvs.annihilation.ecs.ai

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.NoneOf
import com.hvs.annihilation.ecs.dead.DeadComponent

@AllOf([AiComponent::class])
@NoneOf([DeadComponent::class])
class AiSystem(
    private val aiComponents: ComponentMapper<AiComponent>
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(aiComponents[entity]) {
            behaviorTree.step()
        }
    }
}
