package com.hvs.annihilation.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.ui.Drawables
import com.hvs.annihilation.ui.LabelStyles
import com.hvs.annihilation.ui.addSelectionEffect
import com.hvs.annihilation.ui.get
import com.hvs.annihilation.ui.model.ControllerMappingModel
import ktx.collections.GdxArray
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.table

class MappingActionView(
    model: ControllerMappingModel,
    skin: Skin,
    bundle: I18NBundle,
    mappingControllerOptions: GdxArray<Label>
) : View, Table(skin), KTable {

    init {
        setFillParent(true)
        val titlePadding = 15f
        table {
            background = skin[Drawables.MENU_BACKGROUND]
            table { it ->
                label("Choose action", LabelStyles.LARGE.name, skin) {
                    this.setAlignment(Align.center)
                    it.expandX().fill()
                        .pad(8f, titlePadding, 0f, titlePadding)
                        .top()
                        .row()
                }
                table {
                    label("Jump", LabelStyles.DEFAULT.name) { cell ->
                        mappingControllerOptions.add(this@label)
                        setAlignment(Align.right)
                        cell.fillX().padTop(25f).row()
                    }.apply {
                        this.addSelectionEffect()
                    }
                    label("Attack", LabelStyles.DEFAULT.name) { cell ->
                        mappingControllerOptions.add(this@label)
                        setAlignment(Align.right)
                        cell.fillX().row()
                    }
                    label("Back", LabelStyles.DEFAULT.name) { cell ->
                        mappingControllerOptions.add(this@label)
                        setAlignment(Align.right)
                        cell.fillX().row()
                    }
                }
                pack()
                left()
                it.expand().fill()
            }
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.controllerMappingMappingView(
    model: ControllerMappingModel,
    skin: Skin,
    bundle: I18NBundle,
    controllerMappingOptions: GdxArray<Label>,
    init: MappingActionView.(S) -> Unit = {}
): MappingActionView =
    actor(MappingActionView(model, skin, bundle, controllerMappingOptions), init)
