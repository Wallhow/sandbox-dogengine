package sandbox.go.player.inventory



import com.badlogic.gdx.math.MathUtils
import dogengine.ecs.components.utility.logic.CTransforms
import sandbox.def.SWorldHandler
import sandbox.sandbox.def.def.comp.DropConfig
import sandbox.sandbox.go.items.ItemID
import sandbox.sandbox.go.items.ObjectList
import sandbox.sandbox.go.player.Player

class Inventory(private val player: Player, val size: Int = 12) {
    private val arr: kotlin.Array<out InvItem> = kotlin.Array(size) { InvItem() }

    fun push(itemID: ItemID, count: Int = 1): Boolean {
        arr.findLast {it.itemID.dropID==itemID.dropID}.let { it ->
            it.ifNull {
                arr.find {it.itemID==ZeroItem}.let {it1 ->
                    it1.ifNull {
                        return false
                    }
                    it1.apply {
                        this!!.count = count
                        this.itemID = itemID
                        return true
                    }
                }
            }
            it!!.count += count
            return true
        }
    }

    fun readAll(): kotlin.Array<out InvItem> {
        return arr
    }

    fun dropCurrentItem() {

        val pos = CTransforms[player].position.cpy()
        pos.add(MathUtils.random(CTransforms[player].size.halfWidth - 10f,
                CTransforms[player].size.halfWidth + 10f),
                MathUtils.random(6f, CTransforms[player].size.halfHeight))

        if (currentItem < arr.size && arr[currentItem].itemID != ZeroItem) {
            val d = arr[currentItem]
            //кидаем в стек объект для дропа в мир из инвентаря
            SWorldHandler.worldEventDrop.stackDrop.push(DropConfig(d.itemID.dropID,pos))
            d.count -= 1
            if (d.isEmpty()) {
                arr[currentItem].count = -1
                arr[currentItem].itemID = ZeroItem
            }

        }
    }

    var currentItem: Int = 0

    data class InvItem(var itemID: ItemID = ZeroItem, var count: Int = -1) {
        fun isEmpty(): Boolean {
            return count<=0
        }

        fun setZero() {
            itemID = ZeroItem
            count = -1
        }
    }

    class ZeroItem{
        companion object : ItemID {
            override val dropID: ObjectList
                get() = ObjectList.ZERO
        }
    }
}

private inline fun Inventory.InvItem?.ifNull(function: () -> Unit) {
    if(this==null) {
        function.invoke()
    }
}
