package com.hvs.annihilation.ui.model

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.World

class OptionsModel(
    world: World,
    stage: Stage
): EventListener {

    override fun handle(event: Event): Boolean = false
}
