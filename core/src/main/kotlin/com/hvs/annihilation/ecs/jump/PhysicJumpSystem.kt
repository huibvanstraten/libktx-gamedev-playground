package com.hvs.annihilation.ecs.jump

import com.badlogic.gdx.math.Interpolation
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import kotlin.math.min

@AllOf([JumpComponent::class, PhysicsComponent::class])
class PhysicJumpSystem(
    private val jumpCmps: ComponentMapper<JumpComponent>,
    private val physicCmps: ComponentMapper<PhysicsComponent>
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val physicsCmp = physicCmps[entity]
        val jumpCmp = jumpCmps[entity]
        jumpCmp.run {
            // interpolate jump speed via jump time (range 0..1) over 1 second
            jumpTime = min(1f, jumpTime + deltaTime)

            // set physic impulse y value directly which will be applied one time before world step is called
            // in the PhysicSystem
            physicsCmp.impulse.y = when (order) {
                JumpOrder.JUMP -> {
                    // maximum jump speed is 3.5f
                    physicsCmp.body.mass * (jumpAlpha.apply(0f, 3.5f, jumpTime) - physicsCmp.body.linearVelocity.y)
                }
                else -> {
                    // stop jump -> set impulse to zero so it won't be applied anymore
                    jumpTime = 0f
                    if (physicsCmp.body.linearVelocity.y > 0f) {
                        // if the player should not jump but still gets an upwards force then apply an impulse
                        // to stop the upwards movement.
                        // This is used to make the player stick to the ground when moving slopes upwards.
                        physicsCmp.body.mass * -physicsCmp.body.linearVelocity.y
                    } else {
                        0f
                    }
                }
            }
        }
    }

    companion object {
        val jumpAlpha: Interpolation = Interpolation.pow2Out
    }
}
