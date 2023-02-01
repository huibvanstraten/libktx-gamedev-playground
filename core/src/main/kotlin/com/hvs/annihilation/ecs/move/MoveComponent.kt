package com.hvs.annihilation.ecs.move

data class MoveComponent(
    var speed: Float = 0f,
    var x: Float = 0f,
    var y: Float = 0f,
    var root: Boolean = false,
    var slow: Boolean = false
)

