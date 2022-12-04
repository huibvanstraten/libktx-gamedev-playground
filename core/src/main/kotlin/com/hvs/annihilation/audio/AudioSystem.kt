package com.hvs.annihilation.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.quillraven.fleks.IntervalSystem
import com.hvs.annihilation.event.EntityAttackEvent
import com.hvs.annihilation.event.EntityDeathEvent
import com.hvs.annihilation.event.EntityLootEvent
import com.hvs.annihilation.event.MapChangeEvent
import com.hvs.annihilation.event.MenuChoiceEvent
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.tiled.propertyOrNull

class AudioSystem: EventListener, IntervalSystem() {

    private val musicCache = mutableMapOf<String, Music>()
    private val soundCache = mutableMapOf<String, Sound>()
    private val soundRequests = mutableMapOf<String, Sound>()

    override fun onTick() {
        soundRequests.values.forEach { it.play(1f) }
        soundRequests.clear()
    }

    override fun handle(event: Event): Boolean {
        when(event) {
            is MapChangeEvent -> {
                event.map.propertyOrNull<String>("music")?.let { path ->
                    log.debug { "Changing music to $path" }
                    val music = musicCache.getOrPut(path) {
                        Gdx.audio.newMusic(Gdx.files.internal(path)).apply {
                            isLooping = true
                        }
                    }
                    music.play()
                }
                return true
            }
            is EntityAttackEvent -> queueSound("audio/${event.model.atlasKey}_attack.wav")
            is EntityDeathEvent -> queueSound("audio/${event.model.atlasKey}_death.wav")
            is EntityLootEvent -> queueSound("audio/${event.model.atlasKey}_open.wav")
            is MenuChoiceEvent -> queueSound("audio/menu_select.mp3")
        }
        return false
    }

    private fun queueSound(soundPath: String) {
        log.debug { "Queueing sound $soundPath" }
        if (soundPath in soundRequests) {
            return
        }

        val sound = soundCache.getOrPut(soundPath) {
            Gdx.audio.newSound(Gdx.files.internal(soundPath))
        }
        soundRequests[soundPath] = sound
    }

    override fun onDispose() {
        musicCache.values.forEach { it.disposeSafely() }
        soundCache.values.forEach { it.disposeSafely() }
    }

    companion object {
        private val log = logger<AudioSystem>()
    }
}
