package com.hvs.annihilation.ui.widget

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.I18NBundle
import ktx.actors.centerPosition
import ktx.actors.plusAssign
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor

@Scene2dDsl
class StartWidget(
    private val bundle: I18NBundle,
    private val startUiStage: Stage,
    skin: Skin,
    private val banner: Image = Image(Texture(Gdx.files.internal("ui/title.png"))),
) : WidgetGroup(), KGroup {

    init {
        this += banner.apply {
            centerPosition(startUiStage.width * 0.75f, startUiStage.height / 2f)
            this += Actions.fadeIn(200f)
        }
    }

    override fun getPrefHeight(): Float = banner.drawable.minHeight
    override fun getPrefWidth(): Float = banner.drawable.minWidth
}

@Scene2dDsl
inline fun <S> KWidget<S>.startWidget(
    bundle: I18NBundle,
    startUiStage: Stage,
    skin: Skin,
    init: StartWidget.(S) -> Unit = {}
) = actor(StartWidget(bundle, startUiStage, skin), init)
