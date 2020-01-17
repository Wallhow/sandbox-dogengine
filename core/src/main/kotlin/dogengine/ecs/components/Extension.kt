package dogengine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import dogengine.PooledEntityCreate
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.sensors.Sensor
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import dogengine.utils.vec2

inline fun <reified T : Component> Entity.create(pe: Engine, init: T.() -> Unit) {
    val e = pe.createComponent(T::class.java)
    init(e)
    add(e)
}

inline fun <reified T : Component> Entity.create(init: T.() -> Unit) {
    val e = PooledEntityCreate.engine?.createComponent(T::class.java)
    init(e!!)
    add(e)
}

inline fun <reified T : Component> createComponent(init: T.() -> Unit): Component {
    val e = PooledEntityCreate.engine?.createComponent(T::class.java)
    init(e!!)
    return e
}

inline fun <reified T : Component> Entity.create() {
    val e = PooledEntityCreate.engine?.createComponent(T::class.java)
    add(e)
}

inline fun Engine.createEntity(init: (PooledEntityCreate.() -> Entity)): Entity {
    PooledEntityCreate.engine = this
    return init.invoke(PooledEntityCreate)
}

inline fun PooledEntityCreate.components(function: Entity.() -> Unit): Entity {
    val e = PooledEntityCreate.engine!!.createEntity()
    function.invoke(e)
    return e
}

fun Size.toVector2(): Vector2 {
    return vec2(width, height)
}

// DefaultPhysics
fun CDefaultPhysics2d.createBody(tr: CTransforms, x: Float, y: Float, width: Float, height: Float, types: Types.TYPE, name: String) : RectangleBody {
    offset.set(x, y)
    rectangleBody = RectangleBody(tr.position.x, tr.position.y, width, height, types, name)
     return rectangleBody!!
}

fun RectangleBody.createSensor() {
    this.addSensor(Sensor(this.name, 0f, 0f, this.getWidth(), this.getHeight()))
}