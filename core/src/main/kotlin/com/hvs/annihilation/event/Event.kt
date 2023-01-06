package com.hvs.annihilation.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.Entity
import com.hvs.annihilation.enums.AnimationModel

fun Stage.fire(event: Event) {
    this.root.fire(event)
}

data class CollisionDespawnEvent(val cell: TiledMapTileLayer.Cell): Event()

data class MapChangeEvent(val map: TiledMap): Event()

data class EntityAttackEvent(val model: AnimationModel): Event()

data class EntityDeathEvent(val model: AnimationModel): Event()

data class EntityLootEvent(val model: AnimationModel): Event()

class MenuChoiceEvent: Event()

class GamePauseEvent: Event()

class GameResumeEvent: Event()

data class EntityTakeDamageEvent(val entity: Entity, val damage: Float) : Event()

data class EntityReviveEvent(val entity: Entity) : Event()
