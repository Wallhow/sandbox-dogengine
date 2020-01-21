package sandbox.go.environment.drop

import com.badlogic.ashley.core.Entity
import dogengine.ecs.components.create
import sandbox.sandbox.def.def.comp.CDrop
import sandbox.sandbox.go.AGameObject
import sandbox.sandbox.go.items.ItemID
import sandbox.sandbox.go.items.ObjectList

abstract class ADropOnMap(override val dropID: ObjectList,
                          var horizontalLine: Float = 0f) : ItemID,AGameObject(dropID.name_res) {
    override val entity: Entity get() = this
    protected fun createCDrop(timeFly: Float, hLine: Float = horizontalLine) {
        create<CDrop> {
            time = timeFly
            y = hLine
            itemID = this@ADropOnMap
        }
    }

}