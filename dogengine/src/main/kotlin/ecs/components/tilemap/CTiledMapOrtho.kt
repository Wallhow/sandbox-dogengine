package dogengine.ecs.components.tilemap

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.maps.tiled.TiledMap
import dogengine.ecs.def.ComponentResolver

class CTiledMapOrtho(val tmxMapPath: String?) : Component {
    var tiledMap: TiledMap? = null
companion object : ComponentResolver<CTiledMapOrtho>(CTiledMapOrtho::class.java)
}
