package com.hvs.annihilation.ui.view

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.hvs.annihilation.ui.LabelStyles
import ktx.actors.plusAssign
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.label

class PauseView(
    skin: Skin
): KTable, Table(skin) {

    init {
        setFillParent(true)

        if(!skin.has(PIXMAP_KEY, TextureRegionDrawable::class.java)) {
            skin.add(PIXMAP_KEY, TextureRegionDrawable(
                Texture(Pixmap(1,1, Pixmap.Format.RGBA8888).apply {
                        this.drawPixel(0,0, Color.rgba8888(0.1f, 0.1f, 0.1f, 0.7f))
                    })
            ))
        }

        background = skin.get(PIXMAP_KEY, TextureRegionDrawable::class.java)

        this += label("[#FF0000]Pause[]", LabelStyles.LARGE.name) { labelCell ->
            labelCell.expand().fill()
            this.setAlignment(Align.center)
        }
    }

    companion object {
        private const val PIXMAP_KEY = "pauseTexturePixmap"
    }
}

@Scene2dDsl
fun <S> KWidget<S>.pauseView(
    skin: Skin,
    init: PauseView.(S) -> Unit = {}
): PauseView = actor(PauseView(skin), init)
