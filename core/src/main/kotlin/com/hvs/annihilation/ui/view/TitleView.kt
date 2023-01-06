package com.hvs.annihilation.ui.view

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.ui.LabelStyles
import com.hvs.annihilation.ui.addSelectionEffect
import com.hvs.annihilation.ui.model.StartModel
import com.hvs.annihilation.ui.widget.startWidget
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.table

@Scene2dDsl
class StartView(
    bundle: I18NBundle,
    model: StartModel,
    startUiStage: Stage,
    skin: Skin
) : View, Table(skin), KTable {

    private val pressStartLabel: Label


    init {
        //fill entire viewport/stage/screen
        setFillParent(true)

        table {
            table {
                startWidget(bundle, startUiStage, skin)
                row()
            }
            row()
            table {
               this@StartView.pressStartLabel = label("Press Start", LabelStyles.LARGE.name) { cell ->
                    setAlignment(Align.center)
                    cell.fillX().padTop(25f).row()
                }
            }
        }

        pressStartLabel.addSelectionEffect()
    }
}

@Scene2dDsl
inline fun <S> KWidget<S>.startView(
    model: StartModel,
    bundle: I18NBundle,
    startUiStage: Stage,
    skin: Skin,
    init: StartView.(S) -> Unit = {}
) = actor(StartView(bundle, model, startUiStage, skin), init)
