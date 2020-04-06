package sandbox.go.environment.items.models

import com.badlogic.gdx.math.Vector2
import dogengine.utils.Size
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.items.Shadow
import sandbox.go.environment.ObjectList

class GrassItem(pos:Vector2, h:Float): AItemOnMap(ObjectList.GRASS,h) {
    init {
        createCTransform(pos,Size(24f,24f))
        createCAtlasRegion(itemType.name_res)
        createCDrop(0.75f)
        createCUpdate {  }
        engine.addEntity(Shadow(this))
    }
}