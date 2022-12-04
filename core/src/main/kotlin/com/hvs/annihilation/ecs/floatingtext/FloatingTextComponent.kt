package com.hvs.annihilation.ecs.floatingtext

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier
import ktx.actors.plusAssign
import ktx.math.vec2

class FloatingTextComponent(
    val textLocation: Vector2 = vec2(),
    var lifeSpan: Float = 0f
) {
    lateinit var label: Label
    val textTarget: Vector2 = vec2()
    var time: Float = 0f

    companion object {
        class FloatingTextComponentListener(
            @Qualifier("uiStage") private val uiStage: Stage
        ): ComponentListener<FloatingTextComponent> {
            override fun onComponentAdded(entity: Entity, component: FloatingTextComponent) {
                component.label += fadeOut(component.lifeSpan, Interpolation.pow3OutInverse)
                uiStage.addActor(component.label)
                component.textTarget.set(
                    component.textLocation.x + MathUtils.random(-1.5f, 1.5f),
                    component.textLocation.y + 1f
                )
            }

            override fun onComponentRemoved(entity: Entity, component: FloatingTextComponent) {
                uiStage.root.removeActor(component.label)
            }
        }
    }
}
