package com.hvs.annihilation.ecs.dead

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.event.EntityReviveEvent
import com.hvs.annihilation.event.fire
import ktx.log.logger

@AllOf([DeadComponent::class])
class DeadSystem(
    private val lifeComponents: ComponentMapper<LifeComponent>,
    private val deadComponents: ComponentMapper<DeadComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val gameStage: Stage
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        if (animationComponents[entity].isAnimationDone) {
            val deadComp = deadComponents[entity]
            if (deadComp.reviveTime == 0f) {
                log.debug { "Entity $entity with animation gets removed" }
                world.remove(entity)
                return
            }

            deadComp.reviveTime -= deltaTime
            if (deadComp.reviveTime <= 0f) {
                log.debug { "Entity $entity gets resurrected" }
                with(lifeComponents[entity]) { life = maximumLife }
                configureEntity(entity) { deadComponents.remove(it) }
                gameStage.fire(EntityReviveEvent(entity))
            }
        }
    }

    companion object {
        private val log = logger<DeadSystem>()
    }
}
