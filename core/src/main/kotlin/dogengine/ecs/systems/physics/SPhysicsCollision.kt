package dogengine.ecs.systems.physics

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.Inject
import dogengine.ecs.components.events.CCollideEvent
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.SystemPriority
import dogengine.ecs.systems.physics.listeners.AllCollisionListener

class SPhysicsCollision @Inject constructor(world: World): EntitySystem(), ContactListener {
    var allCollision : AllCollisionListener? = null
    init {
        priority = SystemPriority.PHYSICS
        world.setContactListener(this)
    }

    override fun endContact(contact: Contact) {
        val entities = getEntities(contact)
        val collide1 = Collide(entities.second, entities.first, contact,TypeCollide.End)
        val collide2 = Collide(entities.first, entities.second, contact,TypeCollide.End)

        if(allCollision!=null) {
            allCollision?.endCollid(collide1)
        }
        entities.first.add(CCollideEvent(collide1))
        entities.second.add(CCollideEvent(collide2))

    }

    override fun beginContact(contact: Contact) {
        val entities = getEntities(contact)
        val collide1 = Collide(entities.second, entities.first, contact,TypeCollide.Begin)
        val collide2 = Collide(entities.first, entities.second, contact,TypeCollide.Begin)
        allCollision?.apply { beginCollid(collide1) }
        entities.first.add(CCollideEvent(collide1))
        entities.second.add(CCollideEvent(collide2))
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold?) {
        val entities = getEntities(contact)
        val collide1 = Collide(entities.second, entities.first, contact,TypeCollide.PreSolve)
        val collide2 = Collide(entities.first, entities.second, contact,TypeCollide.PreSolve)

        if(allCollision!=null) {
            allCollision?.preSolve(collide1)
        }
        entities.first.add(CCollideEvent(collide1))
        entities.second.add(CCollideEvent(collide2))
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse?) {
        val entities = getEntities(contact)
        val collide1 = Collide(entities.second, entities.first, contact,TypeCollide.PostSolve)
        val collide2 = Collide(entities.first, entities.second, contact,TypeCollide.PostSolve)

        if(allCollision!=null) {
            allCollision?.postSolve(collide1)
        }
        entities.first.add(CCollideEvent(collide1))
        entities.second.add(CCollideEvent(collide2))
    }

    private fun getEntities(contact: Contact) = Pair(
            contact.fixtureA.body.userData as GameEntity,
            contact.fixtureB.body.userData as GameEntity)
}