package sandbox.go.environment

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.items.models.*
import sandbox.sandbox.go.environment.AGameObjectOnMap
import sandbox.sandbox.go.environment.items.models.WoodPlankItem

object ItemCreatorOnMap {
    //TODO Тут централизованное создание дроп Объектов
    fun create(itemData: ItemData): Entity? {
        val pos = itemData.position
        return when (itemData.type) {
            ItemList.GRASS -> {
                GrassItem(pos, pos.y)
            }
            ItemList.WOOD -> {
                WoodItem(pos, pos.y)
            }
            ItemList.SANDSTONE -> {
                SandstoneItem(pos, pos.y)
            }
            ItemList.ROCK -> {
                RockItem(pos, pos.y)
            }
            ItemList.CANDY -> {
                CandyItem(pos)
            }
            ItemList.WOOD_PLANK -> {
                WoodPlankItem(pos)
            }
            else -> createDefEntity(itemData.type,pos)
        }
    }

    private fun createDefEntity(itemType: ItemList, position: Vector2) : Entity {
        return object : AItemOnMap(itemType,position.y) {
            init {
                defInit(position)
            }
        }
    }
}

data class ItemData(var type: ItemList, var position: Vector2) : Pool.Poolable {
    override fun reset() {
        type = ItemList.ZERO
        position.setZero()
    }

    companion object : Pool<ItemData>() {
        override fun newObject(): ItemData {
            return ItemData(ItemList.ZERO, Vector2.Zero.cpy())
        }
    }
}