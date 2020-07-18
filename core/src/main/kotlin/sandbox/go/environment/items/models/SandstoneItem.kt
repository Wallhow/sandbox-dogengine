package sandbox.go.environment.items.models

import com.badlogic.gdx.math.Vector2
import dogengine.utils.Size
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.items.Shadow
import sandbox.sandbox.go.objects.ItemList

class SandstoneItem(pos: Vector2, h: Float) : AItemOnMap(ItemList.SANDSTONE,h) {
    init {
        createCTransform(pos, Size(24f,24f))
        createCAtlasRegion(itemType.resourcesName)
        createCDrop(0.35f)
        createCUpdate {  }
        engine.addEntity(Shadow(this))
    }
}