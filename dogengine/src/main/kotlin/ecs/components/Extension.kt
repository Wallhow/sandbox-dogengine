package dogengine.ecs.components

import com.badlogic.ashley.core.*
import com.badlogic.gdx.math.Vector2
import dogengine.PooledEntityCreate
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.tilemap.CMap2D
import dogengine.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.redkin.physicsengine2d.sensors.Sensor
import dogengine.redkin.physicsengine2d.variables.Types
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

inline fun Engine.isEntityAdded(crossinline func : (entity: Entity) -> Unit) : EntityListener {
    return object : EntityListener {
        override fun entityRemoved(entity: Entity?) {
        }

        override fun entityAdded(entity: Entity) {
            func(entity)
        }
    }
}

inline fun Engine.addEntityAddedListener(family: Family, crossinline func : (entity: Entity) -> Unit) {
    this.addEntityListener(family, this.isEntityAdded {
       func(it)
    })
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
fun CDefaultPhysics2d.createBody(tr: CTransforms, x: Float, y: Float, width : Float= tr.size.width, height: Float = tr.size.height, types: Types.TYPE = Types.TYPE.STATIC, name: String = "") : RectangleBody {
    offset.set(x, y)
    rectangleBody = RectangleBody(tr.position.x, tr.position.y, width, height, types, name)
     return rectangleBody!!
}

fun RectangleBody.createSensor() {
    this.addSensor(Sensor(this.name, 0f, 0f, this.getWidth(), this.getHeight()))
}