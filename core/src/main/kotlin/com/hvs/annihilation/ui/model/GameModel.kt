package com.hvs.annihilation.ui.model

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.hvs.annihilation.ecs.animation.AnimationComponent
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.event.EntityAggroEvent
import com.hvs.annihilation.event.EntityReviveEvent
import com.hvs.annihilation.event.EntityTakeDamageEvent
import com.hvs.annihilation.ui.PropertyChangeSource
import com.hvs.annihilation.ui.propertyNotify

class GameModel(
    world: World,
    uiStage: Stage,
) : PropertyChangeSource(), EventListener {

    private val playerCmps: ComponentMapper<PlayerComponent> = world.mapper()
    private val lifeCmps: ComponentMapper<LifeComponent> = world.mapper()
    private val animationCmps: ComponentMapper<AnimationComponent> = world.mapper()

//    var playerLife by propertyNotify(1f)

    var playerLife = 1f
        private set(value) {
            notify(::playerLife, value)
            field = value
        }

    private var lastEnemy = Entity(-1)
    var enemyType by propertyNotify("")

    var enemyLife by propertyNotify(1f)

    var lootText by propertyNotify("")

    init {
        uiStage.addListener(this)
    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is EntityTakeDamageEvent -> {
                val isPlayer = event.entity in playerCmps
                val lifeCmp = lifeCmps[event.entity]
                if (isPlayer) {
                    playerLife = lifeCmp.life / lifeCmp.maximumLife
                }
            }

            is EntityReviveEvent -> {
                val isPlayer = event.entity in playerCmps
                val lifeCmp = lifeCmps[event.entity]
                if (isPlayer) {
                    playerLife = lifeCmp.life / lifeCmp.maximumLife
                }
            }

            is EntityAggroEvent -> {
                val source = event.aiEntity
                val sourceType = animationCmps.getOrNull(source)?.model?.atlasKey
                val target = event.target
                val isTargetPlayer = target in playerCmps
                if (isTargetPlayer && sourceType != null) {
                    updateEnemy(source)
                }
            }
            else -> return false
        }
        return true
    }

    private fun updateEnemy(enemy: Entity) {
        val lifeCmp = lifeCmps[enemy]
        enemyLife = lifeCmp.life / lifeCmp.maximumLife
        if (lastEnemy != enemy) {
            // update enemy type
            lastEnemy = enemy
            animationCmps.getOrNull(enemy)?.model?.atlasKey?.let { enemyType ->
                this.enemyType = enemyType
            }
        }
    }
}
