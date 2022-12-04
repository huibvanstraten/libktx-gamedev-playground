package com.hvs.annihilation.ecs.collision

import com.badlogic.gdx.scenes.scene2d.Stage
import com.hvs.annihilation.event.CollisionDespawnEvent
import com.hvs.annihilation.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.tiled.TiledComponent

@AllOf([TiledComponent::class])
class CollisionDespawnSystem(
    private val tiledComponents: ComponentMapper<TiledComponent>,
    private val stage: Stage
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(tiledComponents[entity]) {
            if (nearbyEntities.isEmpty()) {
                stage.fire(CollisionDespawnEvent(cell))
                world.remove(entity)
            }
        }
    }
}
