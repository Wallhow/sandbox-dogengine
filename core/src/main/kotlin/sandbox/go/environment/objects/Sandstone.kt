package sandbox.go.environment.objects

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.shadow2d.components.CShadow
import dogengine.utils.Size
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.objects.ObjectList

class Sandstone(position : Vector2) : AGameObjectOnMap(objectType = ObjectList.SANDSTONE) {
    private val itemType = ItemList.SANDSTONE
    init {
        createCAtlasRegion()
        val tex = getAtlasRegion()
        createCTransform(position, Size(tex.regionWidth.toFloat() ,tex.regionHeight * 1f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCHealth(10f,4,itemType)
    }
}