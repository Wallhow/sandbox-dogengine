package dogengine.ecs.systems.draw

import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.components.utility.visible.CLightBox2D
import dogengine.ecs.systems.SystemPriority

class SLightsBox2D @Inject constructor(private val rayHandler: RayHandler, private val camera: OrthographicCamera, world: World): EntitySystem(), EntityListener {
    private lateinit var array : ImmutableArray<Entity>
    init {
        rayHandler.setAmbientLight(0.1f,0f,0.1f,0.3f)
        rayHandler.setBlur(true)
        rayHandler.setWorld(world)
        priority = SystemPriority.DRAW-6
    }
    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)

        array = engine.getEntitiesFor(Family.all(CLightBox2D::class.java).get())
    }

    override fun entityRemoved(entity: Entity) {
        CLightBox2D[entity]?.let {
            if(it.pointLight!=null) {
                it.pointLight?.dispose()
            }
            array = engine.getEntitiesFor(Family.all(CLightBox2D::class.java).exclude(CHide::class.java).get())
        }
    }
    override fun entityAdded(entity: Entity) {
        CLightBox2D[entity]?.let {
            if(it.pointLight==null) {
                it.pointLight = PointLight(rayHandler,128, Color.WHITE.cpy().apply {
                    r=1f
                    g =0.2f
                    b = 0.3f
                },it.radius, CTransforms[entity].position.x, CTransforms[entity].position.y)
            }
            array = engine.getEntitiesFor(Family.all(CLightBox2D::class.java).get())
        }
    }
    override fun update(deltaTime: Float) {
        rayHandler.setCombinedMatrix(camera)
        array.forEach {
            if(CLightBox2D[it].pointLight!=null) {
                CLightBox2D[it].pointLight?.position = CTransforms[it].position
                //CLightBox2D[it].pointLight!!.distance +=0.1f
            }
        }

        rayHandler.update()
        rayHandler.render()
    }
}