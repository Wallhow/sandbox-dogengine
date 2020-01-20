package sandbox.sandbox.go.items.inventory


import com.badlogic.gdx.utils.ArrayMap
import sandbox.sandbox.go.Player
import sandbox.sandbox.go.items.Item
import sandbox.sandbox.go.items.Items
import java.util.*

class Inventory (private val player: Player) {
    private val arrayItem: ArrayMap<Int,Stack<Item>> = ArrayMap(8)

    fun readAll() : com.badlogic.gdx.utils.Array<Pair<Items,Int>> {
        val arrayMap: com.badlogic.gdx.utils.Array<Pair<Items,Int>> = com.badlogic.gdx.utils.Array()
        arrayItem.forEach { stack ->
            arrayMap.add(Pair(stack.value.peek().type,stack.value.size))
        }
        return arrayMap
    }
    fun push(item: Item) {
        if(arrayItem[item.type.id] == null) {
            val stack = Stack<Item>()
            stack.push(item)
            arrayItem.put(item.type.id,stack)
        } else {
            arrayItem[item.type.id].push(item)
        }
    }



}
