package dogengine.ecs.components.listeners

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Contact

interface Box2DContactListener {
    fun beginCollide(collideInfo: CollideInfo)
    fun endCollide(collideInfo: CollideInfo)
    data class CollideInfo(val first: Entity, val last : Entity, val contact: Contact)
}

class Box2DContactListenerDef : Box2DContactListener {
    override fun beginCollide(collideInfo: Box2DContactListener.CollideInfo) {
    }

    override fun endCollide(collideInfo: Box2DContactListener.CollideInfo) {
    }
}