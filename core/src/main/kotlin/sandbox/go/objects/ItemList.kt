package sandbox.sandbox.go.objects

import com.badlogic.gdx.utils.ArrayMap
import sandbox.sandbox.go.objects.Counter.nextId

enum class ItemList(val resourcesName: String, val id: Int,val buildType: ObjectList? = null) {
    CANDY("candy_item", nextId()),
    ZERO("null", nextId()),
    WOOD("wood_item", nextId()),
    GRASS("grass_item", nextId()),
    ROCK("rock_item", nextId()),
    SANDSTONE("sandstone_item", nextId()),
    WOOD_PLANK("plank_item",nextId()),
    WORKBENCH("workbench_object",nextId(), ObjectList.WORKBENCH),
    BONFIRE("bonfire_object",nextId(), ObjectList.BONFIRE1),
    APPLE("apple_item",nextId()),
    APPLE_EAT("apple_eat_item",nextId()),
    BRANCH("branch_item",nextId())
}


object Counter {
    private data class CounterDef(var currentId: Int = -2)
    private val arrayCounters: ArrayMap<Any, CounterDef> = ArrayMap()

    fun Any.nextId(): Int {
        if (arrayCounters[this] == null) {
            arrayCounters.put(this, CounterDef())
        }
        arrayCounters[this].currentId += 1
        return arrayCounters[this].currentId
    }
}
