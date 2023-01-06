package com.hvs.annihilation.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.hvs.annihilation.assets.TextureAtlasAssets
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.event.EntityTakeDamageEvent
import com.hvs.annihilation.event.fire
import com.hvs.annihilation.ui.createSkin
import com.hvs.annihilation.ui.disposeSkin
import com.hvs.annihilation.ui.model.GameModel
import com.hvs.annihilation.ui.view.GameView
import com.hvs.annihilation.ui.view.gameView
import ktx.app.KtxScreen
import ktx.scene2d.actors

class DevelopUiScreen(
    private val bundle:I18NBundle,
    private val skin: Skin
): KtxScreen {
    private val uiStage: Stage = Stage(ExtendViewport(320f, 180f))
    private val eWorld = World {}
    private val playerEntity: Entity = eWorld.entity {
        add<PlayerComponent>()
        add<LifeComponent>() {
            life = 3f
            maximumLife = 5f
        }
    }
    private val model = GameModel(eWorld, uiStage)
    private lateinit var gameView: GameView


    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height)
    }

    override fun show() {
        uiStage.clear()
        uiStage.addListener(model)
        createSkin(TextureAtlas(TextureAtlasAssets.GAMEUI.filePath))

        uiStage.actors {
            gameView = gameView(model, skin)

        }

        uiStage.isDebugAll = true
    }

    override fun render(delta: Float) {
        //To quickly clean the stage
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            hide()
            show()
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            uiStage.fire(EntityTakeDamageEvent(playerEntity, 1f))
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            gameView.playerLife(0.5f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            gameView.playerLife(1f)
        }

        uiStage.act()
        uiStage.draw()
    }

    override fun dispose() {
        uiStage.dispose()
        disposeSkin()
    }
}
