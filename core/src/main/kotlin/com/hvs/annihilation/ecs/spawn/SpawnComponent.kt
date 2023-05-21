package com.hvs.annihilation.ecs.spawn

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

data class SpawnComponent(
    var type: String = "",
    var location: Vector2 = vec2(),
    var mapId: Int = -1
)
