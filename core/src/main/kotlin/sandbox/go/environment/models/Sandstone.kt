package sandbox.sandbox.go.environment.models

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.sandbox.go.environment.AGOMap
import sandbox.go.environment.drop.models.SandstoneDrop
import sandbox.sandbox.go.items.ObjectList

class Sandstone(position : Vector2) : AGOMap(ObjectList.SANDSTONE.nameMainObj) {
    override val dropID: ObjectList = ObjectList.SANDSTONE
    init {
        createCAtlasRegion()
        val tex = CAtlasRegion[this].atlas!!.findRegion(CAtlasRegion[this].nameRegion)
        createCTransform(position, Size(tex.regionWidth.toFloat() ,tex.regionHeight * 1f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCHealth(10f) { befDead() }
    }
    private fun befDead()  {
        deleteMe()
        val size = CTransforms[this].size
        val count = MathUtils.random(3,4)
        for (i in 1..count) {
            val pos = CTransforms[this].position.cpy()
            pos.add(MathUtils.random(size.halfWidth-10f,size.halfWidth+10f), MathUtils.random(6f,size.halfHeight))
            engine.addEntity(SandstoneDrop(pos, CTransforms[this].position.y))
        }
    }
}