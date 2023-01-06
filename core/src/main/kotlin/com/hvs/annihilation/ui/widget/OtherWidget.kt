package com.hvs.annihilation.ui.widget

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.hvs.annihilation.ui.Drawables
import com.hvs.annihilation.ui.get
import ktx.actors.plusAssign
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor

@Scene2dDsl
class OtherWidget(
    skin: Skin,
    private val helmet: Image = Image(skin[Drawables.HELMET])
    ): WidgetGroup(), KGroup {

    init {
        this += helmet
    }

    override fun getPrefHeight() = helmet.drawable.minHeight
    override fun getPrefWidth() = helmet.drawable.minWidth
}

@Scene2dDsl
inline fun <S> KWidget<S>.otherWidget(
    skin: Skin,
    init: OtherWidget.(S) -> Unit = {}
) = actor(OtherWidget(skin), init)
