package com.hvs.annihilation.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import com.hvs.annihilation.assets.TextureAtlasAssets
import com.hvs.annihilation.ecs.life.LifeComponent
import com.hvs.annihilation.ecs.player.PlayerComponent
import com.hvs.annihilation.event.fire
import com.hvs.annihilation.ui.createSkin
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.scene2d.actors

class GameMenuUiScreen(
    private val bundle:I18NBundle
): KtxScreen {
    private val gameStage: Stage = Stage(ExtendViewport(320f, 180f))
    private val uiStage: Stage = Stage(ExtendViewport(320f, 180f))
    private val eWorld = World {}
    private val playerEntity: Entity = eWorld.entity {
        add<PlayerComponent>()
        add<LifeComponent>() {
            life = 3f
            maximumLife = 5f
        }
    }
    private val model = GameMenuModel(eWorld, uiStage)
    private lateinit var gameMenuView: GameMenuView


    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height)
    }

    override fun show() {
        uiStage.clear()
        uiStage.addListener(model)
        createSkin(TextureAtlas(TextureAtlasAssets.GAMEUI.filePath))

        uiStage.actors {
            gameMenuView = gameMenuView(model)

        }

        uiStage.isDebugAll = true
    }

    override fun render(delta: Float) {
        //To quickly clean the stage
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            hide()
            show()
        }

        uiStage.act()
        uiStage.draw()
    }

    override fun dispose() {
        uiStage.disposeSafely()
       gameStage.disposeSafely()
    }
}
