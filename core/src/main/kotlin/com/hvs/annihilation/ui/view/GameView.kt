package com.hvs.annihilation.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.hvs.annihilation.ui.model.GameModel
import com.hvs.annihilation.ui.widget.PlayerInfoWidget
import com.hvs.annihilation.ui.widget.otherWidget
import com.hvs.annihilation.ui.widget.playerInfoWidget
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.table

@Scene2dDsl
class GameView(
    model: GameModel,
    skin: Skin
) : View, Table(skin), KTable {

     private val playerInfo: PlayerInfoWidget

    init {
        //fill entire viewport/stage/screen
        setFillParent(true)
        table {
            this@GameView.playerInfo = playerInfoWidget(skin)
            it.expand().width(80f).height(30f).top().left()
        }

        table {
            otherWidget(skin)
            it.expand().width(80f).height(30f).bottom().right()
        }

        model.onPropertyChange(model::playerLife) { playerLife ->
            playerLife(playerLife)
        }
    }

    fun playerLife(percentage: Float) = playerInfo.life(percentage)
}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    model: GameModel,
    skin: Skin,
    init: GameView.(S) -> Unit = {}
): GameView = actor(GameView(model, skin), init)
