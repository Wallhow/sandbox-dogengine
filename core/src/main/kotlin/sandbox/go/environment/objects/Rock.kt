package sandbox.go.environment.objects

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.dev.ecs.comp.CHealth
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.go.environment.items.dropOnMap
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.objects.ObjectList

class Rock(position: Vector2) : AGameObjectOnMap(objectType = ObjectList.ROCK) {
    private val itemType = ItemList.ROCK
    private var currentIdx = 0

    init {
        createCAtlasRegion()
        val tex = getAtlasRegion()
        createCTransform(position, Size(tex.regionWidth.toFloat(), tex.regionHeight * 1f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCUpdate {
            val idx = 6 - (CHealth[this@Rock].health / 3).toInt()
            CAtlasRegion[this@Rock].index = if (idx < 5) {
                idx
            } else 5
        }
        createCHealth(15f,itemType = itemType)
        CHealth[this].shot = {
            if (currentIdx != CAtlasRegion[this@Rock].index) {
                currentIdx = CAtlasRegion[this@Rock].index
                dropOnMap(0, 1, itemType)
            }

        }
    }
}