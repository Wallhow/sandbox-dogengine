package sandbox.dev.world

import com.badlogic.gdx.utils.ArrayMap
import sandbox.sandbox.go.objects.ItemList
import sandbox.dev.ecs.interfaces.IBuilder
import sandbox.sandbox.go.objects.ObjectList

class Builders {
    private val dropArray : ArrayMap<ItemList, IBuilder> = ArrayMap()
    private val constructArray : ArrayMap<ObjectList, IBuilder> = ArrayMap()

    operator fun get(itemType: ItemList) = dropArray[itemType]
    operator fun get(objectType: ObjectList) = constructArray[objectType]

    fun addBuilder(type: ItemList, builder: IBuilder) {
        dropArray.put(type,builder)
    }
    fun addBuilder(type: ObjectList, builder: IBuilder) {
        constructArray.put(type,builder)
    }

}