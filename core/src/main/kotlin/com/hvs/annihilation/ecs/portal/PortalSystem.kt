package com.hvs.annihilation.ecs.portal

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.Annihilation
import com.hvs.annihilation.Annihilation.Companion.UNIT_SCALE
import com.hvs.annihilation.ecs.image.ImageComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent
import com.hvs.annihilation.ecs.physics.PhysicsComponent.Companion.bodyFromImageAndConfig
import com.hvs.annihilation.ecs.physics.PhysicsComponent.Companion.physicsCompFromShape2D
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.ecs.spawn.EntitySpawnSystem.Companion.PLAYER_CONFIGURATION
import com.hvs.annihilation.enums.AnimationModel
import com.hvs.annihilation.enums.AnimationType
import com.hvs.annihilation.event.MapChangeEvent
import com.hvs.annihilation.event.fire
import ktx.app.gdxError
import ktx.assets.disposeSafely
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.id
import ktx.tiled.layer
import ktx.tiled.property
import ktx.tiled.shape
import ktx.tiled.x
import ktx.tiled.y

@AllOf([PortalComponent::class])
class PortalSystem(
    private val physicsWorld: World,
    private val gameStage: Stage,
    private val atlas: TextureAtlas,
    private val portalCpms: ComponentMapper<PortalComponent>,
    private val physicsCpms: ComponentMapper<PhysicsComponent>,
    private val imageCpms: ComponentMapper<ImageComponent>,
): IteratingSystem(), EventListener {

    private val cachedSizes = mutableMapOf<AnimationModel, Vector2>()
    private var currentMap: TiledMap? = null


    override fun onTickEntity(entity: Entity) {
        val (_, toMap, toPortal, entitiesToMove) = portalCpms[entity]

        if(entitiesToMove.isNotEmpty()) {
            entitiesToMove.clear()
            setMap("map/$toMap.tmx", toPortal)
        }
    }

    fun setMap(path: String, targetPortalId: Int = -1) {
        currentMap.disposeSafely()
        // remove all entities from world
        world.family(noneOf = arrayOf(PlayerComponent::class)).forEach { world.remove(it) }

        val newMap = TmxMapLoader().load(path)
        gameStage.fire(MapChangeEvent(newMap))
        currentMap = newMap

        if(targetPortalId >= 0) {
            world.family(allOf = arrayOf(PlayerComponent::class)).forEach { playerEntity ->
                val targetPortal = targetPortalById(newMap, targetPortalId)
                val image = imageCpms[playerEntity].image

                image.setPosition(
                    targetPortal.x * UNIT_SCALE - image.width * 0.5f * UNIT_SCALE,
                    targetPortal.y * UNIT_SCALE - targetPortal.height * 0.5f * UNIT_SCALE
                )

                val entitySize = getEntitySizeByIdleImage(PLAYER_CONFIGURATION.model)

                configureEntity(playerEntity) {
                    physicsCpms.remove(it)
                    physicsCpms.add(it) {
                        body = bodyFromImageAndConfig(physicsWorld, image, PLAYER_CONFIGURATION, entitySize)
                    }
                }
            }
        }
    }

    private fun getEntitySizeByIdleImage(model: AnimationModel) = cachedSizes.getOrPut(model) {
        val regions = atlas.findRegions("${model.atlasKey}/${AnimationType.IDLE.atlasKey}")
        if (regions.isEmpty) {
            gdxError("There are no regions for the idle animation of model $model")
        }

        vec2(
            x = regions.first().originalWidth * Annihilation.UNIT_SCALE,
            y = regions.first().originalHeight * Annihilation.UNIT_SCALE
        )
    }

    private fun targetPortalById(map: TiledMap, targetPortal: Int): MapObject =
        map.layer("portals").objects.first { it.id == targetPortal}
            ?: gdxError("There is no portal with portalId $targetPortal")

    override fun handle(event: Event): Boolean {
        if (event is MapChangeEvent) {
            val portalLayer = event.map.layers.get("portals")

            portalLayer.objects.forEach {mapObj ->
                val toMap = mapObj.property("toMap", "")
                val toPortal = mapObj.property("toPortal", -1)

                if (toMap.isBlank()) return@forEach
                else if (toPortal == -1) gdxError("No Portal has been set")

                world.entity {
                    add<PortalComponent> {
                        this.id = mapObj.id
                        this.toMap = toMap
                        this.toPortal = toPortal
                    }
                    physicsCompFromShape2D(physicsWorld, 0, 0, mapObj.shape, true)
                }
            }
            return true
        }
        else return false
    }
}
