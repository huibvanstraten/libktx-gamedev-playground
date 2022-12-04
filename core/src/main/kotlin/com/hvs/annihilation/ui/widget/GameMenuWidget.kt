package com.hvs.annihilation.ui.widget

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.hvs.annihilation.ui.Images
import com.hvs.annihilation.ui.LabelStyles
import ktx.actors.onClick
import ktx.collections.GdxArray
import ktx.scene2d.KTable
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label

class GameMenuWidget(
    bundle: I18NBundle,
    skin: Skin = Scene2DSkin.defaultSkin,
    menuOptions: GdxArray<Label>
) : Table(skin), KTable {
    private val newGameLabel: Label
    private val continueLabel: Label

    init {
        skin.getDrawable(Images.MENU_BACKGROUND.imageName)
        defaults().pad(5f, 70f, 25f, 25f)

        newGameLabel = label(bundle["newGame"], LabelStyles.LARGE.name) { cell ->
            menuOptions.add(this)
            setAlignment(Align.center)
            cell.fillX().padTop(25f).row()
        }
        continueLabel = label(bundle["continue"], LabelStyles.LARGE.name) { cell ->
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
}
