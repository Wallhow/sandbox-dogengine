package dogengine.drawcore

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CAtlasRegions : PoolableComponent {
    companion object : ComponentResolver<CAtlasRegions>(CAtlasRegions::class.java)
    var regions: Array<TextureAtlas.AtlasRegion> = Array()
    val directionDraw: Vector2 = Vector2()

    override fun reset() {
        regions.clear()
        directionDraw.setZero()
    }
}