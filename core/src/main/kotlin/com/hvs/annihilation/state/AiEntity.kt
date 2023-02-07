package com.hvs.annihilation.state

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.hvs.annihilation.ecs.ai.AiComponent
import com.hvs.annihilation.ecs.ai.AiComponent.Companion.NO_TARGET
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.attack.AttackComponent
import com.hvs.annihilation.ecs.image.ImageComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.move.MoveComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.ecs.state.StateComponent
import com.hvs.annihilation.enums.AnimationType
import com.hvs.annihilation.event.fire
import ktx.math.component1
import ktx.math.component2

class AiEntity(
    val entity: Entity,
    private val world: World,
    private val stage: Stage,
    private val animationComponents: ComponentMapper<AnimationComponent> = world.mapper(),
    private val moveComponents: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackComponents: ComponentMapper<AttackComponent> = world.mapper(),
    private val stateComponents: ComponentMapper<StateComponent> = world.mapper(),
    private val lifeComponents: ComponentMapper<LifeComponent> = world.mapper(),
    private val physicsComponents: ComponentMapper<PhysicsComponent> = world.mapper(),
    private val aiComponents: ComponentMapper<AiComponent> = world.mapper(),
    private val imageComponents: ComponentMapper<ImageComponent> = world.mapper(),
    private val playerComponents: ComponentMapper<PlayerComponent> = world.mapper()
):com.hvs.annihilation.state.Entity {

    val location: Vector2
        get() = physicsComponents[entity].body.position

    val target: Entity
        get() = aiComponents[entity].target

    val isAnimationDone: Boolean
        get() = animationComponents[entity].isAnimationDone

    private val attackComp: AttackComponent
        get() = attackComponents[entity]

    fun animation(type: AnimationType, mode: PlayMode = PlayMode.LOOP, resetAnimation: Boolean = false) {
        with(animationComponents[entity]) {
            nextAnimation(type)
            playMode = mode
            if (resetAnimation) {
                stateTime = 0f
            }
        }
    }

    fun inTargetRange(range: Float): Boolean {
        val aiCmp = aiComponents[entity]
        if (aiCmp.target == NO_TARGET) return true

        val physicCmp = physicsComponents[entity]
        val targetPhysicCmp = physicsComponents[aiCmp.target]
        val (sourceX, sourceY) = physicCmp.body.position
        val (sourceOffX, sourceOffY) = physicCmp.offset
        var (sourceSizeX, sourceSizeY) = physicCmp.size
        sourceSizeX += range
        sourceSizeY += range
        val (targetX, targetY) = targetPhysicCmp.body.position
        val (targetOffX, targetOffY) = targetPhysicCmp.offset
        val (targetSizeX, targetSizeY) = targetPhysicCmp.size

        TMP_RECT1.set(
            sourceOffX + sourceX - sourceSizeX * 0.5f,
            sourceOffY + sourceY - sourceSizeY * 0.5f,
            sourceSizeX,
            sourceSizeY
        )
        TMP_RECT2.set(
            targetOffX + targetX - targetSizeX * 0.5f,
            targetOffY + targetY - targetSizeY * 0.5f,
            targetSizeX,
            targetSizeY
        )

        return TMP_RECT1.overlaps(TMP_RECT2)
    }

    fun inRange(range: Float, target: Vector2): Boolean {
        val physicsComp = physicsComponents[entity]
        val (sourceX, sourceY) = physicsComp.body.position
        val (offsetX, offsetY) = physicsComp.offset
        var (sizeX, sizeY) = physicsComp.size
        sizeX += range
        sizeY += range

        TMP_RECT1.set(
            sourceX + offsetX - sizeX * 0.5f,
            sourceY + offsetY - sizeY * 0.5f,
            sizeX,
            sizeY
        )

        return TMP_RECT1.contains(target)
    }




    fun state(next: EntityState, immediateChange: Boolean = false) {
        with(stateComponents[entity]) {
            nextState = next
            if (immediateChange) {
                stateMachine.changeState(nextState)
            }
        }
    }

    fun root(enable: Boolean) {
        with(moveComponents[entity]) { root = enable }
    }

    fun stopMovement() = with(moveComponents[entity]) {
        x = 0f
        y = 0f
    }

    fun moveTo(target: Vector2) {
        val (targetX, targetY) = target
        val physicsComp = physicsComponents[entity]
        val (sourceX, sourceY) = physicsComp.body.position

        with(moveComponents[entity]) {
            val angleRadiant = MathUtils.atan2(targetY - sourceY, targetX - sourceX)
            x = MathUtils.cos(angleRadiant)
            y = MathUtils.sin(angleRadiant)
        }
    }

    fun findNearbyEnemy(): Boolean {
        with(aiComponents[entity]) {
            target = nearByEntities.firstOrNull {
                it in playerComponents && !lifeComponents[it].isDead
            } ?: NO_TARGET
            return target != NO_TARGET
        }
    }

    fun checkTargetStillNearby() {
        with(aiComponents[entity]) {
            if (target !in nearByEntities) {
                target = NO_TARGET
            }
        }
    }

    fun moveToTarget() {
        val aiCmp = aiComponents[entity]
        if (aiCmp.target == NO_TARGET) {
            with(moveComponents[entity]) {
                x = 0f
                y = 0f
            }
            return
        }

        val targetPhysicCmp = physicsComponents[aiCmp.target]
        moveTo(targetPhysicCmp.body.position)
    }

    fun moveSlow(slowed: Boolean) {
        moveComponents[entity].slow = slowed
    }

    fun canAttack(extraRange: Float): Boolean {
        val aiCmp = aiComponents[entity]
        if (aiCmp.target == NO_TARGET) {
            return false
        }

        val attackCmp = attackComp
        return attackCmp.isReady && inTargetRange(extraRange)
    }

    fun attack() {
        with(attackComp) {
            doAttack = true
            startAttack()
        }

        val x = physicsComponents[entity].body.position.x
        val targetX = physicsComponents[target].body.position.x
        imageComponents[entity].image.flipX = targetX < x
    }

    fun fireEvent(event: Event) {
        stage.fire(event)
    }

    companion object {
        val TMP_RECT1 = Rectangle()
        val TMP_RECT2 = Rectangle()
    }
}
