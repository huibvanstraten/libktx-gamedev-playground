package com.hvs.annihilation.ecs.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.quillraven.fleks.IntervalSystem
import com.hvs.annihilation.event.EntityAttackEvent
import com.hvs.annihilation.event.EntityDeathEvent
import com.hvs.annihilation.event.EntityLootEvent
import com.hvs.annihilation.event.GamePauseEvent
import com.hvs.annihilation.event.GameResumeEvent
import com.hvs.annihilation.event.GameSelectEvent
import com.hvs.annihilation.event.MapChangeEvent
import com.hvs.annihilation.event.MenuChoiceEvent
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.tiled.propertyOrNull

class AudioSystem: EventListener, IntervalSystem() {

    private val musicCache = mutableMapOf<String, Music>()
    private val soundCache = mutableMapOf<String, Sound>()
    private val soundRequests = mutableMapOf<String, Sound>()

    private var music: Music? = null

    override fun onTick() {
        soundRequests.values.forEach { it.play(1f) }
        soundRequests.clear()
    }

    override fun handle(event: Event): Boolean {
        when(event) {
            is MapChangeEvent -> return mapChangeEvent(event)
            is EntityAttackEvent -> queueSound("audio/${event.model.atlasKey}_attack.wav")
            is EntityDeathEvent -> queueSound("audio/${event.model.atlasKey}_death.wav")
            is EntityLootEvent -> queueSound("audio/${event.model.atlasKey}_open.wav")
            is MenuChoiceEvent -> queueSound("audio/menu_select.mp3")
            is GamePauseEvent -> {
                music?.pause()
                soundCache.values.forEach { it.pause() }
            }
            is GameSelectEvent-> {
                music?.pause()
                soundCache.values.forEach { it.pause() }
                queueSound("audio/pause.mp3")
            }
            is GameResumeEvent -> {
                music?.play()
                queueSound("audio/pause.mp3")
                soundCache.values.forEach { it.resume() }
            }
        }
        return false
    }

    override fun onDispose() {
        musicCache.values.forEach { it.disposeSafely() }
        soundCache.values.forEach { it.disposeSafely() }
    }

    private fun queueSound(soundPath: String) {
        log.debug { "Queueing sound $soundPath" }
        if (soundPath in soundRequests) return

        val sound = soundCache.getOrPut(soundPath) {
            Gdx.audio.newSound(Gdx.files.internal(soundPath))
        }
        soundRequests[soundPath] = sound
    }

    private fun mapChangeEvent(event: MapChangeEvent): Boolean {
        event.map.propertyOrNull<String>("music")?.let { path ->
            log.debug { "Changing music to $path" }
            val newMusic = musicCache.getOrPut(path) {
                Gdx.audio.newMusic(Gdx.files.internal(path)).apply {
                    isLooping = true
                }
            }
            if (music != null && newMusic != music) music?.stop()
            music = newMusic
            music?.play()
        }
        return true
    }

    companion object {
        private val log = logger<AudioSystem>()
    }
}
