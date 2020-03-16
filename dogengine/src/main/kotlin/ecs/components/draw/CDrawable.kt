package dogengine.ecs.components.draw

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CDrawable : Component, Pool.Poolable {
    companion object : ComponentResolver<CDrawable>(CDrawable::class.java)
    var isDeleteAfterDraw = false
    var texture : TextureRegion? = null
    var tint : Color = Color.WHITE.cpy()
    var drawLayer: DrawLayer = DrawLayer.NO_EFFECT
    var offsetX = 0
    var offsetY = 0
    enum class DrawLayer {
        NO_EFFECT,
        YES_EFFECT,
        GUI
    }

    override fun reset() {
        texture = null
        tint = Color.WHITE.cpy()
        drawLayer = DrawLayer.NO_EFFECT
        isDeleteAfterDraw = false
        offsetX = 0
        offsetY = 0
    }
}
fun CDrawable.c(textureRegion: TextureRegion,color: Color = Color.WHITE.cpy(),drawLayer: CDrawable.DrawLayer = CDrawable.DrawLayer.NO_EFFECT) : CDrawable {
    this.texture = textureRegion
    this.tint = color
    this.drawLayer = drawLayer
    return this
}