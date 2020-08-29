package dogengine.ecs.components.draw

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CAtlasRegion : PoolableComponent {
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