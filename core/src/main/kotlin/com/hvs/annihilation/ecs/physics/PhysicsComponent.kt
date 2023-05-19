package com.hvs.annihilation.ecs.physics

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Polyline
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.hvs.annihilation.ecs.collision.CollisionSpawnSystem.Companion.SPAWN_AREA_SIZE
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import com.hvs.annihilation.Annihilation.Companion.UNIT_SCALE
import com.hvs.annihilation.ecs.spawn.EntitySpawnSystem
import com.hvs.annihilation.ecs.spawn.SpawnConfiguration
import ktx.app.gdxError
import ktx.box2d.FixtureDefinition
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.chain
import ktx.box2d.circle
import ktx.box2d.loop
import ktx.math.vec2

class PhysicsComponent {
    val previousPosition = vec2()
    val impulse = vec2()
    val offset = vec2()
    val size = vec2()
    lateinit var body: Body

    companion object {

        fun EntityCreateCfg.physicsCompFromShape2D(
            world: World,
            x: Int,
            y: Int,
            shape: Shape2D,
            isPortal: Boolean = false,
            init: FixtureDefinition.() -> Unit = { }
        ): PhysicsComponent {
            when (shape) {
                is Rectangle -> {
                    val bodyX = x + shape.x * UNIT_SCALE
                    val bodyY = y + shape.y * UNIT_SCALE
                    val bodyWidth = shape.width * UNIT_SCALE
                    val bodyHeight = shape.height * UNIT_SCALE

                    return add {
                        body = world.body(BodyType.StaticBody) {
                            position.set(bodyX, bodyY)
                            fixedRotation = true
                            allowSleep = false
                            loop(
                                vec2(0f, 0f),
                                vec2(bodyWidth, 0f),
                                vec2(bodyWidth, bodyHeight),
                                vec2(0f, bodyHeight)
                            ) {
                                this.isSensor = isPortal
                            }
                            circle(SPAWN_AREA_SIZE + 2f) { isSensor = true }
                        }
                    }
                }
                is Polyline -> {
                    return add {
                        body = world.body(BodyType.StaticBody) {
                            position.set(shape.x * UNIT_SCALE, shape.y * UNIT_SCALE)
                            // transformed vertices also adds the position to each
                            // vertex. Therefore, we need to set position first to ZERO
                            // and then restore it afterwards
                            shape.setPosition(0f, 0f)
                            shape.setScale(UNIT_SCALE, UNIT_SCALE)
                            chain(shape.transformedVertices).init()
                            // restore position
                            shape.setPosition(shape.x, shape.y)
                        }
                    }
                }
                is Polygon -> {
                    return add {
                        body = world.body(BodyType.StaticBody) {
                            position.set(shape.x * UNIT_SCALE, shape.y * UNIT_SCALE)
                            // transformed vertices also adds the position to each
                            // vertex. Therefore, we need to set position first to ZERO
                            // and then restore it afterwards
                            shape.setPosition(0f, 0f)
                            shape.setScale(UNIT_SCALE, UNIT_SCALE)
                            loop(shape.transformedVertices).init()
                            // restore position
                            shape.setPosition(shape.x, shape.y)
                        }
                    }
                }
                else -> gdxError("Shape $shape is not supported")
            }
        }

        fun PhysicsComponent.bodyFromImageAndConfig(
            physicsWorld: World,
            image: Image,
            entityConfig: SpawnConfiguration,
            entitySize: Vector2
        ): Body {
            val positionX = image.x
            val positionY = image.y
            val w = image.width
            val h = image.height

            val physicsComponent = this

            return physicsWorld.body(entityConfig.bodyType) {
                //box2d starting position is dead center instead of bottom left. Therefor this calculation needs to be made
                position.set(
                    positionX + w * 0.5f,
                    positionY + h * 0.5f
                )
                fixedRotation = true
                allowSleep = false

                val width = w * entityConfig.physicsScaling.x
                val height = h * entityConfig.physicsScaling.y
                physicsComponent.offset.set(entityConfig.physicsOffset)
                physicsComponent.size.set(width, height)

                // hit box
                box(width, height, entityConfig.physicsOffset) {
                    isSensor = entityConfig.bodyType != BodyDef.BodyType.StaticBody
                    userData = EntitySpawnSystem.HIT_BOX_SENSOR
                }

                // add a second fixture for ground contact for jumping
                if (entityConfig.bodyType != BodyType.DynamicBody) {
                    box(
                        width, height, entityConfig.physicsOffset
                    ) {
                        userData = EntitySpawnSystem.GROUND_COLLISION_BOX
                        isSensor = false
                    }
                }


                if (entityConfig.bodyType != BodyType.StaticBody) {
                    //collision box
                    val collisionHeight = height * 0.4f
                    val collisionOffset = vec2().apply { set(entityConfig.physicsOffset) }
                    collisionOffset.y -= height * 0.5f - collisionHeight * 0.5f
                    box(width, collisionHeight, collisionOffset)

                    // ground sensor on dynamic entity foot
                    box(
                        entitySize.x * 0.5f,
                        0.2f,
                        vec2().apply {
                            set(
                                entityConfig.physicsOffset.x,
                                -1f
                            )
                        }
                    ) {
                        userData = EntitySpawnSystem.GROUND_TOUCH_SENSOR
                        isSensor = false
                    }
                }
//                previousPosition.set(body.position)   Doesn't seem necessary?????
            }
        }

        class PhysicsComponentListener : ComponentListener<PhysicsComponent> {
            override fun onComponentAdded(entity: Entity, component: PhysicsComponent) {
                component.body.userData = entity
            }

            override fun onComponentRemoved(entity: Entity, component: PhysicsComponent) {
                val body = component.body
                body.world.destroyBody(body)
                body.userData = null
            }
        }
    }
}
