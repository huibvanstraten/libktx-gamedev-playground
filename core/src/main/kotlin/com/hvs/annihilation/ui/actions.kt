package com.hvs.annihilation.ui

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.actors.plusAssign

fun Label.addSelectionEffect() {
  this.clearActions()
  this += Actions.forever(Actions.sequence(Actions.fadeOut(1f), Actions.fadeIn(1f)))
}

fun Label.removeSelectionEffect() {
  this.clearActions()
  this.color.a = 1f
}
