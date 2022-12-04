package com.hvs.annihilation.ecs.collision

import com.badlogic.gdx.utils.ObjectSet
import com.github.quillraven.fleks.Entity

// identifies entities that should be part of the collision handling logic of our game
class CollisionComponent(
    val entities: ObjectSet<Entity> = ObjectSet(4),
    var numGroundContacts: Int = 0
)
