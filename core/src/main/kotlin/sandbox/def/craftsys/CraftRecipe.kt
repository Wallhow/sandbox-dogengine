package sandbox.def.craftsys

/**
 * Рецепт крафта
 * @param name - видимое название рецепта
 * @param moreInfo - подробности о рецепте
 * @param conditions - условия при которых возможен крафт рецепта
 */
class CraftRecipe(val name: String, var moreInfo: String, private vararg val conditions: () -> Boolean) {
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