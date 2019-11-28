package sandbox

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Binder
import dogengine.DogeEngineGame
import dogengine.Kernel
import dogengine.ashley.systems.STiledMapOrtho
import dogengine.ashley.systems.draw.SDrawDebug
import sandbox.def.SJBumpAABB

class MainSandbox : DogeEngineGame() {
    override val viewport: Viewport
        get() = FitViewport(800f,640f,OrthographicCamera(800f, 640f))

    override fun resize(width: Int, height: Int) {
        viewport.update(width,height)
    }


    override fun create() {
        systems.apply {
            use(Kernel.DefSystems.GameObjects)
            use(Kernel.DefSystems.Controller)
            use(Kernel.DefSystems.CameraLook)

            add(STiledMapOrtho::class.java)
            add(SJBumpAABB::class.java)
            add(SDrawDebug::class.java)
        }

        kernel.initialize {
            bind(Viewport::class.java).toInstance(viewport)
            bind(OrthographicCamera::class.java).toInstance(viewport.camera as OrthographicCamera)
        }

        setScreen(MainScreen(kernel.getInjector()))
    }
}