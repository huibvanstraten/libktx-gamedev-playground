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

class OptionsView(
    model: OptionsModel,
    skin: Skin,
    bundle: I18NBundle,
    selectOptions: GdxArray<Label>
) : View, Table(skin), KTable {

    init {
        setFillParent(true)
        val titlePadding = 15f

        table { outerTableCell ->
            background = skin[Drawables.MENU_BACKGROUND]

            label("Options", LabelStyles.LARGE.name, skin) {
                this.setAlignment(Align.center)
                it.expandX().fill()
                    .pad(8f, titlePadding, 0f, titlePadding)
                    .top()
                    .row()
            }

            table { menuTableCell ->
                label("Sound", LabelStyles.DEFAULT.name) { cell ->
                    selectOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().padTop(25f).row()
                }.apply {
                    this.addSelectionEffect()
                }
                label("Volume", LabelStyles.DEFAULT.name) { cell ->
                    selectOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().row()
                }
                label("Controller mapping", LabelStyles.DEFAULT.name) { cell ->
                    selectOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().row()
                }
                label("Back", LabelStyles.DEFAULT.name) { cell ->
                    selectOptions.add(this@label)
                    setAlignment(Align.left)
                    cell.fillX().row()
                }.onClick { Gdx.app.exit() }

                pack()
                left()
                menuTableCell.expand().fill()
            }
            table { audioVolumeWidget(bundle["music"]) { cell -> cell.fillX().padLeft(25f).row() } }
            outerTableCell.expand().width(300f).height(200f).left().center()
        }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.optionsView(
    model: OptionsModel,
    skin: Skin,
    bundle: I18NBundle,
    menuOptions: GdxArray<Label>,
    init: OptionsView.(S) -> Unit = {}
): OptionsView = actor(OptionsView(model, skin, bundle, menuOptions), init)
