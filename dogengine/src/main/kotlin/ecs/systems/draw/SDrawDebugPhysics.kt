package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import dogengine.ecs.systems.physics.SPhysics2D

/**
 * Created by wallhow on 22.12.16.
 */
class SDrawDebugPhysics @Inject constructor(private val world: World, private val camera: OrthographicCamera): EntitySystem() {
    private val renderer = Box2DDebugRenderer(true,false,true,true,true,true)
    init {
        renderer.SHAPE_STATIC.set(Color.BLUE)
        priority= Int.MAX_VALUE

    }
    override fun update(deltaTime: Float) {
        val dzoom = camera.zoom
        camera.zoom = -SPhysics2D.zoom
        val c = camera.combined.cpy()
        renderer.render(world,c.scl(SPhysics2D.zoom))
        camera.zoom = dzoom
    }
}