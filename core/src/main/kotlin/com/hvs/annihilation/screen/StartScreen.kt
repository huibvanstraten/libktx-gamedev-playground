package com.hvs.annihilation.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.Annihilation
import com.hvs.annihilation.event.MenuChoiceEvent
import com.hvs.annihilation.event.fire
import com.hvs.annihilation.input.XboxInputProcessor
import com.hvs.annihilation.ui.LabelStyles
import com.hvs.annihilation.ui.addSelectionEffect
import com.hvs.annihilation.ui.createSkin
import com.hvs.annihilation.ui.removeSelectionEffect
import com.hvs.annihilation.ui.widget.StartMenuWidget
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import ktx.log.logger

class StartScreen(
    private val game: Annihilation,
    private val startStage: Stage,
    private val startUiStage: Stage,
    private val bundle: I18NBundle
) : KtxScreen, XboxInputProcessor {
    private val menuOptions = GdxArray<Label>()
    private var currentOption = OPT_NEW
    private val textureAtlas = TextureAtlas("assets/ui/ui.atlas")
    private val skin = createSkin(textureAtlas)

    private lateinit var titleTexture: Texture

    private val pressStartToBegin =
        Label("Press Start", skin, LabelStyles.LARGE.name).apply {
            wrap = true
            this += Actions.forever(Actions.sequence(Actions.fadeIn(2f), Actions.fadeOut(1f)))
        }

    private var menuShown: Boolean = false

    init {
        addXboxControllerListener()
    }


    override fun show() {
        log.debug { "This is the startscreen" }

        titleTexture = Texture(Gdx.files.internal("ui/title.png"))

        val banner = Image(titleTexture).apply {
            log.debug { "TITLE" }
            setPosition(100f, 200f)
            this += Actions.fadeIn(100f)
        }
        startUiStage.addActor(banner)
        startUiStage.addActor(pressStartToBegin)
        banner.centerPosition(startUiStage.width, 800f)
        pressStartToBegin.centerPosition(startUiStage.width, 400f)
    }

    override fun resize(width: Int, height: Int) {
        startStage.viewport.update(width, height, true)
        startUiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        startUiStage.act()
        startUiStage.draw()
    }

    override fun dispose() {
        startStage.disposeSafely()
        startUiStage.disposeSafely()
        textureAtlas.disposeSafely()
    }

    private fun nextOptionIdx(direction: Int): Int {
        return when {
            currentOption + direction < 0 -> menuOptions.size - 1
            currentOption + direction >= menuOptions.size -> 0
            else -> currentOption + direction
        }
    }

    override fun buttonDown(controller: Controller, buttonCode: Int): Boolean {
        when (buttonCode) {
            XboxInputProcessor.BUTTON_DOWN -> {
                moveOption(1)
                log.debug { "UP" }
            }
            XboxInputProcessor.BUTTON_UP -> {
                moveOption(-1)
                log.debug { "DOWN" }
            }
            XboxInputProcessor.BUTTON_START -> {
                if (!menuShown) {

                    menuShown = true
                    val menu = StartMenuWidget(
                        bundle = bundle,
                        skin = skin,
                        menuOptions
                    )
                    startUiStage.clear()
                    startUiStage.addActor(menu)
                    log.debug { "GAMEMENUWIDGET" }

                    menu.centerPosition(height = 400f)
                } else {
                    selectCurrentOption()
                    log.debug { "START" }
                }
            }
        }
        return true
    }

    private fun moveOption(direction: Int) {
        menuOptions[currentOption].removeSelectionEffect()

        currentOption = nextOptionIdx(direction)

        menuOptions[currentOption].addSelectionEffect()
        startStage.fire(MenuChoiceEvent())
    }

    private fun selectCurrentOption() {
        when (currentOption) {
            OPT_NEW -> startGame()
            OPT_CONTINUE -> startGame()
            OPT_QUIT -> quitGame()
        }
    }

    private fun startGame() {
        startStage.fire(MenuChoiceEvent())
        removeXboxControllerListener()
        game.setScreen<GameScreen>()
        game.removeScreen<StartScreen>()
    }

    private fun quitGame() {
        startStage.fire(MenuChoiceEvent())
        Gdx.app.exit()
    }

    override fun buttonUp(controller: Controller, buttonCode: Int) = false
    override fun axisMoved(controller: Controller, axisCode: Int, value: Float) = false

    companion object {
        private val log = logger<GameScreen>()

        private const val OPT_NEW = 0
        private const val OPT_CONTINUE = 1
//        private const val OPT_MUSIC = 2
//        private const val OPT_SOUND = 3
//        private const val OPT_CREDITS = 4
        private const val OPT_QUIT = 2
    }
}
