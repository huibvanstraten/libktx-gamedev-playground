package com.hvs.annihilation.ecs.attack

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.hvs.annihilation.ecs.life.LifeComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.image.ImageComponent
import com.hvs.annihilation.ecs.loot.LootComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.ecs.spawn.EntitySpawnSystem.Companion.HIT_BOX_SENSOR
import com.hvs.annihilation.enums.AttackState
import com.hvs.annihilation.event.EntityAttackEvent
import com.hvs.annihilation.event.fire
import ktx.box2d.query
import ktx.math.component1
import ktx.math.component2

@AllOf([AttackComponent::class, PhysicsComponent::class, ImageComponent::class])
class AttackSystem(
    private val attackComponents: ComponentMapper<AttackComponent>,
    private val physicsComponents: ComponentMapper<PhysicsComponent>,
    private val imageComponents: ComponentMapper<ImageComponent>,
    private val lifeComponents: ComponentMapper<LifeComponent>,
    private val playerComponents: ComponentMapper<PlayerComponent>,
    private val lootComponents: ComponentMapper<LootComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val physicsWorld: World,
    private val stage: Stage
    //todo: split up the class. too many components
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val attackComponent = attackComponents[entity]

        if (attackComponent.isReady && !attackComponent.doAttack) {
            return
        }

        if (attackComponent.isPrepared && attackComponent.doAttack) {
            attackComponent.doAttack = false
            attackComponent.state = AttackState.ATTACKING
            attackComponent.delay = attackComponent.maxDelay

            stage.fire(EntityAttackEvent(animationComponents[entity].model))
            return
        }

        attackComponent.delay -= deltaTime
        if (attackComponent.delay <= 0f && attackComponent.isAttacking) {
            attackComponent.state = AttackState.DEAL_DAMAGE

            val physicsComp = physicsComponents[entity]
            val image = imageComponents[entity].image
            val attackLeft = image.flipX
            val (x, y) = physicsComp.body.position
            val (offsetX, offsetY) = physicsComp.offset
            val (sizeW, sizeH) = physicsComp.size
            val halfWidth = sizeW * 0.5f
            val halfHeight = sizeH * 0.5f

            if (attackLeft) {
                AABB_RECTANGLE.set(
                    x + offsetX - halfWidth - attackComponent.extraRange,
                    y + offsetY - halfHeight,
                    x + offsetX + halfWidth,
                    y + offsetY + halfHeight
                )

            } else {
                AABB_RECTANGLE.set(
                    x + offsetX - halfWidth,
                    y + offsetY - halfHeight,
                    x + offsetX + halfWidth + attackComponent.extraRange,
                    y + offsetY + halfHeight
                )
            }

            physicsWorld.query(AABB_RECTANGLE.x, AABB_RECTANGLE.y, AABB_RECTANGLE.width, AABB_RECTANGLE.height) { fixture ->
                if (fixture.userData != HIT_BOX_SENSOR) {
                    // we are only interested if we detect hit-boxes of other entities
                    return@query true
                }

                val fixtureEntity = fixture.body.userData as Entity
                if (fixtureEntity == entity) {
                    // ignore the entity itself that is attacking
                    return@query true
                }

                // fixtureEntity refers to another entity that gets hit by the attack
                configureEntity(fixtureEntity) {
                    lifeComponents.getOrNull(it)?.let { lifeComponent ->
                        lifeComponent.takeDamage += attackComponent.damage
                        println("ATTACKING")
                    }

                    if (entity in playerComponents) {
                        lootComponents.getOrNull(it)?.let { lootComponent ->
                            lootComponent.interactEntity = entity
                            println("LOOTING")
                        }
                    }
                }
                return@query true
            }
        }

        val isDone = animationComponents.getOrNull(entity)?.isAnimationDone ?: true
        if (isDone) {
            attackComponent.state = AttackState.READY
        }
    }

    companion object {
        val AABB_RECTANGLE = Rectangle()
    }
}
