package sandbox

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import dogengine.DogeEngineGame
import dogengine.Kernel
import dogengine.ecs.systems.draw.SDrawDebug
import dogengine.ecs.systems.update.SUpdate
import dogengine.ecs.systems.utility.SDeleteComponent
import dogengine.ecs.systems.utility.SDeleteMe
import dogengine.utils.vec2
import sandbox.sandbox.def.map2D.SMap2D
import sandbox.sandbox.def.redkin.physicsengine2d.SDefaultPhysics2d

typealias WorldDef = dogengine.es.redkin.physicsengine2d.world.World
class MainSandbox : DogeEngineGame() {
    private val world: World = World(vec2(0f,0f),true)
    private val defWorld: WorldDef = WorldDef(0f)
    override val viewport: Viewport
        get() = FitViewport(800f,640f,OrthographicCamera(800f, 640f)) as Viewport

    override fun resize(width: Int, height: Int) {
        viewport.update(width,height)
    }


    override fun create() {
        systems.apply {
            use(Kernel.DefSystems.GameObjects)
            use(Kernel.DefSystems.Controller)
            use(Kernel.DefSystems.CameraLook)

            add(SDefaultPhysics2d::class.java)
            add(SUpdate::class.java)
            add(SMap2D::class.java)
            //add(SJBumpAABB::class.java)
            add(SDrawDebug::class.java)
            add(SDeleteMe::class.java)
            add(SDeleteComponent::class.java)
            //add(SDrawDebugPhysics::class.java)
        }
        Kernel.Systems.CameraLook.fixedBounds = false
        kernel.initialize {
            bind(Viewport::class.java).toInstance(viewport)
            bind(OrthographicCamera::class.java).toInstance(viewport.camera as OrthographicCamera)
            bind(World::class.java).toInstance(world)
            bind(dogengine.es.redkin.physicsengine2d.world.World::class.java).toInstance(defWorld)
        }
        setScreen(MainScreen(kernel.getInjector()))
    }
}