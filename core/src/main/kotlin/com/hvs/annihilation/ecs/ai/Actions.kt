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
    val entity: AiEntity
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
            entity.animation((AnimationType.IDLE))
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
    private val startPosition = vec2()
    private val targetPosition = vec2()

    override fun execute(): Status {
        if (status != Status.RUNNING) {
            if (startPosition.isZero) {
                startPosition.set(entity.position)
            }
            entity.animation(AnimationType.RUN)
            targetPosition.set(startPosition)
            targetPosition.x += MathUtils.random(-3f, 3f)
            log.debug { "Target X = ${targetPosition.x}" }
            targetPosition.y += MathUtils.random(-3f, 3f)
            log.debug { "Target Y = ${targetPosition.y}" }
            entity.moveTo(targetPosition)
            entity.moveSlow(true)
            return Status.RUNNING
        }

        if (entity.inRange(1.5f, targetPosition)) {
            entity.stopMovement()
            return Status.SUCCEEDED
        }

        return Status.RUNNING
    }

    override fun end() {
        entity.moveSlow(false)
    }

    companion object {
        private val log = logger<WanderTask>()
    }
}

class AttackTask: Action() {

    override fun execute(): Status {
        if (status != Status.RUNNING) {
            entity.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL, true)
            entity.startAttack()
            return Status.RUNNING
        }

        if (entity.isAnimationDone) {
            entity.animation(AnimationType.IDLE)
            entity.stopMovement()
            return Status.SUCCEEDED
        }

        return Status.RUNNING
    }

    companion object {
        private val log = logger<WanderTask>()
    }
}

