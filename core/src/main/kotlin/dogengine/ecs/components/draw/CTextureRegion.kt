package dogengine.ecs.components.draw

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CTextureRegion : Component,Pool.Poolable {
    companion object : ComponentResolver<CTextureRegion>(CTextureRegion::class.java)
    var texture: TextureRegion? = null
    var color: Color = Color.WHITE.cpy()
    var drawLayer: CDrawable.DrawLayer = CDrawable.DrawLayer.NO_EFFECT
    var offsetX = 0
    var offsetY = 0
    override fun reset() {
        texture = null
        color= Color.WHITE.cpy()
        drawLayer = CDrawable.DrawLayer.NO_EFFECT
        offsetX = 0
        offsetY = 0
    }
}