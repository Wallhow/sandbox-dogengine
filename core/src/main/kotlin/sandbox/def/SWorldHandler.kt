package sandbox.def

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.log
import sandbox.go.environment.ItemList
import sandbox.go.environment.ItemCreatorOnMap
import sandbox.go.environment.ItemData
import java.util.*

class SWorldHandler: EntitySystem() {
    init {
        priority = SystemPriority.BEFORE_UPDATE-1
    }

    override fun update(deltaTime: Float) {
        if(!stackItemData.empty()) {
            val itemData = stackItemData.pop()
            val itemObj = ItemCreatorOnMap.create(itemData)
            if(itemObj!=null) {
                engine.addEntity(itemObj)
                ItemData.free(itemData)
            }
            else log("not can create item on map")
        }
        super.update(deltaTime)
    }

    companion object {
        private val stackItemData : Stack<ItemData> = Stack()
        fun getStackItemData() : Stack<ItemData> = stackItemData
        fun addItemOnMap(type: ItemList, position: Vector2) {
            stackItemData.push(ItemData.obtain().apply {
                this.position.set(position)
                this.type = type
            })
        }
    }
}