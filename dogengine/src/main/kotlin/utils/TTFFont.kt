package dogengine.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.*

/**
 * Created by wallhow on 18.11.16.
 */
class TTFFont(path : String? = null) {
    private val fonts = ArrayMap<String,BitmapFont>()
    private val gen: FreeTypeFontGenerator
    private val generatorConf = FreeTypeFontGenerator.FreeTypeFontParameter()
    private val FONT_CHARS = "абвгдежзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>"
    init {
        generatorConf.color = Color.WHITE
        gen = if(path!=null) { FreeTypeFontGenerator((Gdx.files.internal(path))) }
        else { FreeTypeFontGenerator((Gdx.files.internal("assets/def_fonts.ttf"))) }
    }

    fun create(size: Int, color: Color, key: String = "$size") : TTFFont {
        if(fonts.get(key) == null) {
            generatorConf.size = size
            generatorConf.color = color
            generatorConf.characters = FONT_CHARS
            val font = gen.generateFont(generatorConf)

            fonts.put(key,font)
        }
        return this
    }

    fun get(size : Int, key: String = "$size"): BitmapFont {
        return if(fonts.get(key) == null) {
            generatorConf.size = size
            generatorConf.characters = FONT_CHARS
            val font = gen.generateFont(generatorConf)
            fonts.put(key,font)
            font
        }
        else {
            fonts.get(key)
        }
    }

    fun dispose() {
        fonts.values().forEach(BitmapFont::dispose)
    }

}