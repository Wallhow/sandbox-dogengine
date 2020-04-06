package sandbox.sandbox.def

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import sandbox.go.environment.ObjectList
import sandbox.go.environment.items.models.*

object ItemCreatorOnMap {
    //TODO Тут централизованное создание дроп Объектов
    fun create(itemData: ItemData): Entity? {
        val pos = itemData.position
        return when (itemData.type) {
            ObjectList.GRASS -> {
                GrassItem(pos, pos.y)
            }
            ObjectList.WOOD -> {
                WoodItem(pos, pos.y)
            }
            ObjectList.SANDSTONE -> {
                SandstoneItem(pos, pos.y)
            }
            ObjectList.ROCK -> {
                RockItem(pos, pos.y)
            }
            ObjectList.CANDY -> {
                CandyItem(pos)
            }
            else -> null
        }
    }
}

data class ItemData(var type: ObjectList, var position: Vector2) : Pool.Poolable {
    override fun reset() {
        type = ObjectList.ZERO
        position.setZero()
    }

    companion object : Pool<ItemData>() {
        override fun newObject(): ItemData {
            return ItemData(ObjectList.ZERO,Vector2.Zero.cpy())
        }
    }
}