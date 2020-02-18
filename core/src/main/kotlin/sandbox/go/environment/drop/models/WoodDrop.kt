package sandbox.go.environment.drop.models

import com.badlogic.gdx.math.Vector2
import dogengine.utils.Size
import sandbox.go.environment.drop.ADropOnMap
import sandbox.sandbox.go.environment.drop.Shadow
import sandbox.sandbox.go.items.ObjectList

class WoodDrop(pos: Vector2, h: Float) : ADropOnMap(ObjectList.WOOD,h) {
    init {
        createCTransform(pos,Size(24f,24f))
        createCAtlasRegion(dropID.name_res)
        createCDrop(0.5f)
        createCUpdate {  }
        engine.addEntity(Shadow(this))
    }
}