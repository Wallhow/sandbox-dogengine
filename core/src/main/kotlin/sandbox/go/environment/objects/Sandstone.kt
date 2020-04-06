package sandbox.sandbox.go.environment.models

import com.badlogic.gdx.math.Vector2
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.go.environment.ObjectList

class Sandstone(position : Vector2) : AGameObjectOnMap(ObjectList.SANDSTONE.nameMainObj) {
    override var itemType: ObjectList = ObjectList.SANDSTONE
    init {
        createCAtlasRegion()
        val tex = getAtlasRegion()
        createCTransform(position, Size(tex.regionWidth.toFloat() ,tex.regionHeight * 1f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCHealth(10f,4)
    }
}