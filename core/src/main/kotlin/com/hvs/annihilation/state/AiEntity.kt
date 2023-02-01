package com.hvs.annihilation.state

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.hvs.annihilation.ecs.ai.AiComponent
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.attack.AttackComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.move.MoveComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.ecs.state.StateComponent
import com.hvs.annihilation.enums.AnimationType
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2

class AiEntity(
    private val entity: Entity,
    private val world: World,
    private val animationComponents: ComponentMapper<AnimationComponent> = world.mapper(),
    private val moveComponents: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackComponents: ComponentMapper<AttackComponent> = world.mapper(),
    private val stateComponents: ComponentMapper<StateComponent> = world.mapper(),
    private val lifeComponents: ComponentMapper<LifeComponent> = world.mapper(),
    private val physicsComponents: ComponentMapper<PhysicsComponent> = world.mapper(),
    private val aiComponents: ComponentMapper<AiComponent> = world.mapper(),
    private val playerComponents: ComponentMapper<PlayerComponent> = world.mapper()
):com.hvs.annihilation.state.Entity {

    val location: Vector2
        get() = physicsComponents[entity].body.position

    val wantsToRun: Boolean
        get() = moveComponents[entity].y !=0f

    val wantsToAttack: Boolean
        get() = attackComponents.getOrNull(entity)?.doAttack ?: false

    val attackComp: AttackComponent
        get() = attackComponents[entity]

    val isDead: Boolean
        get() = lifeComponents[entity].life <= 0f

    fun animation(type: AnimationType, mode: PlayMode = PlayMode.LOOP, resetAnimation: Boolean = false) {
        with(animationComponents[entity]) {
            nextAnimation(type)
            playMode = mode
            if (resetAnimation) {
                stateTime = 0f
            }
        }
    }

    val isAnimationDone: Boolean
        get() = animationComponents[entity].isAnimationDone


    fun state(next: EntityState, immediateChange: Boolean = false) {
        with(stateComponents[entity]) {
            nextState = next
            if (immediateChange) {
                stateMachine.changeState(nextState)
            }
        }
    }

    fun enableGlobalState(enable: Boolean) {
        with(stateComponents[entity]) {
            if (enable) {
                stateMachine.globalState = DefaultGlobalState.CHECK_ALIVE
            } else {
                stateMachine.globalState = null
            }
        }
    }

    fun changeToPreviousState() {
        with(stateComponents[entity]) { nextState = stateMachine.previousState }
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
            val target = nearByEntities.firstOrNull {
                it in playerComponents && !lifeComponents[it].isDead
            }.also { if (it != null) println("PLAYER IS nearby") } ?: -1
            return target != -1
        }
    }

    fun inRange(range: Float, target: Vector2): Boolean {
        val physicsComp = physicsComponents[entity]
        val (sourceX, sourceY) = physicsComp.body.position
        val (offsetX, offsetY) = physicsComp.offset
        var (sizeX, sizeY) = physicsComp.size
        sizeX += range
        sizeY += range

        TEMP_RECTANGLE.set(
            sourceX + offsetX - sizeX * 0.5f,
            sourceY + offsetY - sizeY * 0.5f,
            sizeX,
            sizeY
        )

        return TEMP_RECTANGLE.contains(target)
    }

    fun moveSlow(slowed: Boolean) {
        moveComponents[entity].slow = slowed
    }

    fun canAttack(): Boolean {

        if (!attackComponents[entity].isReady) return false

        val enemy = nearbyEnemies().firstOrNull() ?: return false

        val enemyPhysicsComp = physicsComponents[enemy]
        val (sourceX, sourceY) = enemyPhysicsComp.body.position
        val (offsetX, offsetY) = enemyPhysicsComp.offset

        return  inRange(4.5f + attackComp.extraRange, vec2( sourceX + offsetX, sourceY + offsetY))
    }

    fun startAttack() {
        with(attackComponents[entity]) {
            doAttack = true
            startAttack()
        }
    }

    fun hasEnemyNearby(): Boolean = nearbyEnemies().isNotEmpty()

    private fun nearbyEnemies(): List<Entity> = with(aiComponents[entity]) {
        this.nearByEntities.filter { it in playerComponents && !lifeComponents[it].isDead }
    }

    companion object {
        private val TEMP_RECTANGLE = Rectangle()
    }
}
