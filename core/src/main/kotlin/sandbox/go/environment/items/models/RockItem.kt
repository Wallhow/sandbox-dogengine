package sandbox.go.environment.items.models

import com.badlogic.gdx.math.Vector2
import dogengine.utils.Size
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.items.Shadow
import sandbox.go.environment.ItemList

class RockItem(pos: Vector2, h: Float) : AItemOnMap(ItemList.ROCK,h) {
    init {
        createCTransform(pos, Size(24f,24f))
        createCAtlasRegion(itemType.resourcesName)
        createCUpdate {}
        createCDrop(0.25f)
        engine.addEntity(Shadow(this))
    }
}
