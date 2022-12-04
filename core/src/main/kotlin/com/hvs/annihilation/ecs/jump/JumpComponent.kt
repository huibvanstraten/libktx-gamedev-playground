package com.hvs.annihilation.ecs.jump

enum class JumpOrder {
    JUMP, NONE
}

class JumpComponent(
    var order: JumpOrder = JumpOrder.NONE,
    var jumpTime: Float = 0f,
    var maxJumpTime: Float = 1.1f
)
