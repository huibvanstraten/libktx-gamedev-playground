package com.hvs.annihilation.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.fleks.World
import com.hvs.annihilation.Annihilation
import com.hvs.annihilation.assets.TextureAtlasAssets
import com.hvs.annihilation.event.MenuChoiceEvent
import com.hvs.annihilation.event.fire
import com.hvs.annihilation.input.handler.SelectHandler
import com.hvs.annihilation.ui.addSelectionEffect
import com.hvs.annihilation.ui.createSkin
import com.hvs.annihilation.ui.model.OptionsModel
import com.hvs.annihilation.ui.model.SelectModel
import com.hvs.annihilation.ui.model.TitleModel
import com.hvs.annihilation.ui.removeSelectionEffect
import com.hvs.annihilation.ui.view.OptionsView
import com.hvs.annihilation.ui.view.SelectView
import com.hvs.annihilation.ui.view.TitleView
import com.hvs.annihilation.ui.view.optionsView
import com.hvs.annihilation.ui.view.selectView
import com.hvs.annihilation.ui.view.titleView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import ktx.log.logger
import ktx.scene2d.actors

class TitleScreen(
    private val game: Annihilation,
    private val gameStage: Stage,
    private val uiStage: Stage,
    private val bundle: I18NBundle,
    private val entityWorld: World
) : KtxScreen {
    private val textureAtlas = TextureAtlas(TextureAtlasAssets.GAMEUI.filePath)
    private val skin = createSkin(textureAtlas)

    private val selectOptions = GdxArray<Label>()
    private val menuOptions = GdxArray<Label>()

    var menuShown: Boolean = false
    var optionsShown: Boolean = false
    var currentOption = TOP_OPTION

    private val selectModel = SelectModel(uiStage)
    private val titleModel = TitleModel(uiStage)

    init {
        uiStage.addListener(selectModel)

        entityWorld.systems.forEach { sys ->
            if (sys is EventListener) {
                gameStage.addListener(sys)
            }
        }

        SelectHandler(this@TitleScreen)
    }

    override fun show() {
        log.debug { "This is the title screen" }
        uiStage.actors {
            titleView(
                titleModel,
                bundle,
                uiStage,
                skin
            ) { isVisible = true }
            selectView(
                SelectModel(gameStage),
                bundle,
                skin,
                selectOptions
            ) { isVisible = false }
            optionsView(
                OptionsModel(uiStage),
                skin,
                bundle,
                menuOptions
            ) { isVisible = false }
            controllerMappingView
        }
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        entityWorld.update(delta)
        uiStage.act()
        uiStage.draw()
    }

    override fun dispose() {
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
    }

    fun gameSelectMenu() {
        if (!menuShown && !optionsShown) {
            menuShown = true
            currentOption = OPT_NEW
            selectOptions[currentOption].addSelectionEffect()
            uiStage.actors.filterIsInstance<TitleView>().first().isVisible = !menuShown
            uiStage.actors.filterIsInstance<SelectView>().first().isVisible = menuShown
            uiStage.actors.filterIsInstance<OptionsView>().first().isVisible = !menuShown
            uiStage.draw()
        } else {
            selectCurrentOption()
        }
    }

    fun moveOption(direction: Int) {
        if (menuShown) {
            with(selectOptions) {

                this[currentOption].removeSelectionEffect()

                currentOption = nextOptionIdx(direction)

                this[currentOption].addSelectionEffect()
                gameStage.fire(MenuChoiceEvent())
            }
        } else {
            with(menuOptions) {
                this[currentOption].removeSelectionEffect()

                currentOption = nextOptionIdx(direction)

                this[currentOption].addSelectionEffect()
                gameStage.fire(MenuChoiceEvent())
            }
        }
    }

    private fun selectCurrentOption() {
        if (menuShown) {
            selectOptions[currentOption].removeSelectionEffect()
            when (currentOption) {
                OPT_NEW -> startGame()
                OPT_CONTINUE -> startGame()
                OPT_OPTIONS -> options()
                OPT_QUIT -> quitGame()
            }
        } else if (optionsShown) {
            menuOptions[currentOption].removeSelectionEffect()
            when (currentOption) {
                SOUND -> startGame()
                VOLUME -> startGame()
                CONTROLLER_MAPPING -> options()
                BACK -> back()
            }
        }
    }

    private fun nextOptionIdx(direction: Int): Int {
        log.debug { "CURRENT OPTION = $currentOption DIRECTION = $direction" }

        return when {
            currentOption + direction < 0 -> 0
            currentOption + direction > 3 -> currentOption
            else -> currentOption + direction
        }
    }

    private fun startGame() {
        gameStage.fire(MenuChoiceEvent())
//        removeXboxControllerListener()
        game.setScreen<GameScreen>()
        game.removeScreen<TitleScreen>()
    }

    private fun options() {
        gameStage.fire(MenuChoiceEvent())
            optionsShown = true
            menuShown = false
            currentOption = SOUND
            menuOptions[currentOption].addSelectionEffect()
            uiStage.actors.filterIsInstance<TitleView>().first().isVisible = !optionsShown
            uiStage.actors.filterIsInstance<SelectView>().first().isVisible = !optionsShown
            uiStage.actors.filterIsInstance<OptionsView>().first().isVisible = optionsShown
            uiStage.draw()
    }


    private fun continueGame() {
        gameStage.fire(MenuChoiceEvent())
//        removeXboxControllerListener()
        game.setScreen<GameScreen>()
        game.removeScreen<TitleScreen>()
    }

    private fun quitGame() {
        gameStage.fire(MenuChoiceEvent())
        Gdx.app.exit()
    }

    private fun back() {
        gameStage.fire(MenuChoiceEvent())
        optionsShown = false
        gameSelectMenu()
    }

    companion object {
        private val log = logger<GameScreen>()

        private const val OPT_NEW = 0
        private const val OPT_OPTIONS = 1
        private const val OPT_CONTINUE = 2
        private const val OPT_QUIT = 3

        private const val SOUND = 0
        private const val VOLUME = 1
        private const val CONTROLLER_MAPPING = 2
        private const val BACK = 3

        private const val TOP_OPTION = 0
    }
}
