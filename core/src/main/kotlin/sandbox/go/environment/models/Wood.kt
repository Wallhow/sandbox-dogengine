package sandbox.sandbox.go.environment.models

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.go.environment.drop.models.GrassDrop
import sandbox.go.environment.drop.models.WoodDrop
import sandbox.sandbox.go.environment.AGOMap
import sandbox.sandbox.go.items.ObjectList

class Wood (position : Vector2) : AGOMap("wood") {
    override val dropID: ObjectList = ObjectList.WOOD
    init {
        createCAtlasRegion()
        val tex = CAtlasRegion[this@Wood].atlas!!.findRegion(CAtlasRegion[this@Wood].nameRegion)
        createCTransform(position,Size(tex.regionWidth * 2f,tex.regionHeight * 2f))
        val t = CTransforms[this]
        createCPhysicsDef(t.size.width/2f-(t.size.width / 6.5f)/2, 0f, t.size.width / 6.5f, t.size.height / 10,sensor = false,type = Types.TYPE.DYNAMIC)
        createCHealth(8f) { befDead() }
    }
    private fun befDead()  {
        this.create<CDeleteMe>()
        val size = CTransforms[this].size
        var count = MathUtils.random(3,4)
        for (i in 1..count) {
            val pos = CTransforms[this].position.cpy()
            pos.add(MathUtils.random(size.halfWidth-10f,size.halfWidth+10f), MathUtils.random(6f,size.halfHeight))
            engine.addEntity(WoodDrop(pos, CTransforms[this].position.y))
        }
        count = MathUtils.random(3,5)
        for (i in 1..count) {
            val pos = CTransforms[this].position.cpy()
            pos.add(MathUtils.random(size.halfWidth-10f,size.halfWidth+10f), MathUtils.random(6f,size.halfHeight))
            engine.addEntity(GrassDrop(pos, CTransforms[this].position.y))
        }
    }
}