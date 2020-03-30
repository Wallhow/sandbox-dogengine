package sandbox.sandbox.go.environment.models

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.sandbox.go.environment.AGOMap
import sandbox.go.environment.drop.models.SandstoneDrop
import sandbox.sandbox.go.environment.drop.dropOnMap
import sandbox.sandbox.go.items.ObjectList

class Sandstone(position : Vector2) : AGOMap(ObjectList.SANDSTONE.nameMainObj) {
    override val dropID: ObjectList = ObjectList.SANDSTONE
    init {
        createCAtlasRegion()
        val tex = getAtlasRegion()
        createCTransform(position, Size(tex.regionWidth.toFloat() ,tex.regionHeight * 1f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCHealth(10f,4)
    }
}