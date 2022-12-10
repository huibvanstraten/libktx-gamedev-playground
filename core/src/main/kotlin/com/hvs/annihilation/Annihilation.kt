package com.hvs.annihilation

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.hvs.annihilation.assets.I18nAssets
import com.hvs.annihilation.assets.loadAsync
import com.hvs.annihilation.screen.GameMenuUiScreen
import com.hvs.annihilation.screen.LoadingScreen
import com.hvs.annihilation.screen.GameUiScreen
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

class Annihilation: KtxGame<KtxScreen>() {

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        val assetStorage = AssetStorage()

        KtxAsync.initiate()
        KtxAsync.launch {
            // load textures for skin
            val bundle = assetStorage.loadAsync(I18nAssets.DEFAULT)

            addScreen(LoadingScreen(this@Annihilation, AssetStorage(), bundle.await()))
            addScreen(GameMenuUiScreen(bundle.await()))
            setScreen<GameMenuUiScreen>()
        }
    }

    companion object {
        const val UNIT_SCALE = 1 / 16f
    }
}
