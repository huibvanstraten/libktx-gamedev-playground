package com.hvs.annihilation.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.fleks.World
import com.hvs.annihilation.Annihilation
import com.hvs.annihilation.assets.TextureAtlasAssets
import com.hvs.annihilation.ecs.animation.AnimationSystem
import com.hvs.annihilation.ecs.audio.AudioSystem
import com.hvs.annihilation.ecs.camera.CameraSystem
import com.hvs.annihilation.ecs.portal.PortalSystem
import com.hvs.annihilation.ecs.render.RenderSystem
import com.hvs.annihilation.event.GamePauseEvent
import com.hvs.annihilation.event.GameResumeEvent
import com.hvs.annihilation.event.GameSelectEvent
import com.hvs.annihilation.event.MenuChoiceEvent
import com.hvs.annihilation.event.fire
import com.hvs.annihilation.input.InputConverter
import com.hvs.annihilation.input.handler.GameInputHandler
import com.hvs.annihilation.input.handler.SaveHandler
import com.hvs.annihilation.state.PlayerEntity
import com.hvs.annihilation.ui.addSelectionEffect
import com.hvs.annihilation.ui.disposeSkin
import com.hvs.annihilation.ui.model.GameModel
import com.hvs.annihilation.ui.model.SaveModel
import com.hvs.annihilation.ui.removeSelectionEffect
import com.hvs.annihilation.ui.view.GameView
import com.hvs.annihilation.ui.view.PauseView
import com.hvs.annihilation.ui.view.SaveView
import com.hvs.annihilation.ui.view.gameView
import com.hvs.annihilation.ui.view.pauseView
import com.hvs.annihilation.ui.view.saveView
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import ktx.log.logger
import ktx.scene2d.actors

class GameScreen(
    private val game: Annihilation,
    private val gameStage: Stage,
    private val uiStage: Stage,
    private val skin: Skin,
    private val bundle: I18NBundle,
    private val entityWorld: World,
    private val physicsWorld: com.badlogic.gdx.physics.box2d.World,
    private val entity: PlayerEntity
) : KtxScreen, EventListener {

    private val saveOptions = GdxArray<Label>()
    private val textureAtlas = TextureAtlas(TextureAtlasAssets.GAMEUI.filePath)
    private var paused: Boolean = false

    var currentOption = TitleScreen.TOP_OPTION

    private lateinit var inputConverter: InputConverter
    private lateinit var gameView: GameView
    lateinit var saveHandler: SaveHandler

    init {
        gameStage.addListener(this)
    }

    override fun show() {
        log.debug { "This is the GameScreen" }

        entityWorld.system<PortalSystem>().setMap("map/map1.tmx")

        uiStage.clear()
        uiStage.actors {
            gameView = gameView(GameModel(entityWorld, gameStage), skin)
            pauseView(skin) { this.isVisible = false }
            saveView(
                SaveModel(),
                skin,
                bundle,
                saveOptions
            ) { this.isVisible = false }
        }

        gameView.top().left()

        uiStage.act()
        uiStage.draw()

        inputConverter = InputConverter(GameInputHandler(entity, gameStage, uiStage))
    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is GamePauseEvent -> {
                paused = true
                pause()
            }
            is GameResumeEvent -> {
                paused = false
                resume()
            }
            is GameSelectEvent -> {

                saveGameMenu()
            }
        }
        return false
    }

    private fun saveGameMenu() {
        val mandatorySystems = setOf(
            AnimationSystem::class,
            CameraSystem::class,
            RenderSystem::class,
            AudioSystem::class
        )

        entityWorld.systems
            .filter { it::class !in mandatorySystems }
            .forEach { it.enabled = false }

        uiStage.actors.filterIsInstance<SaveView>().first().isVisible = true
        inputConverter.removeXboxControllerListener()
        saveHandler = SaveHandler(this@GameScreen)
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

    fun moveOption(direction: Int) {
        with(saveOptions) {
            this[currentOption].removeSelectionEffect()

            currentOption = nextOptionIdx(this, direction)

            this[currentOption].addSelectionEffect()
            gameStage.fire(MenuChoiceEvent())
        }
    }

    fun selectCurrentOption() {
        saveOptions[currentOption].removeSelectionEffect()
        when (currentOption) {
            OPT_CONTINUE -> continueGame()
            OPT_SAVE_AND_QUIT -> quitAndSaveGame()
            OPT_QUIT -> quitGame()
        }
    }

    private fun continueGame() {
        TODO("Not yet implemented")
    }

    private fun quitAndSaveGame() {
        gameStage.fire(MenuChoiceEvent())
        Gdx.app.getPreferences("Looted Chests").flush()
        quitGame()
    }

    private fun nextOptionIdx(
        options: GdxArray<Label>,
        direction: Int
    ): Int = when {
        currentOption + direction < 0 -> 0
        currentOption + direction > options.size - 1 -> currentOption
        else -> currentOption + direction
    }.also { log.debug { "CURRENT OPTION = $currentOption DIRECTION = $direction" } }

    private fun quitGame() {
        gameStage.fire(MenuChoiceEvent())
        game.addScreen(
            LoadingScreen(
                game,
                AssetStorage(),
                bundle
            )
        )
        saveHandler.removeXboxControllerListener()
        uiStage.clear()
        game.setScreen<LoadingScreen>()
        game.removeScreen<GameScreen>()

        // dispose of everything?
        gameStage.disposeSafely()
    }

    companion object {
        private val log = logger<GameScreen>()

        private const val OPT_CONTINUE = 0
        private const val OPT_SAVE_AND_QUIT = 1
        private const val OPT_QUIT = 2
    }
}
