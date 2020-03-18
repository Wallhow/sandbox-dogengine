package sandbox.go.player.inventory


import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import sandbox.go.environment.drop.ADropOnMap
import sandbox.go.environment.drop.models.GrassDrop
import sandbox.go.environment.drop.models.RockDrop
import sandbox.go.environment.drop.models.SandstoneDrop
import sandbox.go.environment.drop.models.WoodDrop
import sandbox.sandbox.go.items.ItemID
import sandbox.sandbox.go.items.ObjectList
import sandbox.sandbox.go.player.Player
import java.util.*

class Inventory(private val player: Player) {
    private val arrayItemID: ArrayMap<Int, Stack<ItemID>> = ArrayMap(8)
    private val itemsIDs: Array<Int> = Array(8)
    var currentItem: Int = 0
    fun readAll(): Array<Pair<ObjectList, Int>> {
        val arrayMap: Array<Pair<ObjectList, Int>> = Array()
        arrayItemID.forEach { stack ->
            if (!stack.value.isEmpty())
                arrayMap.add(Pair(stack.value.peek().dropID, stack.value.size))
        }
        return arrayMap
    }

    fun push(itemID: ItemID) {
        if (arrayItemID[itemID.dropID.id] == null) {
            val stack = Stack<ItemID>()
            stack.push(itemID)
            arrayItemID.put(itemID.dropID.id, stack)
            itemsIDs.add(itemID.dropID.id)
        } else {
            arrayItemID[itemID.dropID.id].push(itemID)
        }
    }

    fun dropCurrentItem() {

        val pos = CTransforms[player].position.cpy()
        pos.add(MathUtils.random(CTransforms[player].size.halfWidth - 10f,
                CTransforms[player].size.halfWidth + 10f),
                MathUtils.random(6f, CTransforms[player].size.halfHeight))
        val engine = Kernel.getInjector().getInstance(Engine::class.java)

        //TODO переделать, обязательно как-то центролизовать то, что находится ниже
        if (currentItem < itemsIDs.size && itemsIDs[currentItem] != null) {
            val d = arrayItemID[itemsIDs[currentItem]]
            if (!d.isEmpty()) {
                when (d.peek().dropID) {
                    ObjectList.GRASS -> {
                        d.pop()
                        engine.addEntity(GrassDrop(pos, pos.y))
                    }
                    ObjectList.WOOD -> {
                        d.pop()
                        engine.addEntity(WoodDrop(pos, pos.y))
                    }
                    ObjectList.SANDSTONE -> {
                        d.pop()
                        engine.addEntity(SandstoneDrop(pos, pos.y))
                    }
                    ObjectList.ROCK -> {
                        d.pop()
                        engine.addEntity(RockDrop(pos, pos.y))
                    }
                }
                if(d.isEmpty()) {
                    arrayItemID.removeValue(d,true)
                    itemsIDs.removeIndex(currentItem)
                }
            }
        }
    }


}
