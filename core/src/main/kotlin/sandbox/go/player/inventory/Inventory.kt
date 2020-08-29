package sandbox.go.player.inventory


import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.MathUtils
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.utils.extension.get
import sandbox.dev.world.MessagesType
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.player.Player
import kotlin.properties.Delegates

class Inventory(val player: Player, val size: Int = 12) {
    private val arr: Array<out InvItem> = Array(size) { InvItem() }
    var isDirty = false
    val observers: com.badlogic.gdx.utils.Array<InventoryObserver> = com.badlogic.gdx.utils.Array()
    private val observableItem = ObservableItem(observers)
    private val messenger = Kernel.getInjector()[MessageManager::class.java]

    fun push(itemID: ItemList, count: Int = 1): Boolean {
        //ищим в массиве предмет с поступившим id
        getCellWithItem(itemID).let { invItem ->
            //если предмет не найден, то ищим пустую ячейку
            if (invItem == null) {
                getCellEmpty().let { invItem2 ->
                    //Если и пустую ячеку не находим, возвращаем false
                    if (invItem2 == null) return false
                    //если есть пустая ячека то добавляем в нее предмет и возвращаем true
                    invItem2.putItemInCell(itemID, count)
                    isDirty = true
                    observableItem.itemID = invItem2.itemID
                    observableItem.count = count
                    return true
                }
            } else {
                //Если предмет найден увеличиваем счетчик и возвращаем true
                invItem.count += count
                isDirty = true
                observableItem.itemID = invItem.itemID
                observableItem.count = invItem.count
                return true
            }
        }
    }

    private fun getCellWithItem(itemID: ItemList): InvItem? {
        return arr.find { it.itemID == itemID }
    }

    private fun getCellEmpty(): InvItem? {
        return arr.find { it.itemID == ItemList.ZERO }
    }

    private fun InvItem.putItemInCell(itemID: ItemList, count: Int) {
        this.count = count
        this.itemID = itemID
    }

    fun pop(itemID: ItemList, count: Int = 1) {
        if (contain(itemID) != -1)
            pop(contain(itemID), count)
    }

    fun pop(count: Int = 1) {
        pop(currentItem, count)
    }

    fun pop(index: Int, count: Int = 1) {
        arr[index].count -= count
        observableItem.itemID = arr[index].itemID
        observableItem.count = arr[index].count
        if (arr[index].count <= 0) arr[index].setZero()
        isDirty = true
    }

    fun readAll(): Array<out InvItem> = arr

    fun dropCurrentItem() {

        val pos = CTransforms[player].position.cpy()
        pos.add(MathUtils.random(CTransforms[player].size.halfWidth - 10f,
                CTransforms[player].size.halfWidth + 10f),
                MathUtils.random(6f, CTransforms[player].size.halfHeight))

        if (currentItem < arr.size && arr[currentItem].itemID != ItemList.ZERO) {
            val d = arr[currentItem]
            //кидаем сообщение о предмете для дропа в мир из инвентаря
            messenger.dispatchMessage(MessagesType.WORLD_DROP_ITEM_ON_MAP, mapOf(
                    1 to d.itemID,
                    2 to pos
            ))
            pop()
        }
    }

    //Возвращает индекс в инвентаре, либо -1 если не найдено
    fun contain(itemID: ItemList): Int {
        var idx = -1
        arr.forEachIndexed { index, invItem ->
            if (invItem.itemID === itemID) {
                idx = index
                return@forEachIndexed
            }
        }
        return idx
    }

    fun whatSelected(): ItemList {
        return if (currentItem != -1) arr[currentItem].itemID else ItemList.ZERO
    }

    var currentItem: Int = 0

    open class InvItem(open var itemID: ItemList = ItemList.ZERO, open var count: Int = -1) {
        fun isEmpty(): Boolean {
            return count <= 0
        }

        fun setZero() {
            itemID = ItemList.ZERO
            count = -1
        }
    }

    class ObservableItem(private val observers: com.badlogic.gdx.utils.Array<InventoryObserver>) : InvItem() {
        override var itemID: ItemList = ItemList.ZERO
        override var count: Int by Delegates.observable(-1) { _, o, n ->
            observers.forEach { it.countChanged(n, o, this) }
        }


    }
}

interface InventoryObserver {
    fun countChanged(newCount: Int, oldCount: Int, item: Inventory.InvItem)
}