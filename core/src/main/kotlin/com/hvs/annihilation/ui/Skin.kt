package com.hvs.annihilation.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.assets.disposeSafely
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin

enum class Drawables(val atlasKey: String) {
    MENU_BACKGROUND("menu_bgd"),
    BAR_BACKGROUND("bar"),
    BAR_GREEN("bar_green"),
    PLAYER_INFO_BACKGROUND("char_info"),
    PLAYER("player"),
    LIFE_BAR("life_bar"),
    MANA_BAR("mana_bar"),
    HELMET("helmet")
}

enum class LabelStyles {
    MAP_INFO,
    LARGE
}

enum class FontType(val skinKey: String) {
    DEFAULT("defaultFont"),
    LARGE("largeFont")
}

operator fun Skin.get(drawable: Drawables): Drawable = this.getDrawable(drawable.atlasKey)

private fun getBitmapFont(fntName: String, atlas: TextureAtlas) =
    BitmapFont(Gdx.files.internal("ui/$fntName.fnt"), atlas.findRegion(fntName)).apply {
        data.markupEnabled = true
    }

fun createSkin(atlas: TextureAtlas): Skin {
    Scene2DSkin.defaultSkin = skin(atlas) { skin ->

        // fonts
        add(FontType.DEFAULT.skinKey, getBitmapFont("font24", atlas))
        add(FontType.LARGE.skinKey, getBitmapFont("font32", atlas))

        // default label style
        label { font = skin.getFont(FontType.DEFAULT.skinKey) }
        label(LabelStyles.LARGE.name) { font = skin.getFont(FontType.LARGE.skinKey) }
        label(LabelStyles.MAP_INFO.name) {
            font = skin.getFont(FontType.LARGE.skinKey)
        }
    }

    return Scene2DSkin.defaultSkin
}

fun disposeSkin() {
    Scene2DSkin.defaultSkin.disposeSafely()
}
