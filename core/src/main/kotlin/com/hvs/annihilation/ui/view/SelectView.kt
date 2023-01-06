package com.hvs.annihilation.ui.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.ui.Drawables
import com.hvs.annihilation.ui.LabelStyles
import com.hvs.annihilation.ui.addSelectionEffect
import com.hvs.annihilation.ui.get
import com.hvs.annihilation.ui.model.SelectModel
import ktx.actors.onClick
import ktx.collections.GdxArray
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.table

@Scene2dDsl
class SelectView(
    model: SelectModel,
    skin: Skin,
    bundle: I18NBundle,
    menuOptions: GdxArray<Label>
) : View, Table(skin), KTable {

    private val startLabel: Label

    init {
        //fill entire viewport/stage/screen
        setFillParent(true)
        defaults().pad(5f, 70f, 25f, 25f)

        debugAll()
        table {
            Image(skin[Drawables.MENU_BACKGROUND])
            this@SelectView.startLabel = label(bundle["newGame"], LabelStyles.LARGE.name) { cell ->
                menuOptions.add(this)
                setAlignment(Align.center)
                cell.fillX().padTop(25f).row()
            }

            label(bundle["continue"], LabelStyles.LARGE.name) { cell ->
                menuOptions.add(this)
                setAlignment(Align.center)
                cell.fillX().row()
            }
            label(bundle["quitGame"], LabelStyles.LARGE.name) { cell ->
                menuOptions.add(this)
                setAlignment(Align.center)
                cell.fillX().row()
            }.onClick { Gdx.app.exit() }

            pack()
            left()
        }

        startLabel.addSelectionEffect()
    }
}

@Scene2dDsl
inline fun <S> KWidget<S>.selectView(
    model: SelectModel,
    bundle: I18NBundle,
    skin: Skin,
    menuOptions: GdxArray<Label>,
    init: SelectView.(S) -> Unit = {}
) = actor(SelectView(model, skin, bundle, menuOptions), init)
