package com.hvs.annihilation.ecs.dead

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.ecs.portal.PortalComponent
import com.hvs.annihilation.event.EntityReviveEvent
import com.hvs.annihilation.event.fire
import ktx.log.logger

@AllOf([DeadComponent::class])
class DeadSystem(
    private val lifeComponents: ComponentMapper<LifeComponent>,
    private val playerComponents: ComponentMapper<PlayerComponent>,
    private val deadComponents: ComponentMapper<DeadComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val portalComponents: ComponentMapper<PortalComponent>,
    private val gameStage: Stage,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        if (animationComponents[entity].isAnimationDone) {

            // enemies stay dead
            if (entity !in playerComponents) {
                world.remove(entity)
                return
            }

            // set player life to full
            with(lifeComponents[entity]) { life = maximumLife }

            // set player life bar to full
            gameStage.fire(EntityReviveEvent(entity))

            configureEntity(entity) { deadComponents.remove(it) }
            configureEntity(entity) {
                if (it in playerComponents)
                portalComponents.add(it) {
                    entitiesToMove.add(it)
                    // now sets to standard hardcoded resurrection point
                    toMap = "map1"
                    toPortal = 68
                }
            }
        }
    }

    companion object {
        private val log = logger<DeadSystem>()
    }
}
