package dogengine.ecs.components.utility.logic

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.components.listeners.Box2DContactListener
import dogengine.ecs.components.listeners.Box2DContactListenerDef
import dogengine.ecs.def.ComponentResolver

class CPhysics2D : Component, Pool.Poolable {
    companion object : ComponentResolver<CPhysics2D>(CPhysics2D::class.java)
    var bodyType: BodyDef.BodyType = BodyDef.BodyType.DynamicBody
    var body: Body? = null
    var contactListener : Box2DContactListener = Box2DContactListenerDef()
    var defInfo = DefInfo()
    var dirty = true
    var postCreateTask : (() -> Unit)? = null

    override fun reset() {
        bodyType = BodyDef.BodyType.DynamicBody
        body = null
        contactListener = Box2DContactListenerDef()
        defInfo.reset()
        delete = false
    }

    class DefInfo {
        fun reset() {
            sensor = false
            fixedRotation = true
            density = 1f
            friction = 0.3f
            groupIndex = 0
            gravityForce = true
            shape=null
            restriction = 0.3f
        }

        var sensor = false
        var fixedRotation = true
        var density = 1f
        var friction = 0.3f
        var groupIndex : Short = 0
        var gravityForce: Boolean = true

        var shape: Shape?=null
        var restriction: Float = 0.3f
    }
    var delete = false
    fun deleteMe() {
        delete=true
    }

}