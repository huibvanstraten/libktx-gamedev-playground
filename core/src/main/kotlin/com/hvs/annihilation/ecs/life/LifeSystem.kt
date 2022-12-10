package com.hvs.annihilation.ecs.life

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.hvs.annihilation.ecs.dead.DeadComponent
import com.hvs.annihilation.ecs.floatingtext.FloatingTextComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.NoneOf
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.enums.AnimationType
import com.hvs.annihilation.event.EntityTakeDamageEvent
import com.hvs.annihilation.event.fire
import ktx.assets.disposeSafely

@AllOf([LifeComponent::class])
@NoneOf([DeadComponent::class])
class LifeSystem(
    private val lifeComponents: ComponentMapper<LifeComponent>,
    private val deadComponents: ComponentMapper<DeadComponent>,
    private val playerComponents: ComponentMapper<PlayerComponent>,
    private val physicsComponents: ComponentMapper<PhysicsComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val gameStage: Stage
): IteratingSystem() {

    private val damageFont = BitmapFont(Gdx.files.internal("damage.fnt"))
    //TODO: this is the simple way. look into Skin
    private val floatingTextStyle = LabelStyle(damageFont, Color.RED)

    override fun onTickEntity(entity: Entity) {
        val lifeComp = lifeComponents[entity]
        val physicsComp = physicsComponents[entity]

        //for regeneration
        lifeComp.life = (lifeComp.life + lifeComp.regeneration * deltaTime). coerceAtMost(lifeComp.maximumLife)

        //does entity take damage?
        if(lifeComp.takeDamage > 0f) {
            lifeComp.life -= lifeComp.takeDamage
            gameStage.fire(EntityTakeDamageEvent(entity, lifeComp.takeDamage))
            floatingText(lifeComp.takeDamage.toInt().toString(), physicsComp.body.position, physicsComp.size)
            lifeComp.takeDamage = 0f

        }

        //is life 0? if so, add to list of dead entities/components
        if (lifeComp.isDead) {
            animationComponents.getOrNull(entity)?.let { animationComponent ->
                animationComponent.nextAnimation(AnimationType.DEATH)
                animationComponent.playMode = Animation.PlayMode.NORMAL
            }
            configureEntity(entity) {
                deadComponents.add(it) {
                    if (it in playerComponents) {
                        reviveTime = 7f
                    }
                }
            }
            println("DEAD")
        }
    }

    override fun onDispose() {
        damageFont.disposeSafely()
    }

    private fun floatingText(text: String, position: Vector2, size: Vector2) {
        world.entity {
            add<FloatingTextComponent> {
                textLocation.set(position.x, position.y - size.y * 0.5f)
                lifeSpan = 1.5f
                label = Label(text, floatingTextStyle)
            }
        }
    }
}
