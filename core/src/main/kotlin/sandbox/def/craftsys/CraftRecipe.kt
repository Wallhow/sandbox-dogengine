package sandbox.def.craftsys

import sandbox.go.environment.ItemList

/**
 * Рецепт крафта
 * @param name - видимое название рецепта
 * @param moreInfo - подробности о рецепте
 * @param rules - условия при которых возможен крафт рецепта
 */
class CraftRecipe constructor(val name: String, var moreInfo: String,
                              val needItems: com.badlogic.gdx.utils.Array<Pair<ItemList,Int>>,
                              val rules: com.badlogic.gdx.utils.Array<() -> Boolean>) {
    var itemCraft: ItemList = ItemList.ZERO
    //проверка на возможность крафта рецепта
    fun isAvailable(): Boolean {
        var result = true
        rules.forEach {
            if(!it.invoke()) {
                result = false
                return@forEach
            }
        }
        return result
    }
}