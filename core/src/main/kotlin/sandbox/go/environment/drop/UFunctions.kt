package sandbox.sandbox.go.environment.drop

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import dogengine.ecs.components.utility.logic.CTransforms
import sandbox.def.SWorldHandler
import sandbox.go.player.inventory.Inventory.ZeroItem.Companion.dropID
import sandbox.sandbox.def.def.comp.DropConfig
import sandbox.sandbox.go.items.ObjectList

fun Entity.dropOnMap(minCount: Int = 1, maxCount: Int = minCount + 1, drop: ObjectList = dropID) {
    val size = CTransforms[this].size
    val count = MathUtils.random(minCount, maxCount)
    if (count == 0) return
    for (i in 1..count) {
        val pos = CTransforms[this].position.cpy()
        pos.add(MathUtils.random(size.halfWidth - 10f, size.halfWidth + 10f), MathUtils.random(6f, size.halfHeight))
        SWorldHandler.worldEventDrop.stackDrop.push(DropConfig(drop, pos))
    }
}