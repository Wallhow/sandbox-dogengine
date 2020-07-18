package dogengine.utils.extension

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import dogengine.PooledEntityCreate

fun Engine.addEntity(init: (PooledEntityCreate.() -> Entity)) : Entity {
    val e = init.invoke(PooledEntityCreate)
    this.addEntity(e)
    return e
}