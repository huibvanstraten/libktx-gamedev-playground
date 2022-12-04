package com.hvs.annihilation.ecs.dead

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import ktx.log.logger

@AllOf([DeadComponent::class])
class DeadSystem(
    private val lifeComponents: ComponentMapper<LifeComponent>,
    private val deadComponents: ComponentMapper<DeadComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val stage: Stage
) : IteratingSystem() {

    //TODO: conditional keeps getting fired [DEBUG] com.hvs.annilitation.ecs.dead.DeadSystem - Entity Entity(id=8) with animation gets removed
    // conclusion? entity doesnt get removed from deadComponents. WHy? i am not importing DeadComponent. when we remove it out of the list, it gets put back in
    override fun onTickEntity(entity: Entity) {
        val deadComp = deadComponents[entity]
        if (deadComp.reviveTime == 0f) {
//                stage.fire(EntityDeathEvent(animationComponents[entity].model))
            log.debug { "Entity $entity with animation gets removed" }
            world.remove(entity)
            return
        }

        deadComp.reviveTime -= deltaTime
        if (deadComp.reviveTime <= 0f) {
            with(lifeComponents[entity]) { life = maximumLife }
            configureEntity(entity) { deadComponents.remove(entity) }
        }
    }

    companion object {
        private val log = logger<DeadSystem>()
    }
}
