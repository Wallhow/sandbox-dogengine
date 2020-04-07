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
        listRecipe.put("test", CraftRecipe("мусор",
                " Это кошачья лапка, или енота, черт знает чья это лапа, но ею можно кидаться",
                arrayOf(Pair(ObjectList.ROCK,3), Pair(ObjectList.WOOD,1)),
                { isContainCount(ObjectList.ROCK, 3) },
                { isContainCount(ObjectList.WOOD, 1) }).apply { itemCraft = ObjectList.CANDY })
    }

    fun getRecipes() : Array<CraftRecipe> = listRecipe.values().toArray()

    fun getAvailableRecipes(bloc: (recipe:CraftRecipe) -> Unit){
        listRecipe.filter {
            var result = true
            //проходим по нужным дя рецепта ингр-тов и проверяем их наичие
            it.value.needItems.forEach { need ->
                //если одного из инг-тов нет, то выходим из проверки с отр. результатом
                if(!isContainCount(need.first,need.second))  {
                    result = false
                    return@forEach
                }
            }
            result
        }.forEach { bloc.invoke(it.value) }

    }

    private inline fun isContainCount(id: ObjectList, func: (countItem: Int) -> Boolean): Boolean {
        return if (inv.contain(id) == -1) false
        else func.invoke(inv.readAll()[inv.contain(id)].count)
    }
    private fun isContainCount(id: ObjectList, countItem: Int): Boolean {
        return if (inv.contain(id) == -1) false
        else inv.readAll()[inv.contain(id)].count>=countItem
    }

}