package sandbox.def.craftsys

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import sandbox.go.player.inventory.Inventory
import sandbox.go.environment.ObjectList

/**
 * Список возможных рецептов с их условиями
 */
class CraftRecipes(val inv: Inventory) {
    private val listRecipe: ArrayMap<String, CraftRecipe> = ArrayMap()

    init {
        listRecipe.put("test", CraftRecipe("candy2",
                " конфет из булыжника не получится, извини :D",
                { isContainCount(ObjectList.ROCK, 3) },
                { isContainCount(ObjectList.WOOD, 1) }))
    }

    fun getRecipes() : Array<CraftRecipe> = listRecipe.values().toArray()

    private inline fun isContainCount(id: ObjectList, func: (countItem: Int) -> Boolean): Boolean {
        return if (inv.contain(id) == -1) false
        else func.invoke(inv.readAll()[inv.contain(id)].count)
    }
    private fun isContainCount(id: ObjectList, countItem: Int): Boolean {
        return if (inv.contain(id) == -1) false
        else inv.readAll()[inv.contain(id)].count>=countItem
    }

}