package com.hvs.annihilation.screen

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.fleks.World
import com.hvs.annihilation.Annihilation
import com.hvs.annihilation.assets.TextureAtlasAssets
import com.hvs.annihilation.input.handler.SelectHandler
import com.hvs.annihilation.ui.createSkin
import com.hvs.annihilation.ui.model.SelectModel
import com.hvs.annihilation.ui.model.StartModel
import com.hvs.annihilation.ui.view.startView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.scene2d.actors

class TitleScreen(
    game: Annihilation,
    private val gameStage: Stage,
    private val uiStage: Stage,
    private val bundle: I18NBundle,
    entityWorld: World
) : KtxScreen {
    private val textureAtlas = TextureAtlas(TextureAtlasAssets.GAMEUI.filePath)
    private val skin = createSkin(textureAtlas)

    private val selectModel = SelectModel(uiStage)
    private val startModel = StartModel(uiStage)

    init {
        uiStage.addListener(selectModel)

        entityWorld.systems.forEach { sys ->
            if (sys is EventListener) {
                gameStage.addListener(sys)
            }
        }

        SelectHandler(
            game,
            gameStage,
            uiStage,
            bundle,
            skin
        )
    }

    override fun show() {
        log.debug { "This is the title screen" }
        uiStage.actors { startView(startModel, bundle, uiStage, skin) }
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        uiStage.act()
        uiStage.draw()
    }

    override fun dispose() {
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
