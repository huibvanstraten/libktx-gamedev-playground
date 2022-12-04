package com.hvs.annihilation.ecs.ai

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import com.badlogic.gdx.ai.utils.random.FloatDistribution
import com.badlogic.gdx.math.MathUtils
import com.hvs.annihilation.enums.AnimationType
import com.hvs.annihilation.state.AiEntity
import ktx.log.logger
import ktx.math.vec2

abstract class Condition : LeafTask<AiEntity>() {
    val entity: AiEntity
        get() = `object`

    abstract fun condition(): Boolean

    override fun execute(): Status =
        when  {
            condition() -> Status.SUCCEEDED
            else -> Status.FAILED
        }

    override fun copyTo(task: Task<AiEntity>): Task<AiEntity> = task
}

class CanAttack: Condition() {
    override fun condition(): Boolean = entity.canAttack()
}

class IsEnemyNearby: Condition() {
    override fun condition(): Boolean = entity.hasEnemyNearby()
}

