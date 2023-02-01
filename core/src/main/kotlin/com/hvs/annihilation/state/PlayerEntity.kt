package com.hvs.annihilation.state

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.hvs.annihilation.actioncommand.action.AttackAction
import com.hvs.annihilation.actioncommand.action.JumpAction
import com.hvs.annihilation.actioncommand.action.MoveLeftAction
import com.hvs.annihilation.actioncommand.action.MoveRightAction
import com.hvs.annihilation.actioncommand.action.MoveStopAction
import com.hvs.annihilation.actioncommand.action.StopJumpAction
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.attack.AttackComponent
import com.hvs.annihilation.ecs.collision.CollisionComponent
import com.hvs.annihilation.ecs.jump.JumpComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.move.MoveComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.state.StateComponent
import com.hvs.annihilation.enums.AnimationType
import com.hvs.annihilation.input.configuration.InputConfig
import com.hvs.annihilation.input.configuration.InputConfigurator

class PlayerEntity(
    private val entity: Entity,
    private val world: World,
    private val animationComponents: ComponentMapper<AnimationComponent> = world.mapper(),
    private val moveComponents: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackComponents: ComponentMapper<AttackComponent> = world.mapper(),
    private val stateComponents: ComponentMapper<StateComponent> = world.mapper(),
    private val lifeComponents: ComponentMapper<LifeComponent> = world.mapper(),
    private val physicsComponents: ComponentMapper<PhysicsComponent> = world.mapper(),
    private val jumpComponents: ComponentMapper<JumpComponent> = world.mapper(),
    private val collisionComponents: ComponentMapper<CollisionComponent> = world.mapper(),
    private val jumpAction: JumpAction = JumpAction(world),
    private val stopJumpAction: StopJumpAction = StopJumpAction(world),
    private val moveLeftAction: MoveLeftAction = MoveLeftAction(world),
    private val moveRightAction: MoveRightAction = MoveRightAction(world),
    private val moveStopAction: MoveStopAction = MoveStopAction(world),
    private val attackAction: AttackAction = AttackAction(world)
): com.hvs.annihilation.state.Entity {

    var inputConfig: InputConfig = InputConfigurator(this).presetInputMap()

    fun jump() {
        jumpAction.jump()
    }

    fun stopJump() {
        stopJumpAction.fall()
    }

    fun moveLeft() {
        moveLeftAction.moveLeft()
    }

    fun moveRight() {
        moveRightAction.moveRight()
    }

    fun attack() {
        attackAction.attack()
    }

    fun stop() {
        moveStopAction.stop()
    }

    val position: Vector2
        get() = physicsComponents[entity].body.position

    val wantsToRun: Boolean
        get() {
            val moveComp = moveComponents[entity]
            return moveComp.x != 0f
        }

    val wantsToAttack: Boolean
        get() = attackComponents.getOrNull(entity)?.doAttack ?: false

    val attackComp: AttackComponent
        get() = attackComponents[entity]

    val jumpComp: JumpComponent
        get() = jumpComponents[entity]

    val collComp: CollisionComponent
        get() = collisionComponents[entity]

    val physicsComp: PhysicsComponent
        get() = physicsComponents[entity]

    val stateComp: StateComponent
        get() = stateComponents[entity]

    val isDead: Boolean
        get() = lifeComponents[entity].life <= 0f

    fun animation(type: AnimationType, mode: Animation.PlayMode = Animation.PlayMode.LOOP, resetAnimation: Boolean = false) {
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


    fun setState(next: EntityState, immediateChange: Boolean = false) {
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

    fun startAttack() {
        with(attackComponents[entity]) { startAttack() }
    }

    fun isFalling(physic: PhysicsComponent, collision: CollisionComponent) =
        physic.body.linearVelocity.y < 0f && collision.numGroundContacts == 0
}
