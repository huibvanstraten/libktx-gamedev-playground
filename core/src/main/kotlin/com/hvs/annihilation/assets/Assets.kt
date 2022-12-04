package com.hvs.annihilation.assets

import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.utils.I18NBundle
import ktx.assets.async.AssetStorage

// music
enum class MusicAssets(val filePath: String, val volumeScale: Float = 0.15f) {
    LEVEL_1("audio/fields.ogg")
}

fun AssetStorage.loadAsync(asset: MusicAssets) = loadAsync<Music>(asset.filePath)
operator fun AssetStorage.get(asset: MusicAssets) = get<Music>(asset.filePath)

// sound
enum class SoundAssets(val filePath: String, val volumeScale: Float = 1f) {
    UNKNOWN(""),
    CHEST_OPEN("audio/chest_open.wav"),
    PLAYER_ATTACK("audio/player_attack.wav")
}

fun AssetStorage.loadAsync(asset: SoundAssets) = loadAsync<Sound>(asset.filePath)
operator fun AssetStorage.get(asset: SoundAssets) = get<Sound>(asset.filePath)

// texture atlas
enum class TextureAtlasAssets(val filePath: String) {
    GAME_OBJECTS("assets/graphics/game.atlas"),
    UI("assets/ui/ui.atlas")
}

fun AssetStorage.loadAsync(asset: TextureAtlasAssets) = loadAsync<TextureAtlas>(asset.filePath)
operator fun AssetStorage.get(asset: TextureAtlasAssets) = get<TextureAtlas>(asset.filePath)

// tiled map
enum class MapAssets(val filePath: String) {
    MAP_1("map/map1.tmx"),
    START("map/startbackground.tmx")
}

fun AssetStorage.loadAsync(asset: MapAssets) = loadAsync<TiledMap>(asset.filePath)
operator fun AssetStorage.get(asset: MapAssets) = get<TiledMap>(asset.filePath)

// particle effects
enum class ParticleAssets(val filePath: String, val scale: Float = 1f, val sound: SoundAssets = SoundAssets.UNKNOWN) {
    BLOOD("particles/blood.p", 0.5f),
    PORTAL("particles/portal.p"),
    PORTAL2("particles/portal2.p", 0.5f)
}

fun AssetStorage.loadAsync(asset: ParticleAssets, params: ParticleEffectLoader.ParticleEffectParameter) =
    loadAsync<ParticleEffect>(asset.filePath, params)

operator fun AssetStorage.get(asset: ParticleAssets) = get<ParticleEffect>(asset.filePath)

enum class I18nAssets(val filePath: String) {
    DEFAULT("ui/i18n")
}

fun AssetStorage.loadAsync(asset: I18nAssets) = loadAsync<I18NBundle>(asset.filePath)
operator fun AssetStorage.get(asset: I18nAssets) = get<I18NBundle>(asset.filePath)
