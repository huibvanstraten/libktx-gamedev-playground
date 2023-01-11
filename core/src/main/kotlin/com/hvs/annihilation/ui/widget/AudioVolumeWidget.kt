package com.hvs.annihilation.ui.widget

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.hvs.annihilation.ui.LabelStyles
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.checkBox
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.scene2d.textButton

class AudioVolumeWidget(
    text: String,
    skin: Skin
) : Table(skin), KTable {

    init {
        table {
            defaults().spaceLeft(5f)
//            checkBox("") { isChecked = true }
//            textButton("-") { it.bottom() }
            label(text, LabelStyles.LARGE.name) {
                setAlignment(Align.center)
                it.space(0f, 15f, 0f, 15f).padTop(10f).width(160f)
            }
//            textButton("+") { it.bottom() }
        }
    }
}

inline fun <S> KWidget<S>.audioVolumeWidget(
    text: String,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: AudioVolumeWidget.(S) -> Unit = {}
) = actor(AudioVolumeWidget(text, skin), init)
