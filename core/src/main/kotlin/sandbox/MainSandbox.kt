package sandbox

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.dongbat.jbump.World
import dogengine.DogeEngineGame
import dogengine.Kernel
import sandbox.dogengine.ashley.systems.utility.SDeleteMe
import dogengine.ashley.systems.draw.SDrawDebug
import dogengine.ashley.systems.update.SUpdate
import sandbox.def.SJBumpAABB
import sandbox.def.SLabel
import sandbox.dogengine.ashley.systems.utility.SDeleteComponent
import sandbox.sandbox.def.map2D.SMap2DRenderer

class MainSandbox : DogeEngineGame() {
    private val world: World<Entity> = World()
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

            add(SUpdate::class.java)
            add(SMap2DRenderer::class.java)
            add(SJBumpAABB::class.java)
            add(SDrawDebug::class.java)
            add(SLabel::class.java)
            add(SDeleteMe::class.java)
            add(SDeleteComponent::class.java)
        }
        Kernel.Systems.CameraLook.fixedBounds = false
        kernel.initialize {
            bind(Viewport::class.java).toInstance(viewport)
            bind(OrthographicCamera::class.java).toInstance(viewport.camera as OrthographicCamera)
            bind(World::class.java).toInstance(world)
        }

        setScreen(MainScreen(kernel.getInjector()))
    }
}