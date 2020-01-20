package sandbox.go.items.onmap.models

import com.badlogic.gdx.math.Vector2
import dogengine.utils.Size
import sandbox.go.items.onmap.AbstractItemMap
import sandbox.sandbox.go.items.Items

class WoodItemMap(pos: Vector2,h: Float) : AbstractItemMap(Items.WOOD.id,h) {
    init {
        createCTransform(pos,Size(24f,24f))
        createCAtlasRegion(Items.WOOD.name_res)
        createCDrop(0.5f)
        createCPhysicsDef()
    }
}