package sandbox.sandbox.go.environment.models

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.go.environment.items.dropOnMap
import sandbox.go.environment.ObjectList

class Wood (position : Vector2) : AGameObjectOnMap("wood") {
    override var itemType: ObjectList = ObjectList.WOOD
    init {
        createCAtlasRegion()
        val tex = getAtlasRegion()
        createCTransform(position,Size(tex.regionWidth * 2f,tex.regionHeight * 2f))
        val t = CTransforms[this]
        createCPhysicsDef(t.size.width/2f-(t.size.width / 6.5f)/2, 0f, t.size.width / 6.5f, t.size.height / 10,sensor = false,type = Types.TYPE.DYNAMIC)
        createCHealth(8f,itemType = null) { befDead() }
    }
    private fun befDead()  {
        dropOnMap(3,5, ObjectList.GRASS)
        dropOnMap(3,4, ObjectList.WOOD)
    }
}