package sandbox.go.player.inventory



import com.badlogic.gdx.math.MathUtils
import dogengine.ecs.components.utility.logic.CTransforms
import sandbox.def.SWorldHandler
import sandbox.go.environment.ItemList
import sandbox.sandbox.go.player.Player

class Inventory(val player: Player, val size: Int = 12) {
    private val arr: Array<out InvItem> = Array(size) { InvItem() }

    fun push(itemID: ItemList, count: Int = 1): Boolean {
        arr.findLast {it.itemID==itemID}.let { it ->
            it.ifNull {
                arr.find {it.itemID== ItemList.ZERO}.let { it1 ->
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

    fun pop(itemID: ItemList, count: Int = 1) {
        if(contain(itemID)!=-1)
            pop(contain(itemID),count)
    }
    fun pop(count: Int=1) {
        pop(currentItem,count)
    }
    fun pop(index: Int,count: Int = 1) {
        arr[index].count -=count
    }

    fun readAll(): Array<out InvItem> = arr

    fun dropCurrentItem() {

        val pos = CTransforms[player].position.cpy()
        pos.add(MathUtils.random(CTransforms[player].size.halfWidth - 10f,
                CTransforms[player].size.halfWidth + 10f),
                MathUtils.random(6f, CTransforms[player].size.halfHeight))

        if (currentItem < arr.size && arr[currentItem].itemID != ItemList.ZERO) {
            val d = arr[currentItem]
            //кидаем в стек объект для дропа в мир из инвентаря
            SWorldHandler.addItemOnMap(d.itemID,pos)
            d.count -= 1
            if (d.isEmpty()) {
                arr[currentItem].setZero()
            }

        }
    }

    //Возвращает индекс в инвентаре, либо -1 если не найдено
    fun contain(itemID: ItemList) : Int {
        var idx = -1
        arr.forEachIndexed {index, invItem ->
            if(invItem.itemID === itemID) {
                idx = index
                return@forEachIndexed
            }
        }
        return idx
    }
    var currentItem: Int = 0

    data class InvItem(var itemID: ItemList = ItemList.ZERO, var count: Int = -1) {
        fun isEmpty(): Boolean {
            return count<=0
        }
        fun setZero() {
            itemID = ItemList.ZERO
            count = -1
        }
    }
}

private inline fun Inventory.InvItem?.ifNull(function: () -> Unit) {
    if(this==null) {
        function.invoke()
    }
}
