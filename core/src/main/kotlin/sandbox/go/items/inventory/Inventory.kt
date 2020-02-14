package sandbox.sandbox.go.items.inventory


import com.badlogic.gdx.utils.ArrayMap
import sandbox.sandbox.go.items.ItemID
import sandbox.sandbox.go.items.ObjectList
import sandbox.sandbox.go.player.Player
import java.util.*

class Inventory (private val player: Player) {
    private val arrayItemID: ArrayMap<Int,Stack<ItemID>> = ArrayMap(8)

    fun readAll() : com.badlogic.gdx.utils.Array<Pair<ObjectList,Int>> {
        val arrayMap: com.badlogic.gdx.utils.Array<Pair<ObjectList,Int>> = com.badlogic.gdx.utils.Array()
        arrayItemID.forEach { stack ->
            arrayMap.add(Pair(stack.value.peek().dropID,stack.value.size))
        }
        return arrayMap
    }
    fun push(itemID: ItemID) {
        if(arrayItemID[itemID.dropID.id] == null) {
            val stack = Stack<ItemID>()
            stack.push(itemID)
            arrayItemID.put(itemID.dropID.id,stack)
        } else {
            arrayItemID[itemID.dropID.id].push(itemID)
        }
    }



}
