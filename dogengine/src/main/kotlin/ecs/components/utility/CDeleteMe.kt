package dogengine.ecs.components.utility

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

/**
 * Компонент маркер, помечает сущность для удаления, работает в кипе с
 * системой SDeleteMe
 */
class CDeleteMe : Component, Pool.Poolable {
    var task: (() -> Unit)? = null
    override fun reset() {
        task = null
    }

    companion object : ComponentResolver<CDeleteMe>(CDeleteMe::class.java)
}