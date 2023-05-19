package com.hvs.annihilation.screen

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.github.quillraven.fleks.World
import com.hvs.annihilation.assets.TextureAtlasAssets
import com.hvs.annihilation.ecs.animation.AnimationSystem
import com.hvs.annihilation.ecs.audio.AudioSystem
import com.hvs.annihilation.ecs.camera.CameraSystem
import com.hvs.annihilation.ecs.portal.PortalSystem
import com.hvs.annihilation.ecs.render.RenderSystem
import com.hvs.annihilation.event.GamePauseEvent
import com.hvs.annihilation.event.GameResumeEvent
import com.hvs.annihilation.input.InputConverter
import com.hvs.annihilation.input.handler.GameInputHandler
import com.hvs.annihilation.state.PlayerEntity
import com.hvs.annihilation.ui.disposeSkin
import com.hvs.annihilation.ui.model.GameModel
import com.hvs.annihilation.ui.view.GameView
import com.hvs.annihilation.ui.view.PauseView
import com.hvs.annihilation.ui.view.gameView
import com.hvs.annihilation.ui.view.pauseView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.scene2d.actors

class GameScreen(
    private val gameStage: Stage,
    private val uiStage: Stage,
    private val skin: Skin,
    private val entityWorld: World,
    private val physicsWorld: com.badlogic.gdx.physics.box2d.World,
    private val entity: PlayerEntity
) : KtxScreen, EventListener {

    private val textureAtlas = TextureAtlas(TextureAtlasAssets.GAMEUI.filePath)
    private var paused: Boolean = false

    private lateinit var inputConverter: InputConverter
    private lateinit var gameView: GameView

    init {
        gameStage.addListener(this)
    }

    override fun show() {
        inputConverter = InputConverter(GameInputHandler(entity, gameStage))

        log.debug { "This is the GameScreen" }

        entityWorld.system<PortalSystem>().setMap("map/map1.tmx")

        uiStage.clear()
        uiStage.actors {
            gameView = gameView(GameModel(entityWorld, gameStage), skin)
            pauseView(skin) { this.isVisible = false }
        }

        gameView.top().left()

        uiStage.act()
        uiStage.draw()
    }

    override fun handle(event: Event): Boolean {
        when(event) {
            is GamePauseEvent -> {
                paused = true
                pause()
            }
            is GameResumeEvent -> {
                paused = false
                resume()
            }
        }
        return false
    }

    override fun pause() = pauseWorld(true)

    override fun resume() = pauseWorld(false)

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        val deltaTime = if (paused) 0f else delta.coerceAtMost(0.25f)
        GdxAI.getTimepiece().update(deltaTime)
        entityWorld.update(deltaTime)
    }

    override fun dispose() {
        entityWorld.dispose()
        physicsWorld.disposeSafely()
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
        disposeSkin()
    }

    private fun pauseWorld(pause: Boolean) {
        val mandatorySystems = setOf(
            AnimationSystem::class,
            CameraSystem::class,
            RenderSystem::class,
            AudioSystem::class
        )

        entityWorld.systems
            .filter { it::class !in mandatorySystems }
            .forEach { it.enabled = !pause }

        uiStage.actors.filterIsInstance<PauseView>().first().isVisible = pause
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
