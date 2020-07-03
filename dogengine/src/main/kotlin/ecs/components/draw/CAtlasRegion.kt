package dogengine.ecs.components.draw

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CAtlasRegion : Component, Pool.Poolable{
    var atlas: TextureAtlas? = null
    var nameRegion: String? = null
    var drawLayer: CDraw.DrawLayer = CDraw.DrawLayer.NO_EFFECT
    var index = -1
    var color: Color = Color.WHITE.cpy()
    val padding = Vector2()
    override fun reset() {
        atlas = null
        nameRegion = null
        drawLayer = CDraw.DrawLayer.NO_EFFECT
        index = -1
        color = Color.WHITE.cpy()
        padding.setZero()
    }

    companion object : ComponentResolver<CAtlasRegion>(CAtlasRegion::class.java)
}