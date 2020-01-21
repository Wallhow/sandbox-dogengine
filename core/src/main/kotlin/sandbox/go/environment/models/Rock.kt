package sandbox.sandbox.go.environment.models

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.go.environment.drop.models.RockDrop
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.sandbox.go.environment.AGOMap
import sandbox.sandbox.go.items.ObjectList

class Rock(position : Vector2) : AGOMap("rock") {
    override val dropID: ObjectList = ObjectList.ROCK
    init {
        createCAtlasRegion()
        val tex = CAtlasRegion[this@Rock].atlas!!.findRegion(CAtlasRegion[this@Rock].nameRegion)
        createCTransform(position,Size(tex.regionWidth.toFloat() ,tex.regionHeight * 1f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCUpdate {
            val idx = 6-(CHealth[this@Rock].health/3).toInt()
            CAtlasRegion[this@Rock].index = if(idx<5) idx else 5
        }
        createCHealth(15f) { befDead() }
    }
    private fun befDead()  {
        deleteMe()
        val size = CTransforms[this].size
        val count = MathUtils.random(3,4)
        for (i in 1..count) {
            val pos = CTransforms[this].position.cpy()
            pos.add(MathUtils.random(size.halfWidth-10f,size.halfWidth+10f), MathUtils.random(6f,size.halfHeight))
            engine.addEntity(RockDrop(pos, CTransforms[this].position.y))
        }
    }
}