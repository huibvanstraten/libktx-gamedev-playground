package com.hvs.annihilation.ecs.life

data class LifeComponent(
    var life: Float = 15f,
    var maximumLife: Float = 30f,
    var regeneration: Float = 0f,
    var takeDamage: Float = 0f
) {
    val isDead
    get() = life <= 0f
}


