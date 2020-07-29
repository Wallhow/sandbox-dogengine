package sandbox.dev.craftsys

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import sandbox.go.player.inventory.Inventory
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.objects.ObjectList

/**
 * Список возможных рецептов с их условиями
 */
class CraftRecipes(val inv: Inventory) {
    private val listRecipe: ArrayMap<String, CraftRecipe> = ArrayMap()

    init {
        recipe("лапа", "похожа на перчатку таноса, зачем мне её делать? Хотя ей можно кидаться") {
            itemsNeed {
                item(ItemList.ROCK,3)
                item(ItemList.WOOD,1)
            }
            rules {  }
            craftItem(ItemList.CANDY)
        }

        recipe("доска","я конечно попытаюсь, но столяр из меня так себе") {
            itemsNeed {
                item(ItemList.WOOD,2)
            }
            rules {
                create { isNearWorkbench(ObjectList.WORKBENCH) }
            }
            craftItem(ItemList.WOOD_PLANK)
        }


        recipe("печеное яблоко",
                "выглядит не очень, ненавижу печеные яблоки") {
            itemsNeed {
                item(ItemList.APPLE,1)
            }
            rules {
                create { isNearWorkbench(ObjectList.BONFIRE1) }
            }
            craftItem(ItemList.APPLE_EAT)
        }

        recipe("костер",
        "Благодаря костру я смогу погреться и приготовить себе еду!") {
            itemsNeed {
                item(ItemList.BRANCH,2)
                item(ItemList.WOOD,6)
            }
            rules { }
            craftItem(ItemList.BONFIRE)
        }
    }


    fun getRecipes(): Array<CraftRecipe> = listRecipe.values().toArray()
    fun getAvailableRecipes(bloc: (recipe: CraftRecipe) -> Unit) {

        listRecipe.forEach {
            if (isAvailableRecipe(it.value)) {
                bloc.invoke(it.value)
            }
        }
    }

    fun isAvailableRecipe(recipe: CraftRecipe): Boolean {
        var result = true
        recipe.needItems.forEach { need ->
            //если одного из инг-тов нет, то выходим из проверки с отр. результатом
            if (!recipe.isAvailable() || !isContainCount(need.first, need.second)) {
                result = false
                return@forEach
            }
        }
        return result
    }

    private fun recipe(name: String, moreInfo: String, init: RecipeDef.() -> Unit) {
        val recipeDef: RecipeDef = RecipeDef()
        init.invoke(recipeDef)
        val recipe = CraftRecipe(name,moreInfo,recipeDef.items_,recipeDef.rules_)
        recipe.craftItem(recipeDef.craftItem_)
        listRecipe.put(name,recipe)
    }

    private fun RecipeDef.itemsNeed(init: ItemsDef.() -> Unit) {
        val itemsDef: ItemsDef = ItemsDef()
        init.invoke(itemsDef)
        items_ = itemsDef.needItems
    }

    private fun ItemsDef.item(type: ItemList, countItem: Int) {
        needItems.add(Pair(type, countItem))
    }

    private fun RecipeDef.rules(arr: Rules.() -> Unit) {
        val rules = Rules()
        arr.invoke(rules)
        rules_ = rules.array
    }

    private fun RecipeDef.craftItem(itemList: ItemList) {
        craftItem_ = itemList
    }

    private class Rules(val array: Array<() -> Boolean> = Array())
    private class RecipeDef {
        lateinit var craftItem_: ItemList
        lateinit var rules_: Array<() -> Boolean>
        lateinit var items_: Array<Pair<ItemList, Int>>
    }

    private class ItemsDef() {
        val needItems: Array<Pair<ItemList, Int>> = Array()
    }

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