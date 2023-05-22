package com.hvs.annihilation.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import ktx.log.logger

fun gdxInputProcessor(processor: InputProcessor) {
  val currentProcessor = Gdx.input.inputProcessor
  if (currentProcessor == null) {
    Gdx.input.inputProcessor = processor
  } else {
    if (currentProcessor is InputMultiplexer) {
      if (currentProcessor !in currentProcessor.processors) {
        currentProcessor.addProcessor(processor)
      }
    } else {
      Gdx.input.inputProcessor = InputMultiplexer(currentProcessor, processor)
    }
  }
}

interface XboxInputProcessor : ControllerListener {

  /**
   * Adds this instance as a [ControllerListener] to the first X360 Controller that is found.
   */
  fun addXboxControllerListener() {
    log.debug { "getting controller" }
    Controllers.getControllers()
      .firstOrNull { CONTROLLER_NAME == it.name }
      ?.run {
        removeListener(this@XboxInputProcessor)
        addListener(this@XboxInputProcessor)
        log.debug { "controller = ${this.name}" }
        log { "controller connected" }
      }
  }

  /**
   * Removes this instance as a [ControllerListener] from the first X360 Controller that is found.
   */
  fun removeXboxControllerListener() {
    log.debug { "REMOVING CONTROLLER" }
    Controllers.getControllers()
      .firstOrNull { CONTROLLER_NAME == it.name }
      ?.removeListener(this)
  }

  override fun connected(controller: Controller?) = Unit
  override fun disconnected(controller: Controller?) = Unit

  companion object {
    private const val CONTROLLER_NAME = "XInput Controller"
    val log = logger<XboxInputProcessor>()


    const val BUTTON_A = 0
    const val BUTTON_B = 1
    const val BUTTON_X = 2
    const val BUTTON_Y = 3
    const val BUTTON_SELECT = 4
    const val BUTTON_START = 6
    const val BUTTON_L = 9
    const val BUTTON_R = 10
    const val BUTTON_UP = 11
    const val BUTTON_RIGHT = 14
    const val BUTTON_DOWN = 12
    const val BUTTON_LEFT = 13
    const val BUTTON_TRIGGER_L = 7
    const val BUTTON_TRIGGER_R = 8

    const val AXIS_X_LEFT = 0
    const val AXIS_Y_LEFT = 1
    const val AXIS_X_RIGHT = 2
    const val AXIS_Y_RIGHT = 3
    const val AXIS_L2 = 4
    const val AXIS_R2 = 5
  }
}
