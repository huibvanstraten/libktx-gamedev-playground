package com.hvs.annihilation.ecs.move

import com.badlogic.gdx.math.Interpolation
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.image.ImageComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.player.PlayerComponent


// https://www.iforce2d.net/b2dtut/
@AllOf([MoveComponent::class, PhysicsComponent::class])
class MoveSystem(
    private val moveComponents: ComponentMapper<MoveComponent>,
    private val physicsComponents: ComponentMapper<PhysicsComponent>,
    private val imageComponents: ComponentMapper<ImageComponent>,
    private val playerComponents:ComponentMapper<PlayerComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val moveComp = moveComponents[entity]
        val physicsComp = physicsComponents[entity]
        val bodyMass = physicsComp.body.mass
        val velocityX = physicsComp.body.linearVelocity.x

        if (moveComp.x == 0f || moveComp.root) {  //if we are not pushing a moving button, we stop the body
            if (!physicsComp.body.linearVelocity.isZero) {
                if (!playerComponents.contains(entity)) {
                    moveAlpha.apply(0f, 0.2f, 0.5f)
                }
                // entity is moving -> stop it
                physicsComp.impulse.x = bodyMass * (0f - velocityX)
            }
            return
        }

        val slowFactor = if (moveComp.slow) 0.2f else 1f
        physicsComp.impulse.x = bodyMass * (moveComp.speed * slowFactor * moveComp.x - velocityX)

        imageComponents.getOrNull(entity)?.let { imageComponent ->
            if (moveComp.x != 0f) imageComponent.image.flipX = moveComp.x < 0
        }
    }

    companion object {
        private val moveAlpha = Interpolation.circleOut
    }
}
