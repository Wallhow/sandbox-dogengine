package sandbox

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.VisUI
import dogengine.DogeEngineGame
import dogengine.Kernel
import dogengine.ecs.systems.controllers.SInputHandler
import dogengine.ecs.systems.draw.SDrawDebug
import dogengine.ecs.systems.draw.SDrawDebug20
import dogengine.ecs.systems.physics.SDefaultPhysics2d
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.ecs.systems.update.SUpdate
import dogengine.ecs.systems.utility.SDeleteComponent
import dogengine.ecs.systems.utility.SDeleteMe
import dogengine.particles2d.EffectsManager
import dogengine.redkin.physicsengine2d.world.World
import dogengine.utils.TTFFont
import sandbox.def.SWorldHandler
import sandbox.sandbox.def.def.sys.SExtraction
import sandbox.sandbox.def.def.sys.SShack

typealias WorldDef = World
class MainSandbox : DogeEngineGame() {
    private val defWorld: WorldDef = WorldDef(0f)
    override val viewport: Viewport
        get() = FitViewport(800f,640f,OrthographicCamera(800f, 640f)) as Viewport
    private val effectsManager = EffectsManager()
    private lateinit var fnt : TTFFont
    override fun resize(width: Int, height: Int) {
        viewport.update(width,height)
    }


    override fun create() {
        fnt = TTFFont(R.pixel0)
        VisUI.load(VisUI.SkinScale.X2)

        systems.apply {
            use(Kernel.DefSystems.GameObjects)
            use(Kernel.DefSystems.Controller)
            use(Kernel.DefSystems.CameraLook)

            add(SDefaultPhysics2d::class.java)
            add(SUpdate::class.java)
            add(SInputHandler::class.java)
            add(SMap2D::class.java)
            add(SDrawDebug20::class.java)
            add(SWorldHandler::class.java)
            add(SDeleteMe::class.java)
            add(SDeleteComponent::class.java)
            add(SShack::class.java)
            add(SExtraction::class.java)
        }
        Kernel.Systems.CameraLook.fixedBounds = false
        kernel.initialize {
            bind(Viewport::class.java).toInstance(viewport)
            bind(OrthographicCamera::class.java).toInstance(viewport.camera as OrthographicCamera)
            bind(World::class.java).toInstance(defWorld)
            bind(EffectsManager::class.java).toInstance(effectsManager)
            bind(TTFFont::class.java).toInstance(fnt)
        }

        setScreen(MainScreen(kernel.getInjector()))
    }
}