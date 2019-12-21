package sandbox.dogengine.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import dogengine.PooledEntityCreate

inline fun <reified T: Component> Entity.create(pe: Engine, init: T.() -> Unit) {
    val e = pe.createComponent(T::class.java)
    init(e)
    add(e)
}
inline fun <reified T: Component> Entity.create(init: T.() -> Unit) {
    val e = PooledEntityCreate.engine?.createComponent(T::class.java)
    init(e!!)
    add(e)
}

inline fun Engine.createEntity(init : (PooledEntityCreate.() -> Entity)) : Entity {
    PooledEntityCreate.engine = this
    return init.invoke(PooledEntityCreate)
}

inline fun PooledEntityCreate.components(function: Entity.() -> Unit) : Entity {
    val e = PooledEntityCreate.engine!!.createEntity()
    function.invoke(e)
    return e
}
