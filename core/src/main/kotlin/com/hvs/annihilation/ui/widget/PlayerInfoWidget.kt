package com.hvs.annihilation.ui.widget

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Scaling
import com.hvs.annihilation.ui.Drawables
import com.hvs.annihilation.ui.get
import ktx.actors.plusAssign
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor

@Scene2dDsl
class PlayerInfoWidget(
    skin: Skin,
    private val background: Image = Image(skin[Drawables.PLAYER_INFO_BACKGROUND]),
    player: Image = Image(skin[Drawables.PLAYER]),
    private val lifeBar: Image = Image(skin[Drawables.LIFE_BAR]),
    private val manaBar: Image = Image(skin[Drawables.MANA_BAR]),
    ): WidgetGroup(), KGroup {

    init {
        this += background
        this += player.apply {
            setPosition(2f, 2f)
            setSize(22f, 20f)
            setScaling(Scaling.contain)
        }
        this += lifeBar.apply { setPosition(26f, 19f) }
        this += manaBar.apply { setPosition(26f, 13f) }
    }

    override fun getPrefHeight() = background.drawable.minHeight
    override fun getPrefWidth() = background.drawable.minWidth


    fun life(percentage: Float, duration: Float = 0.75f) {
        lifeBar.clearActions()
        lifeBar += Actions.scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, duration)
    }

    fun mana(percentage: Float, duration: Float = 0.75f) {
        manaBar.clearActions()
        manaBar += Actions.scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, duration)
    }
}

@Scene2dDsl
inline fun <S> KWidget<S>.playerInfoWidget(
    skin: Skin = Scene2DSkin.defaultSkin,
    init: PlayerInfoWidget.(S) -> Unit = {}
) = actor(PlayerInfoWidget(skin), init)
