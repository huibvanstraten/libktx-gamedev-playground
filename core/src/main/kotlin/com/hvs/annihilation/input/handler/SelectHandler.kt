package com.hvs.annihilation.input.handler

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.Annihilation
import com.hvs.annihilation.event.MenuChoiceEvent
import com.hvs.annihilation.event.fire
import com.hvs.annihilation.input.XboxInputProcessor
import com.hvs.annihilation.screen.GameScreen
import com.hvs.annihilation.screen.TitleScreen
import com.hvs.annihilation.ui.addSelectionEffect
import com.hvs.annihilation.ui.model.SelectModel
import com.hvs.annihilation.ui.removeSelectionEffect
import com.hvs.annihilation.ui.view.selectView
import ktx.collections.GdxArray
import ktx.log.logger
import ktx.scene2d.actors

class SelectHandler(
    private val game: Annihilation,
    private val gameStage: Stage,
    private val uiStage: Stage,
    private val bundle: I18NBundle,
    private val skin: Skin
): XboxInputProcessor {
    private val menuOptions = GdxArray<Label>()
    private var currentOption = OPT_NEW
    private var menuShown: Boolean = false

    init {
        log.debug { "SELECT HANDLER" }
        addXboxControllerListener()
    }

    override fun buttonDown(
        controller: Controller,
        buttonCode: Int
    ): Boolean {
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
                gameSelectMenu()
            }
        }
        return true
    }

    private fun nextOptionIdx(direction: Int): Int {
        return when {
            currentOption + direction < 0 -> menuOptions.size - 1
            currentOption + direction >= menuOptions.size -> 0
            else -> currentOption + direction
        }
    }

    private fun moveOption(direction: Int) {
        menuOptions[currentOption].removeSelectionEffect()

        currentOption = nextOptionIdx(direction)

        menuOptions[currentOption].addSelectionEffect()
        gameStage.fire(MenuChoiceEvent())
    }

    private fun selectCurrentOption() {
        when (currentOption) {
            OPT_NEW -> startGame()
            OPT_CONTINUE -> startGame()
            OPT_QUIT -> quitGame()
        }
    }

    private fun startGame() {
        gameStage.fire(MenuChoiceEvent())
        removeXboxControllerListener()
        game.setScreen<GameScreen>()
        game.removeScreen<TitleScreen>()
    }

    private fun continueGame() {
        gameStage.fire(MenuChoiceEvent())
        removeXboxControllerListener()
        game.setScreen<GameScreen>()
        game.removeScreen<TitleScreen>()
    }

    private fun quitGame() {
        gameStage.fire(MenuChoiceEvent())
        Gdx.app.exit()
    }

    private fun gameSelectMenu() {
        if (!menuShown) {
            menuShown = true
            uiStage.clear()
            uiStage.actors {
                selectView(
                    SelectModel(gameStage),
                    bundle,
                    skin,
                    menuOptions
                )
            }
            uiStage.draw()
        } else {
            selectCurrentOption()
        }
    }

    override fun buttonUp(controller: Controller, buttonCode: Int) = false
    override fun axisMoved(controller: Controller, axisCode: Int, value: Float) = false

    companion object {
        private val log = logger<GameScreen>()

        private const val OPT_NEW = 0
        private const val OPT_CONTINUE = 1
        private const val OPT_QUIT = 2
    }
}
