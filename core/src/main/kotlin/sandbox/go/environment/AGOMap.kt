package sandbox.sandbox.go.environment

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.logic.CTransforms
import sandbox.go.environment.drop.ADropOnMap
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.sandbox.go.AGameObject
import sandbox.sandbox.go.items.ObjectList

abstract class AGOMap (name: String) : AGameObject(name) {
    abstract override val dropID: ObjectList
    protected fun createCHealth(maxHealth : Float, beforeDeadFunc: () -> Unit) {
        create<CHealth> {
            health = maxHealth
            beforeDead = beforeDeadFunc
        }
    }

    protected inline fun <reified T: ADropOnMap> Entity.dropOnMap(minCount:Int = 1, maxCount: Int = minCount+1) {
        val size = CTransforms[this].size
        val count = MathUtils.random(minCount,maxCount)
        val drop = T::class.java
        if (count==0) return
        for (i in 1..count) {
            val pos = CTransforms[this].position.cpy()
            pos.add(MathUtils.random(size.halfWidth-10f,size.halfWidth+10f), MathUtils.random(6f,size.halfHeight))
            engine.addEntity(drop.constructors[0].newInstance(pos, CTransforms[this].position.y) as Entity)
        }
    }
}