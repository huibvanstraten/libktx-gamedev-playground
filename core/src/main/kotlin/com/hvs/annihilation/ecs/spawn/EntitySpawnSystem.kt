package com.hvs.annihilation.ecs.spawn

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.utils.Scaling
import com.hvs.annihilation.ecs.move.MoveComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent.Companion.bodyFromImageAndConfig
import com.hvs.annihilation.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.Annihilation.Companion.UNIT_SCALE
import com.hvs.annihilation.ecs.ai.AiComponent
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.attack.AttackComponent
import com.hvs.annihilation.ecs.collision.CollisionComponent
import com.hvs.annihilation.ecs.image.FlipImage
import com.hvs.annihilation.ecs.image.ImageComponent
import com.hvs.annihilation.ecs.jump.JumpComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.loot.LootComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.ecs.spawn.SpawnConfiguration.Companion.DEFAULT_ATTACK_DAMAGE
import com.hvs.annihilation.ecs.spawn.SpawnConfiguration.Companion.DEFAULT_LIFE
import com.hvs.annihilation.ecs.spawn.SpawnConfiguration.Companion.DEFAULT_SPEED
import com.hvs.annihilation.ecs.state.StateComponent
import com.hvs.annihilation.enums.AnimationModel
import com.hvs.annihilation.enums.AnimationType
import ktx.app.gdxError
import ktx.box2d.circle
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.x
import ktx.tiled.y
import kotlin.math.roundToInt

@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    private val physicsWorld: World,
    private val atlas: TextureAtlas,
    private val spawnComponents: ComponentMapper<SpawnComponent>
) : EventListener, IteratingSystem() {
    private val cachedSpawnConfigurations = mutableMapOf<String, SpawnConfiguration>()
    private val cachedSizes = mutableMapOf<AnimationModel, Vector2>()
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))


    override fun onTickEntity(entity: Entity) {
        with(spawnComponents[entity]) {
            val entityConfig = setSpawnConfiguration(type)
            val entitySize = getEntitySizeByIdleImage(entityConfig.model)

            world.entity {
                val imageComp = add<ImageComponent> {
                    //flipping image of player
                    image = FlipImage().apply {
                        setPosition(location.x, location.y)
                        setScaling(Scaling.fill)
                        setSize(entitySize.x, entitySize.y)
                    }
                }

                add<AnimationComponent> {
                    nextAnimation(entityConfig.model, AnimationType.IDLE)
                }

                val physicsComp = add<PhysicsComponent> {
                    body = bodyFromImageAndConfig(physicsWorld, imageComp.image, entityConfig, entitySize)
                }

                if (entityConfig.speedScaling != 0f) {
                    add<MoveComponent> {
                        speed = DEFAULT_SPEED * entityConfig.speedScaling
                    }
                }

                if (entityConfig.canAttack) {
                    add<AttackComponent> {
                        maxDelay = entityConfig.attackDelay
                        damage = (DEFAULT_ATTACK_DAMAGE * entityConfig.attackScaling).roundToInt()
                        extraRange = entityConfig.extraAttackRange
                    }
                }

                if (entityConfig.lifeScaling > 0f) {
                    add<LifeComponent> {
                        maximumLife = DEFAULT_LIFE * entityConfig.lifeScaling
                        life = maximumLife
                    }
                }

                if (entityConfig.bodyType != BodyDef.BodyType.StaticBody) {
                    //such entities will create/remove collision objects
                    add<CollisionComponent>()
                }

                if (type == "Player") {
                    add<PlayerComponent>()
                    add<StateComponent>()
                    add<JumpComponent>()
                }

                if (entityConfig.lootable) {
                    add<LootComponent>()
                }

                if (entityConfig.aiTreePath.isNotBlank()) {
                    add<AiComponent> {
                        treePath =  entityConfig.aiTreePath
                    }
                    physicsComp.body.circle(4f) {
                        isSensor = true
                        userData = AI_SENSOR
                    }
                }
            }
        }
        world.remove(entity)
    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is MapChangeEvent -> {
                val entityLayer = event.map.layer("entities")
                entityLayer.objects.forEach { mapObject ->
                    val name = mapObject.name
                        ?: gdxError("MapObject $mapObject does not have a name")
                    if(name == "Player" && playerEntities.numEntities > 0) {
                        return@forEach
                    }

                    world.entity {
                        add<SpawnComponent> {
                            this.type = name
                            this.location.set(mapObject.x * UNIT_SCALE, mapObject.y * UNIT_SCALE)
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    private fun setSpawnConfiguration(
        type: String
    ): SpawnConfiguration = cachedSpawnConfigurations.getOrPut(type) {
        when (type) {
            "Player" -> PLAYER_CONFIGURATION
            "Slime" -> SLIME_CONFIGURATION
            "Chest" -> CHEST_CONFIGURATION
            else -> gdxError("Type $type has no spawnConfiguration set up")
        }
    }

     private fun getEntitySizeByIdleImage(model: AnimationModel) = cachedSizes.getOrPut(model) {
        val regions = atlas.findRegions("${model.atlasKey}/${AnimationType.IDLE.atlasKey}")
        if (regions.isEmpty) {
            gdxError("There are no regions for the idle animation of model $model")
        }

        vec2(
            x = regions.first().originalWidth * UNIT_SCALE,
            y = regions.first().originalHeight * UNIT_SCALE
        )
    }

    companion object {
        const val AI_SENSOR = "aiSensor"
        const val HIT_BOX_SENSOR = "hitBox"
        const val GROUND_TOUCH_SENSOR = "groundTouchSensor"
        const val GROUND_COLLISION_BOX = "collisionBox"

        val PLAYER_CONFIGURATION = SpawnConfiguration(
            model = AnimationModel.PLAYER,
            physicsScaling = vec2(0.3f, 0.3f),
            physicsOffset = vec2(0f, -10f * UNIT_SCALE),
            extraAttackRange = 0.6f,
            attackScaling = 1.25f
        )
        val SLIME_CONFIGURATION = SpawnConfiguration(
            model = AnimationModel.SLIME,
            physicsScaling = vec2(0.3f, 0.3f),
            physicsOffset = vec2(0f, -2f * UNIT_SCALE),
            lifeScaling = 0.75f,
            extraAttackRange = 2f,
            aiTreePath = "ai/slime.tree"
        )
        val CHEST_CONFIGURATION = SpawnConfiguration(
            model = AnimationModel.CHEST,
            speedScaling = 0f,
            bodyType = BodyDef.BodyType.StaticBody,
            canAttack = false,
            lifeScaling = 0f,
            lootable = true
        )
    }
}
