package com.hvs.annihilation.ecs.camera

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.image.ImageComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.event.MapChangeEvent
import ktx.tiled.height
import ktx.tiled.width

//this is no good for multiple players
@AllOf([PlayerComponent::class, ImageComponent::class])
class CameraSystem(
    private val imageComponents: ComponentMapper<ImageComponent>,
    stage: Stage
): EventListener, IteratingSystem() {
    private val camera = stage.camera
    private var maximumWidth = 0f
    private var maximumHeight = 0f


    override fun onTickEntity(entity: Entity) {
        with (imageComponents[entity]) {
            val viewWidth = camera.viewportWidth * 0.5f
            val viewHeight = camera.viewportHeight * 0.5f
            camera.position.set(
                image.x.coerceIn(viewWidth, maximumWidth - viewWidth),
                image.y.coerceIn(viewHeight, maximumHeight - viewHeight),
                camera.position.z
            )
        }
    }

    override fun handle(event: Event): Boolean {
        if (event is MapChangeEvent) {
            maximumWidth = event.map.width.toFloat()
            maximumHeight = event.map.height.toFloat()
            return true
        }
        return false
    }
}
