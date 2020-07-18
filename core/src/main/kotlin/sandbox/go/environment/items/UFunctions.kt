package sandbox.go.environment.items

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import dogengine.ecs.components.utility.logic.CTransforms
import sandbox.def.def.sys.SWorldHandler
import sandbox.sandbox.go.objects.ItemList

fun Entity.dropOnMap(minCount: Int = 1, maxCount: Int = minCount + 1, drop: ItemList = ItemList.ZERO) {
    val size = CTransforms[this].size
    val count = MathUtils.random(minCount, maxCount)
    if (count == 0) return
    for (i in 1..count) {
        val pos = CTransforms[this].position.cpy()
        pos.add(MathUtils.random(size.halfWidth - 10f, size.halfWidth + 10f), MathUtils.random(6f, size.halfHeight))
        SWorldHandler.worldManager.createItem(drop,pos)
    }
}