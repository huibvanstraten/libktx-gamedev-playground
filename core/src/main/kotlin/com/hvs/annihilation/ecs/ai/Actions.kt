package com.hvs.annihilation.ecs.ai

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import com.badlogic.gdx.ai.utils.random.FloatDistribution
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.MathUtils
import com.hvs.annihilation.enums.AnimationType
import com.hvs.annihilation.state.AiEntity
import ktx.log.logger
import ktx.math.vec2

abstract class Action : LeafTask<AiEntity>() {
    val slime: AiEntity
        get() = `object`

    override fun copyTo(task: Task<AiEntity>): Task<AiEntity> = task
}

class IdleTask(
    @JvmField
    @TaskAttribute(required = true)
    var duration: FloatDistribution? = null
) : Action() {
    private var currentDuration = 0f

    override fun execute(): Status {
        if (status != Status.RUNNING) {
            slime.animation((AnimationType.IDLE))
            currentDuration = duration?.nextFloat() ?: 1f
            return Status.RUNNING
        }

        currentDuration -= GdxAI.getTimepiece().deltaTime
        if (currentDuration <= 0f) {
            return Status.SUCCEEDED
        }
        return Status.RUNNING
    }

    override fun copyTo(task: Task<AiEntity>): Task<AiEntity> {
        (task as IdleTask).duration = duration
        return task
    }

    companion object {
        private val log = logger<IdleTask>()
    }
}

class WanderTask: Action() {
    private val startLocation = vec2()
    private val targetLocation = vec2()

    override fun execute(): Status {
        when {
            status != Status.RUNNING -> {
                if (startLocation.isZero) {
                    startLocation.set(slime.location)
                }
                slime.animation(AnimationType.RUN)
                targetLocation.set(startLocation)
                targetLocation.x += MathUtils.random(-3f, 3f)
                targetLocation.y = slime.location.y
                slime.moveTo(targetLocation)
                slime.moveSlow(true)
                return Status.RUNNING
            }
            slime.inRange(0.5f, targetLocation) -> {
                slime.stopMovement()
                return Status.SUCCEEDED
            }
            slime.findNearbyEnemy() -> return Status.SUCCEEDED
        }

        return Status.RUNNING
    }

    override fun end() {
        slime.moveSlow(false)
    }

    companion object {
        private val log = logger<WanderTask>()
    }
}

class AttackTask: Action() {

    override fun execute(): Status {
        if (status != Status.RUNNING) {
            slime.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL, true)
            slime.startAttack()
            return Status.RUNNING
        }

        if (slime.isAnimationDone) {
            slime.animation(AnimationType.IDLE)
            slime.stopMovement()
            return Status.SUCCEEDED
        }

        return Status.RUNNING
    }

    companion object {
        private val log = logger<WanderTask>()
    }
}

