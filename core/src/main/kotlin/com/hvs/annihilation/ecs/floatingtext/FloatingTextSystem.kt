package com.hvs.annihilation.ecs.floatingtext

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import ktx.math.vec2

@AllOf([FloatingTextComponent::class])
class FloatingTextSystem(
    private val gameStage: Stage,
    @Qualifier("uiStage") private val uiStage: Stage,
    private val textComponents: ComponentMapper<FloatingTextComponent>
) : IteratingSystem() {
    private val uiLocation = vec2()
    private val uiTarget = vec2()

    override fun onTickEntity(entity: Entity) {
        with(textComponents[entity]) {
            if (time >= lifeSpan) {
                world.remove(entity)
                return
            }

            // this transforms ui coordinates to gamestage coordinates
            uiLocation.toUiCoordinates(textLocation)
            uiTarget.toUiCoordinates(textTarget)

            //makes the text move
            uiLocation.interpolate(uiTarget, (time / lifeSpan).coerceAtMost(1f), Interpolation.smooth2)

            //project or unproject is giving y position upside down so we need to change it
            label.setPosition(uiLocation.x, uiStage.viewport.worldHeight - uiLocation.y)

            time += deltaTime
        }
    }

    private fun Vector2.toUiCoordinates(from: Vector2) {
        this.set(from)
        gameStage.viewport.project(this)
        uiStage.viewport.unproject(this)
    }
}
