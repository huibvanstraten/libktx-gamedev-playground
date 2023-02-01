package com.hvs.annihilation.ecs.physics

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.World
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.ecs.ai.AiComponent
import com.hvs.annihilation.ecs.collision.CollisionComponent
import com.hvs.annihilation.ecs.image.ImageComponent
import com.hvs.annihilation.ecs.spawn.EntitySpawnSystem.Companion.ACTION_SENSOR
import com.hvs.annihilation.ecs.spawn.EntitySpawnSystem.Companion.AI_SENSOR
import com.hvs.annihilation.ecs.tiled.TiledComponent
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

@AllOf([PhysicsComponent::class, ImageComponent::class])
class PhysicsSystem(
    private val physicWorld: World,
    private val imageCmps: ComponentMapper<ImageComponent>,
    private val physicCmps: ComponentMapper<PhysicsComponent>,
    private val tiledCmps: ComponentMapper<TiledComponent>,
    private val collisionCmps: ComponentMapper<CollisionComponent>,
    private val aiCmps: ComponentMapper<AiComponent>
) : IteratingSystem(interval = Fixed(1 / 60f)), ContactListener {
    init {
        physicWorld.setContactListener(this)
    }

    override fun onUpdate() {
        if (physicWorld.autoClearForces) {
            LOG.error { "AutoClearForces must be set to false to guarantee a correct physic step behavior." }
            physicWorld.autoClearForces = false
        }
        super.onUpdate()
        physicWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(deltaTime, 6, 2)
    }

    // store position before world update for smooth interpolated rendering
    override fun onTickEntity(entity: Entity) {
        val physicCmp = physicCmps[entity]
        physicCmp.previousPosition.set(physicCmp.body.position)

        if (!physicCmp.impulse.isZero) {
            physicCmp.body.applyLinearImpulse(physicCmp.impulse, physicCmp.body.worldCenter, true)
            physicCmp.impulse.setZero()
        }
    }

    // interpolate between position before world step and real position after world step for smooth rendering
    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val imageCmp = imageCmps[entity]
        val physicCmp = physicCmps[entity]

        imageCmp.image.run {
            val (prevX, prevY) = physicCmp.previousPosition
            val (bodyX, bodyY) = physicCmp.body.position

            setPosition(
                MathUtils.lerp(prevX, bodyX, alpha) - width * 0.5f,
                MathUtils.lerp(prevY, bodyY, alpha) - height * 0.5f
            )
        }
    }

    private val Fixture.entity: Entity
        get() = this.body.userData as Entity

    private val Contact.isSensorA: Boolean
        get() = this.fixtureA.isSensor

    private val Contact.isSensorB: Boolean
        get() = this.fixtureB.isSensor

    override fun beginContact(contact: Contact) {
        val entityA = contact.fixtureA.entity
        val entityB = contact.fixtureB.entity

        when {
            // keep track of nearby entities for tiled collision entities.
            // when there are no nearby entities then the collision object will be removed
            entityA in tiledCmps && entityB in collisionCmps && contact.isSensorA && !contact.isSensorB -> {
                tiledCmps[entityA].nearbyEntities += entityB }

            entityB in tiledCmps && entityA in collisionCmps && contact.isSensorB && !contact.isSensorA -> {
                tiledCmps[entityB].nearbyEntities += entityA
            }

            entityB in tiledCmps && entityA in collisionCmps && !contact.isSensorA && !contact.isSensorB && entityA !in aiCmps -> {
                    collisionCmps[entityA].numGroundContacts ++
                        println("FIRST: ADDING NUMGROUNDCONTACTS TO PLAYER? ${collisionCmps[entityA].numGroundContacts}")
                    }

            entityA in tiledCmps && entityB in collisionCmps && !contact.isSensorA && !contact.isSensorB && entityB !in aiCmps -> {
                    collisionCmps[entityB].numGroundContacts ++
                        println("SECOND: ADDING NUMGROUNDCONTACTS TO PLAYER? ${collisionCmps[entityB].numGroundContacts}")
                    }

            // AI entities keep track of their nearby entities to have this information available
            // for their behavior. E.g. a slime entity will attack a player if he comes close
            entityA in aiCmps && entityB in collisionCmps && contact.fixtureA.userData == ACTION_SENSOR -> {
                aiCmps[entityA].nearByEntities += entityB
            }
            entityB in aiCmps && entityA in collisionCmps && contact.fixtureB.userData == ACTION_SENSOR -> {
                aiCmps[entityB].nearByEntities += entityA
            }
        }
    }

    override fun endContact(contact: Contact) {
        val entityA = contact.fixtureA.entity
        val entityB = contact.fixtureB.entity

        // same as beginContact but we remove entities instead
        // Note: we cannot add the collision component check in endContact because when an entity
        // gets removed then it does not have any components anymore, but it might be part of the
        // nearbyEntities set.
        // -> simply remove entities all the time because the set will take care of correct removal calls
        when {
            entityA in tiledCmps && contact.isSensorA && !contact.isSensorB -> {
                tiledCmps[entityA].nearbyEntities -= entityB }

            entityB in tiledCmps && contact.isSensorB && !contact.isSensorA -> {
                tiledCmps[entityB].nearbyEntities -= entityA
            }

            entityB in tiledCmps && entityA in collisionCmps && !contact.isSensorA && !contact.isSensorB && entityA !in aiCmps -> {
                collisionCmps[entityA].numGroundContacts --
                println("FIRST: REmoving NUMGROUNDCONTACTS TO PLAYER? ${collisionCmps[entityA].numGroundContacts}")
            }

            entityA in tiledCmps && entityB in collisionCmps && !contact.isSensorA && !contact.isSensorB && entityB !in aiCmps -> {
                collisionCmps[entityB].numGroundContacts --
                println("SECOND: removing NUMGROUNDCONTACTS TO PLAYER? ${collisionCmps[entityB].numGroundContacts}")
            }

            entityA in aiCmps && contact.fixtureA.userData == AI_SENSOR -> {
                aiCmps[entityA].nearByEntities - entityB
            }
            entityB in aiCmps && contact.fixtureB.userData == AI_SENSOR -> {
                aiCmps[entityB].nearByEntities -= entityA
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        // only allow collision between Dynamic and Static bodies
        contact.isEnabled =
            (contact.fixtureA.body.type == BodyDef.BodyType.StaticBody && contact.fixtureB.body.type == BodyDef.BodyType.DynamicBody)
            ||
            (contact.fixtureB.body.type == BodyDef.BodyType.StaticBody && contact.fixtureA.body.type == BodyDef.BodyType.DynamicBody)
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) = Unit

    companion object {
        private val LOG = logger<PhysicsSystem>()
    }
}
