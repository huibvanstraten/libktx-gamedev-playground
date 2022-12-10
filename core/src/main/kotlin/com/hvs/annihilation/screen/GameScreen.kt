package com.hvs.annihilation.screen

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.World
import com.hvs.annihilation.assets.TextureAtlasAssets
import com.hvs.annihilation.event.MapChangeEvent
import com.hvs.annihilation.event.fire
import com.hvs.annihilation.input.InputConverter
import com.hvs.annihilation.input.InputHandler
import com.hvs.annihilation.state.PlayerEntity
import com.hvs.annihilation.ui.createSkin
import com.hvs.annihilation.ui.disposeSkin
import com.hvs.annihilation.ui.model.GameModel
import com.hvs.annihilation.ui.view.GameView
import com.hvs.annihilation.ui.view.gameView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.scene2d.actors

class GameScreen(
    private val gameStage: Stage,
    private val uiStage: Stage,
    private val entityWorld: World,
    private val physicsWorld: com.badlogic.gdx.physics.box2d.World,
): KtxScreen {

    private val textureAtlas = TextureAtlas(TextureAtlasAssets.GAMEUI.filePath)

    lateinit var gameView: GameView
    lateinit var currentMap: TiledMap

    init {
        entityWorld.systems.forEach { sys ->
            if (sys is EventListener) {
                gameStage.addListener(sys)
            }
        }

        InputConverter(
            inputHandler = InputHandler(
                PlayerEntity(entityWorld.entity(), entityWorld)
            )
        )

        createSkin(TextureAtlas(TextureAtlasAssets.GAMEUI.filePath))
    }

    override fun show() {
        log.debug { "This is the GameScreen" }

        uiStage.clear()
        currentMap = TmxMapLoader().load("map/map1.tmx") //TODO: this is static 1 map
        gameStage.fire(MapChangeEvent(currentMap))

        uiStage.actors {
            gameView = gameView(GameModel(entityWorld, gameStage))
        }

        gameView.debugAll()
        gameView.top().left()

        uiStage.act()
        uiStage.draw()
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        val deltaTime = delta.coerceAtMost(0.25f)
        GdxAI.getTimepiece().update(deltaTime)
        entityWorld.update(deltaTime)
    }

    override fun dispose() {
        entityWorld.dispose()
        physicsWorld.disposeSafely()
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
        currentMap.disposeSafely()
        disposeSkin()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
