package com.hvs.annihilation.ecs.loot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.enums.AnimationType
import com.hvs.annihilation.event.EntityLootEvent
import com.hvs.annihilation.event.fire

@AllOf([LootComponent::class])
class LootSystem(
    private val lootComponents: ComponentMapper<LootComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val stage: Stage
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(lootComponents[entity]) {
            if (interactEntity == null) {
                return
            }

            stage.fire(EntityLootEvent(animationComponents[entity].model))

            configureEntity(entity) { lootComponents.remove(it)}
            animationComponents.getOrNull(entity)?.let { animationComponent ->
                animationComponent.nextAnimation(AnimationType.OPEN)
                animationComponent.playMode = Animation.PlayMode.NORMAL
            }

            Gdx.app.getPreferences("Looted Chests").putInteger(mapId.toString(), mapId)
        }
    }
}
