package com.hvs.annihilation.ecs.spawn

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.hvs.annihilation.enums.AnimationModel
import ktx.math.vec2

data class SpawnConfiguration(
    var mapId: Int = -1,
    val model: AnimationModel,
    val speedScaling: Float = 1f,
    val canAttack: Boolean = true,
    val attackScaling: Float = 1f,
    val attackDelay: Float = 0.2f,
    val extraAttackRange: Float = 0f,
    val lifeScaling: Float = 1f,
    val lootable: Boolean = false,
    val aiTreePath: String = "",
    val physicsScaling: Vector2 = vec2(1f, 1f),
    val physicsOffset: Vector2 = vec2(0f, 0f),
    val bodyType: BodyType = BodyType.DynamicBody
) {

    companion object {
        const val DEFAULT_SPEED = 3f
        const val DEFAULT_ATTACK_DAMAGE = 5
        const val DEFAULT_LIFE = 15
    }
}
