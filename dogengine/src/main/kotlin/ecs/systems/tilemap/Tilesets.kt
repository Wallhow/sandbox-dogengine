package dogengine.ecs.systems.tilemap

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.ArrayMap
import dogengine.utils.Size
import dogengine.utils.log

class Tilesets {
    private val tileset: ArrayMap<Int, TextureAtlas.AtlasRegion> = ArrayMap()
    var tileSize: Size = Size(32f,32f)


    fun createTileSet(tileSize: Size,func : (ArrayMap<Int, TextureAtlas.AtlasRegion>) -> Unit) {
        this.tileSize = tileSize
        func.invoke(tileset)
    }


    fun getTile(index: Int) : TextureAtlas.AtlasRegion = tileset[index]
}