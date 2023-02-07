package com.hvs.annihilation.ecs.ai

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import com.hvs.annihilation.state.AiEntity

abstract class Condition: LeafTask<AiEntity>() {
    val aiEntity: AiEntity
        get() = `object`

    abstract fun condition(): Boolean

    override fun execute(): Status =
        when (condition()) {
            true -> Status.SUCCEEDED
            else -> Status.FAILED
        }

    override fun copyTo(task: Task<AiEntity>): Task<AiEntity> = task
}

class CanAttack(
    @JvmField
    @TaskAttribute(required = true)
    var range: Float = 0f
): Condition() {
    override fun condition(): Boolean = aiEntity.canAttack(range)
}

class IsEnemyNearby: Condition() {
    override fun condition(): Boolean = aiEntity.findNearbyEnemy()
}

