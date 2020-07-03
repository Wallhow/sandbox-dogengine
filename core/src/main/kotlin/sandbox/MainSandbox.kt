package sandbox

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Binder
import com.kotcrab.vis.ui.VisUI
import dogengine.DogeEngineGame
import dogengine.Kernel
import dogengine.drawcore.DrawTypes
import dogengine.drawcore.SDraw2D
import dogengine.ecs.systems.controllers.SInputHandler
import dogengine.ecs.systems.draw.SDrawDebug20
import dogengine.ecs.systems.draw.SDrawable
import dogengine.ecs.systems.draw.SLightsBox2D
import dogengine.ecs.systems.physics.SDefaultPhysics2d
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.ecs.systems.update.SUpdate
import dogengine.ecs.systems.utility.SDeleteComponent
import dogengine.ecs.systems.utility.SDeleteMe
import dogengine.ecs.systems.utility.STime
import dogengine.particles2d.EffectsManager
import dogengine.redkin.physicsengine2d.world.World
import dogengine.shadow2d.systems.SShadow2D
import dogengine.utils.GameCamera
import dogengine.utils.TTFFont
import dogengine.utils.system
import dogengine.utils.vec2
import sandbox.sandbox.def.def.sys.SWorldHandler
import sandbox.def.particles.EmitterManager
import sandbox.sandbox.DefClass
import sandbox.sandbox.def.def.sys.SExtraction
import sandbox.sandbox.def.def.sys.SParticleEmitter
import sandbox.sandbox.def.def.sys.SShack
import sandbox.sandbox.drawfunctions.MyDrawBatchFunction

typealias WorldDef = World

class MainSandbox : DogeEngineGame() {
    private val defWorld: WorldDef = WorldDef(0f)
    override lateinit var viewport: Viewport
    lateinit var gameCam: GameCamera
    private val effectsManager = EffectsManager()
    private lateinit var fnt: TTFFont
    private val worldb2d = com.badlogic.gdx.physics.box2d.World(vec2(0f,-9f),false)

    override val systemConfigure: Kernel.Systems.() -> Unit = {
        use(Kernel.DefSystems.GameObjects)
        use(Kernel.DefSystems.Controller)
        use(Kernel.DefSystems.CameraLook)

        add(SShadow2D::class.java)
        //add(SDrawToFlexBatch::class.java)
        add(SDrawable::class.java)
        add(SDraw2D::class.java)
        add(STime::class.java)
        add(SDefaultPhysics2d::class.java)
        add(SLightsBox2D::class.java)
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

    override val injectConfigure: Binder.() -> Unit = {
        bind(Viewport::class.java).toInstance(viewport)
        bind(OrthographicCamera::class.java).toInstance(gameCam.getCamera())
        bind(World::class.java).toInstance(defWorld)
        bind(EffectsManager::class.java).toInstance(effectsManager)
        bind(TTFFont::class.java).toInstance(fnt)
        bind(EmitterManager::class.java).toInstance(eManager)
        bind(GameCamera::class.java).toInstance(gameCam)
        bind(com.badlogic.gdx.physics.box2d.World::class.java).toInstance(worldb2d)
    }


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
        gameCam.getCamera().far = -1f
        gameCam.getCamera().near = 1000f


        initialize(this)


        setScreen(MainScreen(injector))
        //setScreen(DefClass(injector))
    }
}