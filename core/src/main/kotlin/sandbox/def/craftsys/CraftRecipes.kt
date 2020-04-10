package sandbox.def.craftsys

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import dogengine.utils.log
import sandbox.go.player.inventory.Inventory
import sandbox.go.environment.ItemList
import sandbox.sandbox.go.environment.ObjectList

/**
 * Список возможных рецептов с их условиями
 */
class CraftRecipes(val inv: Inventory) {
    private val listRecipe: ArrayMap<String, CraftRecipe> = ArrayMap()

    init {
        listRecipe.put("test", CraftRecipe("мусор",
                " Это кошачья лапка, или енота, черт знает чья это лапа, но ею можно кидаться",
                arrayOf(Pair(ItemList.ROCK, 3), Pair(ItemList.WOOD, 1)),
                rules {

                })
                .craftItem(ItemList.CANDY))

        listRecipe.put("test2", CraftRecipe("доска",
                "",
                arrayOf(Pair(ItemList.WOOD, 2)),
                rules {
                    create { isNearWorkbench(ObjectList.WORKBENCH) }
                })
                .craftItem(ItemList.WOOD_PLANK))
    }

    fun getRecipes(): Array<CraftRecipe> = listRecipe.values().toArray()
    fun getAvailableRecipes(bloc: (recipe: CraftRecipe) -> Unit) {

        listRecipe.forEach {
            if(isAvailableRecipe(it.value)) {
                bloc.invoke(it.value)
            }
        }
    }
    fun isAvailableRecipe(recipe: CraftRecipe) : Boolean {
        var result = true
        recipe.needItems.forEach {need ->
            //если одного из инг-тов нет, то выходим из проверки с отр. результатом
            if (!recipe.isAvailable() || !isContainCount(need.first, need.second) ) {
                result = false
                return@forEach
            }
        }
        return result
    }

    private fun rules(arr: Rules.() -> Unit): Array<() -> Boolean> {
        val rules = Rules()
        arr.invoke(rules)
        return rules.array
    }
    private class Rules(val array: Array<() -> Boolean> = Array())

    private fun Rules.create(rule: () -> Boolean) {
        this.array.add(rule)
    }

    private fun CraftRecipe.craftItem(type: ItemList): CraftRecipe {
        itemCraft = type
        return this
    }

    private inline fun isContainCount(id: ItemList, func: (countItem: Int) -> Boolean): Boolean {
        return if (inv.contain(id) == -1) false
        else func.invoke(inv.readAll()[inv.contain(id)].count)
    }

    private fun isContainCount(id: ItemList, countItem: Int): Boolean {
        return if (inv.contain(id) == -1) false
        else inv.readAll()[inv.contain(id)].count >= countItem
    }

    private fun isNearWorkbench(type: ObjectList): Boolean {
        return if (inv.player.workbenchNear[type] != null) {
            inv.player.workbenchNear[type]
        } else false
    }

}