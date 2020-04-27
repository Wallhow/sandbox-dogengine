package sandbox

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.VisUI
import dogengine.DogeEngineGame
import dogengine.Kernel
import dogengine.ecs.systems.controllers.SInputHandler
import dogengine.ecs.systems.draw.SDrawDebug20
import dogengine.ecs.systems.physics.SDefaultPhysics2d
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.ecs.systems.update.SUpdate
import dogengine.ecs.systems.utility.SDeleteComponent
import dogengine.ecs.systems.utility.SDeleteMe
import dogengine.particles2d.EffectsManager
import dogengine.redkin.physicsengine2d.world.World
import dogengine.utils.GameCamera
import dogengine.utils.TTFFont
import ktx.style.set
import sandbox.sandbox.def.def.sys.SWorldHandler
import sandbox.sandbox.def.def.particles.EmitterManager
import sandbox.sandbox.def.def.sys.SExtraction
import sandbox.sandbox.def.def.sys.SParticleEmitter
import sandbox.sandbox.def.def.sys.SShack

typealias WorldDef = World

class MainSandbox : DogeEngineGame() {
    private val defWorld: WorldDef = WorldDef(0f)
    override lateinit var viewport: Viewport
    lateinit var gameCam: GameCamera
    private val effectsManager = EffectsManager()
    private lateinit var fnt: TTFFont
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    private val eManager = EmitterManager()


    override fun create() {
        fnt = TTFFont(R.pixel0)
        val skin = Skin()
        skin.add("default-font",fnt.get(24))
        skin.addRegions( TextureAtlas(Gdx.files.internal(("assets/ui/x2/uiskin.atlas"))));
        skin.load(Gdx.files.internal("assets/ui/x2/uiskin.json"));
        VisUI.load(skin);

        viewport = FitViewport(800f, 640f)
        viewport.apply()
        gameCam = GameCamera(viewport)


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
            add(SParticleEmitter::class.java)
        }
        Kernel.Systems.CameraLook.fixedBounds = false
        kernel.initialize {
            bind(Viewport::class.java).toInstance(viewport)
            bind(OrthographicCamera::class.java).toInstance(gameCam.getCamera())
            bind(World::class.java).toInstance(defWorld)
            bind(EffectsManager::class.java).toInstance(effectsManager)
            bind(TTFFont::class.java).toInstance(fnt)
            bind(EmitterManager::class.java).toInstance(eManager)
            bind(GameCamera::class.java).toInstance(gameCam)
        }

        setScreen(MainScreen(kernel.getInjector()))
        //setScreen(DefClass(kernel.getInjector()))
    }
}