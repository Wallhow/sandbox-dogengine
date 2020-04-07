package sandbox.def.craftsys

import sandbox.go.environment.ObjectList

/**
 * Рецепт крафта
 * @param name - видимое название рецепта
 * @param moreInfo - подробности о рецепте
 * @param conditions - условия при которых возможен крафт рецепта
 */
class CraftRecipe constructor(val name: String, var moreInfo: String,
                              val needItems: Array<Pair<ObjectList,Int>>,
                              private vararg val conditions: () -> Boolean) {
    var itemCraft: ObjectList = ObjectList.ZERO
    //проверка на возможность крафта рецепта
    fun isAvailable(): Boolean {
        var result = true
        conditions.forEach {
            if(!it.invoke()) {
                result = false
                return@forEach
            }
        }
        return result
    }
}