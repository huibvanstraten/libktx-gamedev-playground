package com.hvs.annihilation.ui.widget

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.ui.Drawables
import com.hvs.annihilation.ui.LabelStyles
import com.hvs.annihilation.ui.get
import ktx.actors.plusAssign
import ktx.actors.txt
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor

@Scene2dDsl
class LoadingBarWidget(
    private val bundle: I18NBundle,
    private val skin: Skin,
    private val loadingBar: Image = Image(skin[Drawables.BAR_GREEN]),
    private val barBackground: Image = Image(skin[Drawables.BAR_BACKGROUND]),
    private val label: Label = Label(bundle["loading"], skin, LabelStyles.LARGE.name)
) : WidgetGroup(), KGroup {

    init {
        this += barBackground
        this += loadingBar.apply {
            setPosition(19f, 22f)
            setSize(100f, 20f)
            scaleX = 0f
        }
        this += label.apply {
            setAlignment(Align.center, Align.center)
            setSize(this@LoadingBarWidget.prefWidth, this@LoadingBarWidget.prefHeight)
        }
    }

    fun scaleTo(percentage: Float, scaleDuration: Float = 0.1f) {
        loadingBar.run {
            clearActions()
            this += scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, scaleDuration)
        }
        if (percentage >= 1f) {
            label.txt = bundle["finishedLoading"]
        }
    }

    override fun getPrefHeight(): Float = barBackground.drawable.minHeight
    override fun getPrefWidth(): Float = barBackground.drawable.minWidth
}

@Scene2dDsl
inline fun <S> KWidget<S>.loadingBarWidget(
    bundle: I18NBundle,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: LoadingBarWidget.(S) -> Unit = {}
) = actor(LoadingBarWidget(bundle, skin), init)
