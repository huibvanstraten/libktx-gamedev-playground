package com.hvs.annihilation.ui.view

import com.badlogic.gdx.Gdx
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
import com.hvs.annihilation.ui.model.OptionsModel
import com.hvs.annihilation.ui.widget.audioVolumeWidget
import ktx.actors.onClick
import ktx.collections.GdxArray
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.table

class ControllerMappingView(
    model: ControllerMappingModel,
    skin: Skin,
    bundle: I18NBundle,
    controllerMappingOptions: GdxArray<Label>
) : View, Table(skin), KTable {

    init {
        setFillParent(true)
        val titlePadding = 15f

        table {
            background = skin[Drawables.MENU_BACKGROUND]

            label("Controller Mapping", LabelStyles.LARGE.name, skin) {
                this.setAlignment(Align.center)
                it.expandX().fill()
                    .pad(8f, titlePadding, 0f, titlePadding)
                    .top()
                    .row()
            }

            table { menuTableCell ->
                label("A-Button", LabelStyles.DEFAULT.name) { cell ->
                    controllerMappingOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().padTop(25f).row()
                }.apply {
                    this.addSelectionEffect()
                }
                label("B-Button", LabelStyles.DEFAULT.name) { cell ->
                    controllerMappingOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().row()
                }
                label("X-Button", LabelStyles.DEFAULT.name) { cell ->
                    controllerMappingOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().row()
                }
                label("Y-Button", LabelStyles.DEFAULT.name) { cell ->
                    controllerMappingOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().row()
                }

                label("Back", LabelStyles.DEFAULT.name) { cell ->
                    controllerMappingOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().row()
                }

                pack()
                left()
                menuTableCell.expand().fill()
            }
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.controllerMappingView(
    model: ControllerMappingModel,
    skin: Skin,
    bundle: I18NBundle,
    controllerMappingOptions: GdxArray<Label>,
    init: ControllerMappingView.(S) -> Unit = {}
): ControllerMappingView = actor(ControllerMappingView(model, skin, bundle, controllerMappingOptions), init)
