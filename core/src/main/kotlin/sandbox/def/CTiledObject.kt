package sandbox.def

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.ArrayMap
import dogengine.def.ComponentResolver

class CTiledObject : Component {
    companion object : ComponentResolver<CTiledObject>(CTiledObject::class.java)
    val properties: ArrayMap<Properties,String> = ArrayMap()
    enum class Properties{
        Alert
    }
}