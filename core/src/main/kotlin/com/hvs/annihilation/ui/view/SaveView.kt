package com.hvs.annihilation.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.ui.LabelStyles
import com.hvs.annihilation.ui.addSelectionEffect
import com.hvs.annihilation.ui.model.SaveModel
import ktx.collections.GdxArray
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.table

class SaveView(
    model: SaveModel,
    skin: Skin,
    bundle: I18NBundle,
    saveOptions: GdxArray<Label>
): KTable, Table(skin) {

    private val saveLabel: Label

    init {
        setFillParent(true)

        table { saveTableCell ->
            this@SaveView.saveLabel = label("Continue", LabelStyles.DEFAULT.name) { cell ->
                saveOptions.add(this@label)
                setAlignment(Align.left)
                cell.fillX().row()
            }
            label("Save & Quit", LabelStyles.DEFAULT.name) { cell ->
                saveOptions.add(this@label)
                setAlignment(Align.left)
                cell.fillX().row()
            }
            label("Quit without saving", LabelStyles.DEFAULT.name) { cell ->
                saveOptions.add(this@label)
                setAlignment(Align.left)
                cell.fillX().row()
            }

            pack()
            center()
            saveTableCell.expand().fill()
        }

        saveLabel.addSelectionEffect()
    }

    companion object {
    }
}

@Scene2dDsl
fun <S> KWidget<S>.saveView(
    model: SaveModel,
    skin: Skin,
    bundle: I18NBundle,
    saveOptions: GdxArray<Label>,
    init: SaveView.(S) -> Unit = {}
): SaveView = actor(SaveView(model, skin, bundle, saveOptions), init)
