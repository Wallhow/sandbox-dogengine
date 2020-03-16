package dogengine.ecs.systems.physics

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.Inject
import dogengine.ecs.components.utility.logic.CPhysics2D
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority

class SPhysics2D @Inject constructor(val world: World) : IteratingSystem(Family.all(CPhysics2D::class.java).get()), EntityListener {
    companion object {
        var zoom = 100f
    }
    init {
        priority = SystemPriority.PHYSICS
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun entityRemoved(entity: Entity) {
        CPhysics2D[entity]?.let { i ->
            i.body?.let {
                world.destroyBody(it)
            }

        }
    }

    override fun entityAdded(entity: Entity?) {
    }

    fun createBodyInWorld(entity: Entity) {
        CPhysics2D[entity]?.let { ph ->
            if (ph.dirty) {
                ph.body?.let {
                    world.destroyBody(it)
                }
                ph.body = null
                ph.dirty = false
               println("created body")
                if (ph.body == null) {
                    val bodyDef = BodyDef()
                    val halfSize = Vector2(CTransforms[entity].size.halfWidth, CTransforms[entity].size.halfHeight)
                    bodyDef.type = ph.bodyType
                    bodyDef.fixedRotation = ph.defInfo.fixedRotation
                    bodyDef.position.set(CTransforms[entity].position.cpy().add(halfSize).apply {
                        set(x / zoom, y / zoom)
                    })
                    ph.body = world.createBody(bodyDef)
                    (ph.body as Body).apply {
                        setTransform(position.x, position.y
                                , CTransforms[entity].angle)
                        userData = entity
                        val fixture = FixtureDef().apply {
                            isSensor = ph.defInfo.sensor
                            isFixedRotation = ph.defInfo.fixedRotation
                            density = ph.defInfo.density
                            friction = ph.defInfo.friction
                            filter.groupIndex = ph.defInfo.groupIndex
                            restitution = ph.defInfo.restriction
                            if (!ph.defInfo.gravityForce) gravityScale = 0f
                        }
                        if(ph.defInfo.shape==null) {
                            val shape = PolygonShape()
                            shape.setAsBox(CTransforms[entity].size.halfWidth / zoom,
                                    CTransforms[entity].size.halfHeight / zoom)
                            fixture.shape = shape
                            createFixture(fixture)
                            shape.dispose()
                        }
                        else {
                            fixture.shape = ph.defInfo.shape
                            fixture.shape.radius = fixture.shape.radius/ zoom
                            createFixture(fixture)
                        }
                    }
                }
                if(ph.postCreateTask!=null) {
                    ph.postCreateTask?.invoke()
                }
            }
        }
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        createBodyInWorld(entity)
        val physics = CPhysics2D[entity]
        val halfSize = Vector2(CTransforms[entity].size.halfWidth, CTransforms[entity].size.halfHeight)
        val body = physics.body as Body
        val nx: Float
        val ny : Float
        val angle = if(!body.isFixedRotation)  MathUtils.radDeg*body.angle else 0f
        when {
            body.type == BodyDef.BodyType.DynamicBody -> {
                nx = body.position.x * zoom - halfSize.x
                ny = body.position.y * zoom - halfSize.y
                CTransforms[entity].position.set(nx, ny)
                CTransforms[entity].angle = angle
                body.apply {
                    transform.rotation = angle
                }
            }
            physics.body?.type == BodyDef.BodyType.KinematicBody -> {
                nx = (CTransforms[entity].position.x + halfSize.x)/zoom
                ny = (CTransforms[entity].position.y + halfSize.y)/zoom
                body.setTransform(nx, ny, angle)
            }
            physics.body?.type == BodyDef.BodyType.StaticBody -> {
                nx = (CTransforms[entity].position.x + halfSize.x)/zoom
                ny = (CTransforms[entity].position.y + halfSize.y)/zoom
                body.setTransform(nx,ny, 0f)
            }
        }
    }

}
