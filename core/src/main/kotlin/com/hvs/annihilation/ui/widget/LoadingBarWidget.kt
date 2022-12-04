package com.hvs.annihilation.ui.widget

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.ui.Images
import com.hvs.annihilation.ui.LabelStyles
import ktx.actors.plusAssign
import ktx.actors.txt

class LoadingBarWidget(
    private val bundle: I18NBundle,
    private val skin: Skin,
    private val bar: Image = Image(skin.getDrawable(Images.BAR_GREEN.imageName)),
    private val label: Label = Label(bundle["loading"], skin, LabelStyles.LARGE.name)
) : WidgetGroup(
    Image(skin.getTiledDrawable(Images.BAR_BACKGROUND.imageName)),
    bar,
    label
) {
    init {
        with(bar) {
            setPosition(19f, 22f)
            setSize(530f, 43f)
            scaleX = 0f
        }
        with(label) {
            setAlignment(Align.center, Align.center)
            setSize(this@LoadingBarWidget.prefWidth, this@LoadingBarWidget.prefHeight)
        }
    }

    fun scaleTo(percentage: Float, scaleDuration: Float = 0.1f) {
        bar.run {
            clearActions()
            this += scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, scaleDuration)
        }
        if (percentage >= 1f) {
            label.txt = bundle["finishedLoading"]
        }
    }

    override fun getPrefHeight(): Float = 85f
    override fun getPrefWidth(): Float = 600f
}
