package com.hvs.annihilation.ecs.save


import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.IntMap
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.hvs.annihilation.assets.MapAssets
import com.hvs.annihilation.ecs.attack.AttackComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.ecs.tiled.TiledComponent
import ktx.collections.set
import ktx.preferences.set
import ktx.preferences.flush
import java.util.EnumMap

class SaveState {
    lateinit var currentMap: MapAssets
    lateinit var mapEntities: IntMap<IntArray>
    var life = 0f
    var maxLife = 0f
}

@AllOf([PlayerComponent::class, SaveComponent::class])
class SaveSystem(
    private val preferences: Preferences,
    private val map: MapAssets,
    private val lifeComponents: ComponentMapper<LifeComponent>,
    private val saveComponents: ComponentMapper<SaveComponent>
) : IteratingSystem() {

    private val saveState = SaveState().apply { mapEntities = IntMap() }
    val mapEntityCache = EnumMap<MapAssets, IntArray>(MapAssets::class.java)

    override fun onTickEntity(entity: Entity) {
        preferences.flush {
            this[SAVE_STATE_KEY] = saveState.apply {
                // map information
                currentMap = map
                mapEntities.clear()
                storeMapEntities(currentMap)
                mapEntityCache.forEach { (map, entities) ->
                    mapEntities[map.ordinal] = IntArray(entities)
                }
                this.life = lifeComponents[entity].life
                this.maxLife = lifeComponents[entity].maximumLife
            }
        }
        // remove component since the save is done

    }

    private fun storeMapEntities(map: MapAssets) {
        mapEntityCache.computeIfAbsent(map) { IntArray(32) }.apply {
            this.clear()
            world.family().forEach { entity ->
                val tmxMapCmp =
                if (tmxMapCmp != null) {
                    this.add(tmxMapCmp.id)
                }
            }
        }
    }

    companion object {
        const val SAVE_STATE_KEY = "saveState"
    }
}

